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

package com.glenviewsoftware.e6b.calc.e6b.convert;

import java.util.List;

import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.Value;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalcManager;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalculation;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class OneUnitCalculator implements E6BCalculation
{
    private String fName;
    private String fDesc;
    private Measurement fUnit;

    private Value fInValue;
    private int fOutUnit;

    public OneUnitCalculator(String name, String desc, Measurement unit)
    {
        fName = name;
        fDesc = desc;
        fUnit = unit;
        
        fInValue = new Value(fUnit.standardUnit());
        fOutUnit = 0;
    }

    @Override
    public String getCalculationName()
    {
        return fName;
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return fDesc;
    }

    @Override
    public int getInputFieldCount()
    {
        return 1;
    }

    @Override
    public String getInputFieldName(int index)
    {
        return "Input Value";
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        return fUnit;
    }

    @Override
    public int getOutputFieldCount()
    {
        return 1;
    }

    @Override
    public String getOutputFieldName(int index)
    {
        return "Output Value";
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        return fUnit;
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        in.get(0).loadStoreValue(fInValue);
        out.get(0).setUnit(fOutUnit);
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView vin = in.get(0);
        CalculatorInputView vout = out.get(0);
        
        vout.setValue(vin.getValue(), vin.getUnit());
        
        if (store) {
            fInValue = vin.saveStoreValue();
            fOutUnit = vout.getUnit();
        }
    }

}


