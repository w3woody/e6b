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

package com.glenviewsoftware.e6b.calc.e6b.fuel;

import java.util.List;

import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalcManager;
import com.glenviewsoftware.e6b.calc.e6b.E6BCalculation;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.units.Time;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.units.Volume;
import com.glenviewsoftware.e6b.units.VolumeBurn;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public class FuelEndurance implements E6BCalculation
{

    @Override
    public String getCalculationName()
    {
        return "Fuel Endurance";
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new E6BCalcManager(this);
    }

    @Override
    public String getCalculationDescription()
    {
        return "Given the current volume of fuel and the current fuel burn rate, " +
        		"calculate the amount of time left before fuel exhaustion.\n\n" +
        		"Note that this simply calculates the amount of time it will take " +
        		"to consume the volume of fuel specified at the burn rate given. " +
        		"This does not take into account varying fuel requirements for " +
        		"climbing to altitude, run-up time, or factors in the required " +
        		"30 minute buffer (daytime) or 45 minute buffer (nighttime) safety factor.";
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
            case 0: return "Fuel Amount";
            case 1: return "Fuel Burn Rate";
        }
    }

    @Override
    public Measurement getInputFieldUnit(int index)
    {
        switch (index) {
            default:
            case 0: return Units.volume;
            case 1: return Units.volumeBurn;
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
        return "Fuel Endurance";
    }

    @Override
    public Measurement getOutputFieldUnit(int index)
    {
        return Units.time;
    }

    @Override
    public void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        
        a.loadStoreValue(CalcStorage.shared().currentVolume);
        b.loadStoreValue(CalcStorage.shared().currentBurn);

        CalculatorInputView c = out.get(0);
        c.setUnit(CalcStorage.shared().elapsedTime.getUnit());
    }

    @Override
    public void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store)
    {
        CalculatorInputView a = in.get(0);
        CalculatorInputView b = in.get(1);
        
        double av = a.getValueAsUnit(Volume.VOLUME_GALLONS);
        double bv = b.getValueAsUnit(VolumeBurn.VOLBURN_GALHR);
        double v = av / bv;
        
        CalculatorInputView c = out.get(0);
        c.setValue(v, Time.TIME_TIME);
        
        if (store) {
            CalcStorage.shared().currentVolume = a.saveStoreValue();
            CalcStorage.shared().currentBurn = b.saveStoreValue();
            CalcStorage.shared().elapsedTime = c.saveStoreValue();
        }
    }

}


