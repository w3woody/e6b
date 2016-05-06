/*  E6BCalculation.java
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

package com.glenviewsoftware.e6b.calc.e6b;

import java.util.List;

import com.glenviewsoftware.e6b.calc.Calculation;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.view.CalculatorInputView;

public interface E6BCalculation extends Calculation
{
    String getCalculationDescription();

    int getInputFieldCount();
    String getInputFieldName(int index);
    Measurement getInputFieldUnit(int index);
    
    int getOutputFieldCount();
    String getOutputFieldName(int index);
    Measurement getOutputFieldUnit(int index);
    
    void calculatorInitialize(List<CalculatorInputView> in, List<CalculatorInputView> out);
    void calculate(List<CalculatorInputView> in, List<CalculatorInputView> out, boolean store);
}


