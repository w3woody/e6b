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

package com.glenviewsoftware.e6b.calc.e6b.triangle;

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

public class CrossWind implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Cross Wind Calculator";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Given a runway number, wind direction and speed, calculate the cross wind and headwind components.";
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
            case 0: return "Runway Number (1-36)";
            case 1: return "Wind Direction";
            case 2: return "Wind Speed";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0:
            case 1: return null;
            case 2: return Units.speed;
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
            case 0: return "- Left/+ Right Crosswind";
            case 1: return "- Headwind/+ Tailwind";
        }
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.speed;
            case 1: return Units.speed;
        }
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        a.loadStoreValue(CalcStorage.shared().runwayNumber);
        b.loadStoreValue(CalcStorage.shared().windDirection);
        c.loadStoreValue(CalcStorage.shared().windSpeed);

        a = out.get(0);
        b = out.get(1);
        a.setUnit(CalcStorage.shared().crosswind);
        b.setUnit(CalcStorage.shared().headwind);
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        if (store) {
            CalcStorage.shared().runwayNumber = a.saveStoreValue();
            CalcStorage.shared().windDirection = b.saveStoreValue();
            CalcStorage.shared().windSpeed = c.saveStoreValue();
        }
        
        double av = 10 * a.getValue() + 180;
        double bv = b.getValue();
        double cv = c.getValueAsUnit(Speed.SPEED_KNOTS);
        
        /*
         * Calculate cross wind
         */

        double r = (av-bv);
        r *= Math.PI / 180;
        double x = Math.cos(r) * cv;
        double y = Math.sin(r) * cv;
        
        a = out.get(0);
        b = out.get(1);
        
        a.setValue(y,Speed.SPEED_KNOTS);
        b.setValue(x,Speed.SPEED_KNOTS);
        
        if (store) {
            CalcStorage.shared().crosswind = a.getUnit();
            CalcStorage.shared().headwind = b.getUnit();
        }
    }
}


