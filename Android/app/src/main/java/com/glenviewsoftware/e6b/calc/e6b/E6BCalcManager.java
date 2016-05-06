/*  E6BCalcManager.java
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

import java.util.ArrayList;

import android.view.View;
import android.widget.RelativeLayout;

import com.glenviewsoftware.e6b.calc.CalcDelegate;
import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.view.CalculatorInputView;
import com.glenviewsoftware.e6b.view.DescriptionView;

public class E6BCalcManager extends CalcManager
{
    private int yPos;
    private DescriptionView fLabel;
    private ArrayList<CalculatorInputView> fInput;
    private ArrayList<CalculatorInputView> fOutput;
    private CalculatorInputView.InputViewCallback fCallback;
    private CalcDelegate fDelegate;
    private E6BCalculation fCalculation;
    private float fScale;
    private int fTop;

    /**
     * Internal construction of calculation manager
     * @param calc
     */
    public E6BCalcManager(E6BCalculation calc)
    {
        fCalculation = calc;
    }

    @Override
    public void setDelegate(CalcDelegate del)
    {
        fDelegate = del;
    }

    @Override
    public void setInputCallback(CalculatorInputView.InputViewCallback callback)
    {
        fCallback = callback;
    }

    @Override
    public void constructCalcViews(RelativeLayout frame)
    {
        frame.removeAllViews();

        /*
         * Add layout, input views, output views
         */

        fLabel = new DescriptionView(frame.getContext());
        addView(fLabel,frame);
        fLabel.setDescription(fCalculation.getCalculationDescription());

        fInput = new ArrayList<CalculatorInputView>();
        int len = fCalculation.getInputFieldCount();
        for (int i = 0; i < len; ++i) {
            CalculatorInputView v = createInputView(frame);
            v.setLabel(fCalculation.getInputFieldName(i));
            v.setMeasurement(fCalculation.getInputFieldUnit(i));
            v.setEditable(true);
            v.setCallback(fCallback);
            fInput.add(v);
        }

        fOutput = new ArrayList<CalculatorInputView>();
        len = fCalculation.getOutputFieldCount();
        for (int i = 0; i < len; ++i) {
            CalculatorInputView v = createInputView(frame);
            v.setLabel(fCalculation.getOutputFieldName(i));
            v.setMeasurement(fCalculation.getOutputFieldUnit(i));
            v.setCallback(fCallback);
            v.setEditable(false);
            fOutput.add(v);
        }

        fCalculation.calculatorInitialize(fInput, fOutput);
        fCalculation.calculate(fInput, fOutput, false);
    }

    @Override
    public void layoutCalcViews(RelativeLayout frame)
    {
        fScale = frame.getResources().getDisplayMetrics().density;
        yPos = layoutLabel(frame);

        startInput(frame);
        for (CalculatorInputView v: fInput) {
            layoutInput(frame,v);
        }
        startOutput(frame);
        for (CalculatorInputView v: fOutput) {
            layoutOutput(frame,v);
        }

        updateCalculation();
    }

    @Override
    public void updateCalculation()
    {
        fCalculation.calculate(fInput, fOutput, true);
    }

    /*
     * The following aid in laying out the views
     */

    private int layoutLabel(RelativeLayout layout)
    {
//        int w = layout.getWidth();
//        int h = fLabel.getHeight(w-20);
//        setLocation(fLabel,10,10,w-20,h);
        final int w = (int)(layout.getWidth() - 40 * fScale);  /* Padding = 20 */
        int h = fLabel.getHeight(w-20);
        setLocation(fLabel,10,10,w-20,h);
        return 20 + h;
    }

    private void startInput(RelativeLayout layout)
    {
        fTop = yPos;
    }

    private void layoutInput(RelativeLayout layout, View view)
    {
        int w = layout.getWidth() - 20;
        if (isWide(layout)) {
            w = (w - 10)/2;
        }
        int h = (int)(60 * fScale);
        setLocation(view,10,yPos,w,h);
        yPos += h;
    }

    private void startOutput(RelativeLayout layout)
    {
        if (isWide(layout)) {
//            int w = layout.getWidth();
//            int h = fLabel.getHeight(w);
//            yPos = h + 20;
            yPos = fTop;
        }
    }

    private void layoutOutput(RelativeLayout layout, View view)
    {
        int x = 10;
        int w = layout.getWidth() - 20;
        if (isWide(layout)) {
            w = (w - 10)/2;
            x += w + 10;
        }
        int h = (int)(60 * fScale);
        setLocation(view,x,yPos,w,h);
        yPos += h;
    }
}


