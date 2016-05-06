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
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.units.Volume;
import com.glenviewsoftware.e6b.units.Weight;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class OneFuelForWeight implements E6BCalculation
{
    private String fName;
    private String fDesc;
    private double fLbsPerGal;

    private Value fInValue;
    private int fOutUnit;

    public OneFuelForWeight(String name, String desc, double d)
    {
        fName = name;
        fDesc = desc;
        fLbsPerGal = d;
        
        fInValue = new Value(Weight.WEIGHT_LBS);
        fOutUnit = Volume.VOLUME_GALLONS;
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
        return "Weight";
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        return Units.weight;
    }

    @Override
    public int getOutputFieldCount()
    {
        return 1;
    }

    @Override
    public String getOutputFieldName(int index)
    {
        return "Volume";
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        return Units.volume;
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
        
        double a = vin.getValueAsUnit(Weight.WEIGHT_LBS);
        double v = a / fLbsPerGal;
        vout.setValue(v, Volume.VOLUME_GALLONS);
        
        if (store) {
            fInValue = vin.saveStoreValue();
            fOutUnit = vout.getUnit();
        }
    }

}


