/*  CalcManager.java
 *
 *  Created on Dec 23, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.calc;

import android.view.View;
import android.widget.RelativeLayout;

import com.glenviewsoftware.e6b.view.CalculatorInputView;

/**
 * The calculation manager. Holds a reference to the calculation object for internal
 * purposes
 */
public abstract class CalcManager
{
    /**
     * Set the delegate
     * @param del
     */
    public abstract void setDelegate(CalcDelegate del);
    
    /**
     * Set the input callback during startup
     * @param callback
     */
    public abstract void setInputCallback(CalculatorInputView.InputViewCallback callback);
    
    /**
     * Used to construct the new views on startup
     * @param frame 
     */
    public abstract void constructCalcViews(RelativeLayout frame);
    
    /**
     * Used to handle laying out the calculation views after startup
     * @param frame
     */
    public abstract void layoutCalcViews(RelativeLayout frame);
    
    /**
     * Used to update the values of the calculation
     */
    public abstract void updateCalculation();
    
    /*
     * Internal routines used to construct the views
     */
    
    /**
     * Create an input view and put it into the layout. This returns the input view but
     * this does not position the view
     * @param layout
     * @return
     */
    protected CalculatorInputView createInputView(RelativeLayout layout)
    {
        CalculatorInputView v = new CalculatorInputView(layout.getContext());
        RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(10, 10);
        l.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layout.addView(v, l);
        return v;
    }
    
    protected void addView(View view, RelativeLayout layout)
    {
        RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(10, 10);
        l.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layout.addView(view, l);
    }
    
    /**
     * Is this a wide layout?
     * @param layout
     * @return
     */
    protected boolean isWide(RelativeLayout layout)
    {
        float sf = layout.getResources().getDisplayMetrics().density;
        float scale = layout.getResources().getDisplayMetrics().ydpi;
        float ywidth = layout.getWidth();
        return (ywidth / (scale * sf)) > 2.5;
    }
    
    protected void setLocation(View v, int left, int top, int width, int height)
    {
        if (v == null) return;
        
        RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(width, height);
        l.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        l.setMargins(left, top, 0, 0);
        v.setLayoutParams(l);
    }
}


