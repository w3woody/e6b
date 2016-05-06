/*  CalculatorEditText.java
 *
 *  Created on Jan 2, 2013 by William Edward Woody
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.method.SingleLineTransformationMethod;
import android.util.AttributeSet;
import android.widget.EditText;

public class CalculatorEditText extends EditText
{
    private float fScale;
    
    private class Background extends Drawable
    {
        private static final int RADIUS = 11;
        private Paint paint = new Paint();
        
        @Override
        public void draw(Canvas canvas)
        {
            if (!hasFocus()) return;
            
            Rect r = getBounds();
            RectF f = new RectF(r.left+2,r.top+2,r.right-2,r.bottom-2);
            
            float rad = RADIUS * fScale;
            
            paint.setColor(0xFF1D3D1D);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(f, rad, rad, paint);
            
            paint.setColor(0xFF008000);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawRoundRect(f, rad, rad, paint);
        }

        @Override
        public int getOpacity()
        {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha)
        {
        }

        @Override
        public void setColorFilter(ColorFilter cf)
        {
        }
    }

    public CalculatorEditText(Context context)
    {
        super(context);
        initialize();
    }

    public CalculatorEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public CalculatorEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void initialize()
    {
        fScale = getResources().getDisplayMetrics().density;
        
        this.setTextColor(0xFF00FF00);
        this.setTextSize(21);
        this.setSingleLine(true);
        this.setBackgroundDrawable(new Background());
        this.setTransformationMethod(SingleLineTransformationMethod.getInstance());
    }
    
    

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        invalidate();
    }

}


