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
import com.glenviewsoftware.e6b.units.Temperature;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class CloudBaseCalculator implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Cloud Base Estimator";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Given the current reported airport temperature, dew point, and the airport's elevation, calculates the approximate cloud base MSL.";
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
            case 0: return "Ground Temperature";
            case 1: return "Dew Point Temperature";
            case 2: return "Field Elevation";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.temperature;
            case 1: return Units.temperature;
            case 2: return Units.distance;
        }
    }

    @Override
    public int getOutputFieldCount()
    {
        return 1;
    }

    @Override
    public String getOutputFieldName(int index)
    {
        switch (index) {
            default:
            case 0: return "Cloud Base Altitude";
        }
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.distance;
        }
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        a.loadStoreValue(CalcStorage.shared().groundTemperature);
        b.loadStoreValue(CalcStorage.shared().dewPointTemperature);
        c.loadStoreValue(CalcStorage.shared().fieldElevation);
        
        a = out.get(0);
        a.setUnit(CalcStorage.shared().cloudBase);
    }
    
    /** Calculate density altitude, pressure altitude using equations at
     *
     *      http://wahiduddin.net/calc/density_altitude.htm
     *  and http://en.wikipedia.org/wiki/Density_altitude
     */

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);

        if (store) {
            CalcStorage.shared().groundTemperature = a.saveStoreValue();
            CalcStorage.shared().dewPointTemperature = b.saveStoreValue();
            CalcStorage.shared().fieldElevation = c.saveStoreValue();
        }
        
        double av = a.getValueAsUnit(Temperature.TEMP_CELSIUS);
        double bv = b.getValueAsUnit(Temperature.TEMP_CELSIUS);
        double cv = c.getValueAsUnit(Distance.DISTANCE_FEET);
        
        /*
         *  Source: http://en.wikipedia.org/wiki/Cloud_base
         */
        
        double cl = cv + (av - bv) * 400;
        
        /*
         *  Populate results
         */
        
        a = out.get(0);
        a.setValue(cl,Distance.DISTANCE_FEET);
        
        if (store) {
            CalcStorage.shared().cloudBase = a.getUnit();
        }
    }

}


