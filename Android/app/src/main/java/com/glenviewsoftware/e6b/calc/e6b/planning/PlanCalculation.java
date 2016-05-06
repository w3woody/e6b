/*  PlanCalculation.java
 *
 *  Created on Dec 24, 2012 by William Edward Woody
 */
/*	E6B: Calculator software for pilots.
 *
 *	Copyright © 2016 by William Edward Woody
 *
 *	This program is free software: you can redistribute it and/or modify it
 *	under the terms of the GNU General Public License as published by the
 *	Free Software Foundation, either version 3 of the License, or (at your
 *	option) any later version.
 *
 *	This program is distributed in the hope that it will be useful, but
 *	WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *	or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *	for more details.
 *
 *	You should have received a copy of the GNU General Public License along
 *	with this program. If not, see <http://www.gnu.org/licenses/>
 */

package com.glenviewsoftware.e6b.calc.e6b.planning;

import java.util.List;

import android.util.Log;

import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalcManager;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalculation;
import com.glenviewsoftware.e6b.units.Distance;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.units.Speed;
import com.glenviewsoftware.e6b.units.Time;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.units.Volume;
import com.glenviewsoftware.e6b.units.VolumeBurn;
import com.glenviewsoftware.e6b.utils.Util;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class PlanCalculation implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Flight Planner";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "This handles all the calculations necessary to plan a leg of your flight. " +
        		"Given the aircraft's true speed, desired (ground) true course, wind speed " +
        		"and direction, leg distance, magnetic variation and fuel burn rate, " +
        		"calculates the true heading, magnetic course, magnetic heading, leg " +
        		"time, and fuel burn amount for that leg of flight.";
    }

    @Override
    public int getInputFieldCount()
    {
        return 7;
    }

    @Override
    public String getInputFieldName(int index)
    {
        switch (index) {
            default:
            case 0: return "(Ground) True Course";
            case 1: return "(Ground) Distance";
            case 2: return "True Air Speed";
            case 3: return "Wind Direction";
            case 4: return "Wind Speed";
            case 5: return "Fuel Burn Rate";
            case 6: return "Magnetic Variation (+W/-E)";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return null;
            case 1: return Units.distance;
            case 2: return Units.speed;
            case 3: return null;
            case 4: return Units.speed;
            case 5: return Units.volumeBurn;
            case 6: return null;
        }
    }

    @Override
    public int getOutputFieldCount()
    {
        return 6;
    }

    @Override
    public String getOutputFieldName(int index)
    {
        switch (index) {
            default:
            case 0: return "(Aircraft) True Heading";
            case 1: return "(Ground) Magnetic Course";
            case 2: return "(Aircraft) Magnetic Heading";
            case 3: return "Ground Speed";
            case 4: return "Estimated Time of Arrival";
            case 5: return "Fuel Required";
        }
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return null;
            case 1: return null;
            case 2: return null;
            case 3: return Units.speed;
            case 4: return Units.time;
            case 5: return Units.volume;
        }
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        in.get(0).loadStoreValue(CalcStorage.shared().groundCourse);
        in.get(1).loadStoreValue(CalcStorage.shared().elapsedDistance);
        in.get(2).loadStoreValue(CalcStorage.shared().trueAirSpeed);
        in.get(3).loadStoreValue(CalcStorage.shared().windDirection);
        in.get(4).loadStoreValue(CalcStorage.shared().windSpeed);
        in.get(5).loadStoreValue(CalcStorage.shared().currentBurn);
        in.get(6).loadStoreValue(CalcStorage.shared().magVariation);
        
        out.get(0).setUnit(0);
        out.get(1).setUnit(0);
        out.get(2).setUnit(0);
        out.get(3).setUnit(CalcStorage.shared().groundSpeed.getUnit());
        out.get(4).setUnit(0);
        out.get(5).setUnit(CalcStorage.shared().currentVolume.getUnit());
    }
    
    public static class FHResult
    {
        public final double gs;
        public final double th;
        
        FHResult(double g, double t)
        {
            gs = g;
            th = t;
        }
    }

    /**
     * Find the heading givne the true air speed, true course, wind speed and wind direction,
     * find the ground speed and true heading of the aircraft.
     * @param tc
     * @param tas
     * @param wd
     * @param ws
     * @return
     */
    public static FHResult FindHeading(double tc, double tas, double wd, double ws)
    {
        double ca,gs;
        
        tc = tc * Math.PI / 180.0;         /* Convert to radians */
        wd = (wd + 180) * Math.PI / 180.0; /* Convert to vector in radians */
        
        double sc = ws * Math.sin(tc - wd) / tas;
        if ((sc < -1) || (sc > 1)) {
            /*
             *  We are unable to travel fast enough.
             */
            
            ca = Double.POSITIVE_INFINITY;
            gs = Double.POSITIVE_INFINITY;
        } else {
            /*
             *  Compute course corretion angle
             */
            
            sc = Math.asin(sc);
            ca = tc + sc;
            
            /*
             *  Step 2: find ground speed given direction
             */
            
            double ax = Math.sin(wd) * ws + Math.sin(ca) * tas;
            double ay = Math.cos(wd) * ws + Math.cos(ca) * tas;
            
            ca *= 180 / Math.PI;
            ca = Util.fixAngle(ca);
            double ar = Util.fixAngle(Math.atan2(ax,ay)*180.0/Math.PI);
            Log.d("E6B",ca + " - " + ar);
            
            gs = Math.sqrt(ax * ax + ay * ay);
        }
        
        return new FHResult(gs,ca);
    }


    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> output, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        CalculatorInputView d = in.get(3);
        CalculatorInputView e = in.get(4);
        CalculatorInputView f = in.get(5);
        CalculatorInputView g = in.get(6);
        
        if (store) {
            CalcStorage.shared().groundCourse = a.saveStoreValue();
            CalcStorage.shared().elapsedDistance = b.saveStoreValue();
            CalcStorage.shared().trueAirSpeed = c.saveStoreValue();
            CalcStorage.shared().windDirection = d.saveStoreValue();
            CalcStorage.shared().windSpeed = e.saveStoreValue();
            CalcStorage.shared().currentBurn = f.saveStoreValue();
            CalcStorage.shared().magVariation = g.saveStoreValue();
        }
        
        double tc = a.getValue();
        double ed = b.getValueAsUnit(Distance.DISTANCE_NMILES);
        double tas = c.getValueAsUnit(Speed.SPEED_KNOTS);
        double wd = d.getValue();
        double ws = e.getValueAsUnit(Speed.SPEED_KNOTS);
        double fb = f.getValueAsUnit(VolumeBurn.VOLBURN_GALHR);
        double mv = g.getValue();

        /*
         *  Steal calculations from other calculators.
         *
         *  Step 1: Given tc, tac, ws and wd, calculate th and gs
         */
        
        FHResult rs = FindHeading(tc,tas,wd,ws);
        
        /*
         *  Now calculate the ETA and required fuel
         */
        
        double eta = ed / rs.gs;        /* eta in hours */
        double fuel = fb * eta;
        
        /*
         *  Headings
         */
        
        double mc = Util.fixAngle(tc + mv);
        double mh = Util.fixAngle(rs.th + mv);
        
        /*
         *  Store results
         */
        
        CalculatorInputView out;
        out = output.get(0);
        out.setValue(rs.th);
        out = output.get(1);
        out.setValue(mc);
        out = output.get(2);
        out.setValue(mh);
        out = output.get(3);
        out.setValue(rs.gs,Speed.SPEED_KNOTS);
        if (store) {
            CalcStorage.shared().groundSpeed = out.saveStoreValue();
        }
        out = output.get(4);
        out.setValue(eta,Time.TIME_TIME);
        out = output.get(5);
        out.setValue(fuel,Volume.VOLUME_GALLONS);
        if (store) {
            CalcStorage.shared().currentVolume = out.saveStoreValue();
        }
    }

}


