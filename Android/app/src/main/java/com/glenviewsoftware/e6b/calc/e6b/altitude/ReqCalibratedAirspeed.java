/*  PDAltCalculation.java
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

package com.glenviewsoftware.e6b.calc.e6b.altitude;

import java.util.List;

import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalcManager;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalculation;
import com.glenviewsoftware.e6b.units.Distance;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.units.Pressure;
import com.glenviewsoftware.e6b.units.Speed;
import com.glenviewsoftware.e6b.units.Temperature;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class ReqCalibratedAirspeed implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Required Calibrated Airspeed";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Given your desired true airspeed, current altitude and the outside observed temperature, find the calibrated airspeed and current density altitude for your aircraft.";
    }

    @Override
    public int getInputFieldCount()
    {
        return 3;
    }

    @Override
    public String getInputFieldName(int index)
    {
        switch (index) {
            default:
            case 0: return "Pressure Altitude";
            case 1: return "Outside Temperature";
            case 2: return "True Air Speed";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.distance;
            case 1: return Units.temperature;
            case 2: return Units.speed;
        }
    }

    @Override
    public int getOutputFieldCount()
    {
        return 3;
    }

    @Override
    public String getOutputFieldName(int index)
    {
        switch (index) {
            default:
            case 0: return "Calibrated Air Speed";
            case 1: return "Mach Number";
            case 2: return "Density Altitude";
        }
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.speed;
            case 1: return null;
            case 2: return Units.distance;
        }
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        a.loadStoreValue(CalcStorage.shared().pressureAltitude);
        b.loadStoreValue(CalcStorage.shared().outsideTemperature);
        c.loadStoreValue(CalcStorage.shared().trueAirSpeed);

        a = out.get(0);
        b = out.get(1);
        c = out.get(2);
        a.setUnit(CalcStorage.shared().calibratedAirSpeed.getUnit());
        b.setUnit(0);
        c.setUnit(CalcStorage.shared().densityAltitude);
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        if (store) {
            CalcStorage.shared().pressureAltitude = a.saveStoreValue();
            CalcStorage.shared().outsideTemperature = b.saveStoreValue();
            CalcStorage.shared().trueAirSpeed = c.saveStoreValue();
        }
        
        double av = a.getValueAsUnit(Distance.DISTANCE_FEET);
        double bv = b.getValueAsUnit(Temperature.TEMP_KELVIN);
        double cv = c.getValueAsUnit(Speed.SPEED_KNOTS);

        /*
         *  Step 1: Figure out station pressure given pressure altitude.
         *
         *  http://www.nwstc.noaa.gov/DATAACQ/d.ALGOR/d.PRES/PRESalgoProcessW8.html
         */
        
        double tmp = Math.pow(29.92,0.1903);     /* Pressure altitude with constant sea level */
        tmp = tmp - 1.313e-5 * av;
        double p = Math.pow(tmp, 5.255);

        /*
         *  Step 2: Calculate density altitude
         *
         *      http://en.wikipedia.org/wiki/Density_altitude
         */
        
        double da = (p/29.92)/(bv/288.15);
        da = 1-Math.pow(da,0.234969);
        da *= 145442.156;

        /*
         *  Step 3: Calculate the mach number from true airspeed. This is
         *  related to the equivalent airspeed.
         */
        
        double tr = Math.sqrt(bv/288.15);    /* sqrt(T/T0) */
        double es = cv / tr;            /* equivalent airspeed */
        double m = es / 661.4788;       /* Mach number */
        
        /*
         *  Step 4: Calculate impact pressure on the pitot tube. Derived from
         *  the mach number
         */
        
        tmp = (m * m)/5 + 1;
        tmp = Math.pow(tmp,7.0/2.0);
        double qc = (tmp - 1) * p;
        
        /*
         *  Step 5: From the impact pressure on the pitot tube, calculate calibrated
         *  airspeed
         */
        
        double cs = Math.pow(qc/29.92126 + 1,2.0/7.0);
        cs = 661.4788 * Math.sqrt(5*(cs - 1));


        /*
         * Slam values
         */
        
        a = out.get(0);
        b = out.get(1);
        c = out.get(2);
        
        a.setValue(cs,Speed.SPEED_KNOTS);
        b.setValue(m);
        c.setValue(da,Distance.DISTANCE_FEET);
        
        if (store) {
            CalcStorage.shared().calibratedAirSpeed = a.saveStoreValue();
            CalcStorage.shared().densityAltitude = c.getUnit();
        }
    }

}


