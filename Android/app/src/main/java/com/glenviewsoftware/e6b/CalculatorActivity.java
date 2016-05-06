/*  CalculatorActivity.java
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

package com.glenviewsoftware.e6b;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.glenviewsoftware.e6b.calc.CalcDelegate;
import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.Calculation;
import com.glenviewsoftware.e6b.calc.wb.WBCalculation;
import com.glenviewsoftware.e6b.view.CalculatorInputView;
import com.glenviewsoftware.e6b.view.ContentLayout;

public class CalculatorActivity extends Activity
{
    private TextView fTitle;
    private ContentLayout fContents;
    private Calculation fCurCalculation;
    private CalcManager fCurCalcManager;
    private View fHelpButton;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);
        
        fTitle = (TextView)findViewById(R.id.title);
        fContents = (ContentLayout)findViewById(R.id.contents);
        fContents.setOnDidLayout(new ContentLayout.OnDidLayout() {
            @Override
            public void didLayout(final ContentLayout l)
            {
                if (fCurCalcManager != null) {
                    fContents.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            fCurCalcManager.layoutCalcViews(l);
                            l.requestLayout();
                        }
                    });
                }
            }
        });
        
        fHelpButton = findViewById(R.id.help);
        fHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CalculatorActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        int index = intent.getIntExtra("calc", 0);
        boolean wb = intent.getBooleanExtra("type", false);

        /*
         * Wire up calculation, calculation manager
         */
        if (wb) {
            fCurCalculation = WBCalculation.getWBValues().get(index);
        } else {
            fCurCalculation = E6BApplication.e6bCalculations().get(index).getCalculation();
        }
        if (fCurCalculation == null) {
            finish();
            return; /* ERROR */
        }
        fCurCalcManager = fCurCalculation.getCalculationManager();
        
        fCurCalcManager.setDelegate(new CalcDelegate() {
            @Override
            public void updateCalcName(String name)
            {
                fTitle.setText(name);
                // TODO ?
            }

            @Override
            public void updateLayout()
            {
                fCurCalcManager.layoutCalcViews(fContents);
            }
        });
        fCurCalcManager.setInputCallback(new CalculatorInputView.InputViewCallback() {
            @Override
            public void viewDidClear(CalculatorInputView v)
            {
                fCurCalcManager.updateCalculation();
            }

            @Override
            public void viewDidUpdate(CalculatorInputView v)
            {
                fCurCalcManager.updateCalculation();
            }
        });
        
        fCurCalcManager.constructCalcViews(fContents);
        if (fContents.getWidth() > 0) {
            fCurCalcManager.layoutCalcViews(fContents);
            fContents.requestLayout();
        }
        
        fTitle.setText(fCurCalculation.getCalculationName());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        
        /* Make sure E6B values are saved */
        CalcStorage.shared().saveValues();
    }
    
    
}


