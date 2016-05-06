/*  CalculatorKeyboardView.java
 *
 *  Created on Dec 15, 2012 by William Edward Woody
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
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

/**
 * Provides a keyboard input view for my calculator 
 */
public class CalculatorKeyboardView extends View
{
    private Paint fPaint;
    private float fScale;
    
    private int fBtnWidth;
    private int fBtnHeight;
    private int fXOffset;
    private int fYOffset;
    
    private int fHitItem;
    private boolean fInHit;
    
    private static final int MINWIDTH = 66;
    private static final int MINHEIGHT = 44;
    private static final int MAXWIDTH = 88;
    
    private static final char[] GButtons = {
        '7', '4', '1', ':',
        '8', '5', '2', '0',
        '9', '6', '3', '.',
        '+', '\u2013', '\u00D7', '\u00F7',
        'C', '\u2190', '=', '\u00B1'
    };
    private static final char[] GValues = {
        '7', '4', '1', ':',
        '8', '5', '2', '0',
        '9', '6', '3', '.',
        '+', '-', '*', '/',
        0x7F, 0x08, 0x0A, '!'
    };
    
    public CalculatorKeyboardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    public CalculatorKeyboardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public CalculatorKeyboardView(Context context)
    {
        super(context);
        initialize();
    }

    private void initialize()
    {
        fScale = getResources().getDisplayMetrics().density;
        fPaint = new Paint();
        fPaint.setAntiAlias(true);
        fPaint.setTextSize(27 * fScale);
        fHitItem = -1;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        
        int maxbtnwidth = (int)(MAXWIDTH * fScale);
        int btnwidth = getWidth()/5;
        if (btnwidth > maxbtnwidth) btnwidth = maxbtnwidth;
        
        fBtnWidth = btnwidth;
        fBtnHeight = (btnwidth * MINHEIGHT)/MINWIDTH;
        int maxBtnHeight = getHeight() / 4;
        if (fBtnHeight > maxBtnHeight) fBtnHeight = maxBtnHeight;
        
        fXOffset = (getWidth() - fBtnWidth * 5)/2;
        fYOffset = (getHeight() - fBtnHeight * 4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        int btnwidth = (int)(MINWIDTH * fScale);
        int maxbtnwidth = (int)(MAXWIDTH * fScale);
        int swidth = btnwidth * 5;
        
        if (specWidthMode == MeasureSpec.UNSPECIFIED) {
            specWidthSize = swidth;
        }
        
        btnwidth = specWidthSize / 5;
        if (btnwidth > maxbtnwidth) btnwidth = maxbtnwidth;
        int btnheight = (btnwidth * MINHEIGHT)/MINWIDTH;
        
        int height = btnheight * 4;
        if (specHeightMode == MeasureSpec.EXACTLY) {
            height = specHeightSize;
        } else if (specHeightMode == MeasureSpec.AT_MOST) {
            if (height > specHeightSize) height = specHeightSize;
        }
        
        this.setMeasuredDimension(specWidthSize, height);
    }
    
    private RectF getButton(int index)
    {
        int y = index % 4;
        int x = index / 4;
        
        x = fXOffset + x * fBtnWidth;
        y = fYOffset + y * fBtnHeight;
        return new RectF(x,y,x+fBtnWidth,y+fBtnHeight);
    }
    
    protected void drawButton(Canvas c, RectF r, char label, boolean pressed)
    {
        r.inset(3*fScale, 2*fScale);
        
        LinearGradient lg;
        if (pressed) {
            lg = new LinearGradient(r.left,r.top,r.left,r.bottom,0xFFB3B3B3, 0xFF808080,Shader.TileMode.CLAMP);
        } else {
            lg = new LinearGradient(r.left,r.top,r.left,r.bottom,0xFFEDEDED, 0xFFD4D4D4,Shader.TileMode.CLAMP);
        }
        fPaint.setShader(lg);
        fPaint.setStyle(Paint.Style.FILL);
        c.drawRoundRect(r, 8*fScale, 8*fScale, fPaint);
        
        fPaint.setShader(null);
        fPaint.setColor(0xFF333333);
        fPaint.setStyle(Paint.Style.STROKE);
        c.drawRoundRect(r, 8*fScale, 8*fScale, fPaint);
        
        String str = "" + label;
        int width = (int)fPaint.measureText(str);
        int y = (int)(r.top + r.bottom - fPaint.ascent())/2;
        c.drawText("" + label, (int)(r.right + r.left - width)/2, y, fPaint);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        /*
         * Draw background
         */
        LinearGradient lg = new LinearGradient(0,0,0,getHeight(),0xFF9E9CA8, 0xFF42424A, Shader.TileMode.CLAMP);
        fPaint.setShader(lg);
        fPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), fPaint);
        
        for (int i = 0; i < 20; ++i) {
            RectF r = getButton(i);
            drawButton(canvas, r,GButtons[i],fHitItem == i);
        }
        
        fPaint.setShader(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int)event.getX();
        int y = (int)event.getY();
        int i;
        for (i = 0; i < 20; ++i) {
            RectF r = getButton(i);
            if (r.contains(x, y)) {
                break;
            }
        }
        if (i == 20) i = -1;
        
        if ((event.getAction() == MotionEvent.ACTION_DOWN) || (event.getAction() == MotionEvent.ACTION_MOVE)) {
            if (fHitItem != i) {
                fHitItem = i;
                fInHit = (i != -1);
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (fInHit) {
                playSoundEffect(SoundEffectConstants.CLICK);
                doKey(GValues[fHitItem]);
            }
            fHitItem = -1;
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            fHitItem = -1;
            fInHit = false;
            invalidate();
        }
        
        return true;
    }
    
    /**
     * Send over to active input view
     * @param k
     */
    private void doKey(char k)
    {
        CalculatorInputView.doInput(k);
    }
}
