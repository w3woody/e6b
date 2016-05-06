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

import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalcManager;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalculation;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.units.Speed;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.units.Weight;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class ManeuveringSpeed implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Va for Weight";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Given the airplane's max gross rated weight and the Va (maneuvering " +
        		"speed) for that weight, and given the airplane's current weight, " +
        		"calculate Va for the current weight.";
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
            case 0: return "Rated Va at Max Weight";
            case 1: return "Max Weight";
            case 2: return "Current Weight";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            case 0: return Units.speed;
            default: return Units.weight;
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
        return "Current Va";
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        return Units.speed;
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        a.loadStoreValue(CalcStorage.shared().manuveurWeight);
        b.loadStoreValue(CalcStorage.shared().maxWeight);
        c.loadStoreValue(CalcStorage.shared().currentWeight);

        a = out.get(0);
        a.setUnit(CalcStorage.shared().curManuveurWeight);
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        CalculatorInputView c = in.get(2);
        
        if (store) {
            CalcStorage.shared().manuveurWeight = a.saveStoreValue();
            CalcStorage.shared().maxWeight = b.saveStoreValue();
            CalcStorage.shared().currentWeight = c.saveStoreValue();
        }
        
        double va = a.getValueAsUnit(Speed.SPEED_KNOTS);
        double mw = b.getValueAsUnit(Weight.WEIGHT_LBS);
        double cw = c.getValueAsUnit(Weight.WEIGHT_LBS);
        
        double cva = va * Math.sqrt(cw/mw);

        a = out.get(0);
        a.setValue(cva,Speed.SPEED_KNOTS);
        if (store) {
            CalcStorage.shared().curManuveurWeight = a.getUnit();
        }
    }

}


