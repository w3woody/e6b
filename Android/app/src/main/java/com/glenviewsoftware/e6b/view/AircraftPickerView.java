/*  AircraftPickerView.java
 *
 *  Created on Jan 1, 2013 by William Edward Woody
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

package com.glenviewsoftware.e6b.view;

import com.glenviewsoftware.e6b.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * View presents picker display
 */
public class AircraftPickerView extends View
{
    private Paint fPaint;
    private float fScale;
    private String fLabel;
    private Drawable fOpen;

    public AircraftPickerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    public AircraftPickerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public AircraftPickerView(Context context)
    {
        super(context);
        initialize();
    }

    public void initialize()
    {
        fPaint = new Paint();
        fPaint.setAntiAlias(true);
        fScale = getResources().getDisplayMetrics().density;
        
        fPaint.setTextSize(21 * fScale);
        
        fOpen = getResources().getDrawable(R.drawable.open);
    }
    
    public void setText(String t)
    {
        fLabel = t;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int xpos = 5;
        int ypos = (int)((getHeight() - fPaint.ascent() - fPaint.descent())/2);
        String label;
        if ((fLabel == null) || (fLabel.length() == 0)) {
            label = "Select Aircraft";
            fPaint.setColor(0xFF008800);
        } else {
            label = fLabel;
            fPaint.setColor(0xFF00FF00);
        }
        canvas.drawText(label, xpos, ypos, fPaint);
        
        int btm = (int)(ypos + fPaint.descent() + 2);
        
        int ih = (int)(-fPaint.ascent()/1.3);
        int iw = (fOpen.getIntrinsicWidth() * ih)/fOpen.getIntrinsicHeight();
        xpos = getWidth() - iw - 5;
        ypos -= ih + 2;
        fOpen.setBounds(xpos,ypos,xpos+iw,ypos+ih);
        fOpen.draw(canvas);
        
        fPaint.setColor(0xFF008800);
        canvas.drawRect(0, btm, getWidth(), btm+1, fPaint);
    }
    
    
}


