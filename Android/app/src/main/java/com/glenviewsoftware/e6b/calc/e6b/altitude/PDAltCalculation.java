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
import com.glenviewsoftware.e6b.units.Temperature;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class PDAltCalculation implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Pressure/Density Altitude";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Given your altimeter settings and the observed outside temperature, find the density altitude and pressure altitude.";
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
            case 0: return "Indicated Altitude";
            case 1: return "Barometer Setting";
            case 2: return "Outside Temperature";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.distance;
            case 1: return Units.pressure;
            case 2: return Units.temperature;
        }
    }

    @Override
    public int getOutputFieldCount()
    {
        return 2;
    }

    @Override
    public String getOutputFieldName(int index)
    {
        switch (index) {
            default:
            case 0: return "Density Altitude";
            case 1: return "Pressure Altitude";
        }
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        return Units.distance;
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        a.loadStoreValue(CalcStorage.shared().indicatedAltitude);
        b.loadStoreValue(CalcStorage.shared().barometerSetting);
        c.loadStoreValue(CalcStorage.shared().outsideTemperature);

        a = out.get(0);
        b = out.get(1);
        a.setUnit(CalcStorage.shared().densityAltitude);
        b.setUnit(CalcStorage.shared().pressureAltitude.getUnit());
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        if (store) {
            CalcStorage.shared().indicatedAltitude = a.saveStoreValue();
            CalcStorage.shared().barometerSetting = b.saveStoreValue();
            CalcStorage.shared().outsideTemperature = c.saveStoreValue();
        }
        
        double av = a.getValueAsUnit(Distance.DISTANCE_FEET);
        double bv = b.getValueAsUnit(Pressure.PRESSURE_INHG);
        double cv = c.getValueAsUnit(Temperature.TEMP_KELVIN);
        
        
        /*
         *  Step 1: derive station pressure based on altimeter settings.
         *  Equation source: 
         *
         *  http://www.nwstc.noaa.gov/DATAACQ/d.ALGOR/d.PRES/PRESalgoProcessW8.html
         */
        
        double ea = Math.pow(bv,0.1903); /* bv: altimeter setting */
        ea -= 1.313e-5 * av;        /* av: station elevation */
        double pr = Math.pow(ea,5.255);  /* pa: actual station pressure */
        
        /*
         *  Step 2: derive density altitude in feet
         */
        
        double da = (pr/29.92) / (cv/288.15);   /* cv: temperature */
        da = 1 - Math.pow(da,0.234969);
        da = 145442.156 * da;
        
        /*
         *  Step 3: derive pressure altitude from station pressure. Basically
         *  work step 1 backwards but with 29.92 imHG for pressure. Note we
         *  still have 'ea' as an intermediate value.
         */
        
        double pa = Math.pow(29.92,0.1903) - ea; /* pa = ea ^ 5.255 */
        pa /= 1.313e-5;


        /*
         * Slam values in
         */
        a = out.get(0);
        b = out.get(1);

        a.setValue(da, Distance.DISTANCE_FEET);
        b.setValue(pa, Distance.DISTANCE_FEET);
        
        if (store) {
            CalcStorage.shared().densityAltitude = a.getUnit();
            CalcStorage.shared().pressureAltitude = b.saveStoreValue();
        }
    }

}


