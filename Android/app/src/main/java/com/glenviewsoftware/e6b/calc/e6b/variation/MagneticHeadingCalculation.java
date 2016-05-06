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

package com.glenviewsoftware.e6b.calc.e6b.variation;

import java.util.List;

import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalcManager;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalculation;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.utils.Util;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class MagneticHeadingCalculation implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Magnetic Course/Heading";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Convert True Course/Heading to Magnetic Course/Heading.";
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
            case 0: return "True Course/Heading";
            case 1: return "Magnetic Variation (+W/-E)";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        return null;
    }

    @Override
    public int getOutputFieldCount()
    {
        return 1;
    }

    @Override
    public String getOutputFieldName(int index)
    {
        return "Magnetic Course/Heading";
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        return null;
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        in.get(0).loadStoreValue(CalcStorage.shared().groundCourse);
        in.get(1).loadStoreValue(CalcStorage.shared().magVariation);

        out.get(0).setUnit(0);
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        
        if (store) {
            CalcStorage.shared().groundCourse = a.saveStoreValue();
            CalcStorage.shared().magVariation = b.saveStoreValue();
        }
        
        double tc = a.getValue();
        double mv = b.getValue();
        double th = Util.fixAngle(tc + mv);
        
        a = out.get(0);
        a.setValue(th);
    }

}


