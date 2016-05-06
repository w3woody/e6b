/*  SpeedCalculation.java
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

package com.glenviewsoftware.e6b.calc.e6b.tds;

import java.util.List;

import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalcManager;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalculation;
import com.glenviewsoftware.e6b.units.Distance;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.units.Speed;
import com.glenviewsoftware.e6b.units.Time;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class SpeedCalculation implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Calculate Speed";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Given the distance traveled and the time traveled, determine the current speed.";
    }

    @Override
    public int getInputFieldCount()
    {
        return 2;
    }

    @Override
    public String getInputFieldName(int index)
    {
        switch (index) {
            default:
            case 0: return "Distance Traveled";
            case 1: return "Elapsed Time";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.distance;
            case 1: return Units.time;
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
            case 0: return "Speed";
        }
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.speed;
        }
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        
        a.loadStoreValue(CalcStorage.shared().elapsedDistance);
        b.loadStoreValue(CalcStorage.shared().elapsedTime);

        CalculatorInputView c = out.get(0);
        c.setUnit(CalcStorage.shared().currentSpeed.getUnit());
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        
        double av = a.getValueAsUnit(Distance.DISTANCE_NMILES);
        double bv = b.getValueAsUnit(Time.TIME_TIME);
        double v = av / bv;
        
        CalculatorInputView c = out.get(0);
        c.setValue(v, Speed.SPEED_KNOTS);
        
        if (store) {
            CalcStorage.shared().elapsedDistance = a.saveStoreValue();
            CalcStorage.shared().elapsedTime = b.saveStoreValue();
            CalcStorage.shared().currentSpeed = c.saveStoreValue();
        }
    }

}


