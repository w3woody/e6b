/*  SelectorView.java
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

package com.glenviewsoftware.e6b.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

public class SelectorView extends View
{
    public interface Callback
    {
        int numberItems();
        String itemLabel(int index);
        void doSelect(int index);
    }

    private Paint fPaint;
    private float fScale;
    private Callback fCallback;
    private int fSelect;

    public SelectorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    public SelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public SelectorView(Context context)
    {
        super(context);
        initialize();
    }

    private void initialize()
    {
        fPaint = new Paint();
        fScale = getResources().getDisplayMetrics().density;
        fPaint.setTextSize(fScale * 15);
        fPaint.setAntiAlias(true);
        
        fCallback = new Callback() {
            @Override
            public int numberItems()
            {
                return 2;
            }

            @Override
            public String itemLabel(int index)
            {
                switch (index) {
                    default:
                    case 0: return "E6B";
                    case 1: return "W&B";
                }
            }

            @Override
            public void doSelect(int index)
            {
            }
        };
    }
    
    public void setCallback(Callback c)
    {
        fCallback = c;
        invalidate();
    }
    
    private Rect tabLocation(int index)
    {
        int len = fCallback.numberItems();
        int w = getWidth() + 1;
        
        int left = (w * index)/len;
        int right = (w * (index + 1))/len;
        int h = getHeight();
        
        return new Rect(left,0,right,h);
    }

    @Override
    public void draw(Canvas canvas)
    {
        int w = getWidth();
        int h = getHeight();
        
        float rad = 8 * fScale;
        RectF r = new RectF(0,0,w,h);
        
        Path path = new Path();
        path.addRoundRect(r, rad, rad, Path.Direction.CCW);

        /*
         * Draw the border, frame and contents
         */
        fPaint.setStyle(Paint.Style.FILL);
        LinearGradient g = new LinearGradient(0,0,0,h,0xFF666666,0xFF444444,Shader.TileMode.CLAMP);
        fPaint.setShader(g);
        canvas.drawRoundRect(r, rad, rad, fPaint);
        
        canvas.save();
        Rect tl = tabLocation(fSelect);
        canvas.clipPath(path);
        g = new LinearGradient(0,0,0,h,0xFF888888,0xFF666666,Shader.TileMode.CLAMP);
        fPaint.setShader(g);
        canvas.drawRect(tl, fPaint);
        canvas.restore();
        
        fPaint.setStyle(Paint.Style.STROKE);
        fPaint.setShader(null);
        fPaint.setStrokeWidth(1);
        fPaint.setColor(0xFF222222);
        canvas.drawRoundRect(r, rad, rad, fPaint);
        
        /*
         * Draw the labels
         */
        
        int i,len = fCallback.numberItems();
        fPaint.setColor(0xFFFFFFFF);
        fPaint.setStyle(Paint.Style.FILL);
        for (i = 0; i < len; ++i) {
            String label = fCallback.itemLabel(i);
            
            tl = tabLocation(i);
            float xpos = (tl.left + tl.right - fPaint.measureText(label))/2;
            float ypos = (h - fPaint.ascent() - fPaint.descent())/2;
            canvas.drawText(label, xpos, ypos, fPaint);
        }
        
        /*
         * Draw the separator bars
         */
        
        fPaint.setColor(0xFF222222);
        for (i = 1; i < len; ++i) {
            tl = tabLocation(i);
            canvas.drawLine(tl.left-1, 0, tl.left-1, h, fPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int len = fCallback.numberItems();
            int pos = (int)event.getX();
            int width = getWidth();
            int x = (pos * len)/width;
            
            if (x != fSelect) {
                fSelect = x;
                invalidate();
                playSoundEffect(SoundEffectConstants.CLICK);
                fCallback.doSelect(x);
            }
        }
        
        return true;
    }
    
    public void setSelection(int sel)
    {
        fSelect = sel;
        invalidate();
    }
}


