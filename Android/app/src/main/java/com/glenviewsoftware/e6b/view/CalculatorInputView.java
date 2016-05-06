/*  CalculatorInputView.java
 *
 *  Created on Dec 17, 2012 by William Edward Woody
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

import java.util.ArrayList;
import java.util.Collection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.glenviewsoftware.e6b.R;
import com.glenviewsoftware.e6b.calc.Value;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.units.Speed;
import com.glenviewsoftware.e6b.units.Time;

public class CalculatorInputView extends View
{
    private static CalculatorInputView gInput;
    private Paint fPaint;
    private float fScale;
    
    private boolean time;
    private boolean clear;
    private boolean negate;
    private boolean editable;
    
    private char lastOp;
    private double value;
    
    private int pos;
    private char[] buf = new char[128];
    
    private String label;
    private Measurement measurement;
    private int unit;
    
    private boolean compact;
    
    private ArrayList<InputFixedValues> fixedValues = new ArrayList<InputFixedValues>();
    private InputViewCallback callback;
    private Drawable fDropArrow;
    
    private static final int RADIUS = 11;
    
    /**
     * Entry in the fixed values
     */
    public static class InputFixedValues
    {
        private Value value;
        private String label;
        
        public InputFixedValues(Value v, String l)
        {
            value = v;
            label = l;
        }
    }
    
    /**
     * Input callbacks
     */
    public interface InputViewCallback
    {
        void viewDidClear(CalculatorInputView v);
        void viewDidUpdate(CalculatorInputView v);
    }
    
    public CalculatorInputView(Context context)
    {
        super(context);
        initialize();
    }

    public CalculatorInputView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public CalculatorInputView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize()
    {
        fPaint = new Paint();
        fPaint.setAntiAlias(true);
        fScale = getResources().getDisplayMetrics().density;
        fDropArrow = getResources().getDrawable(R.drawable.droparrow);
        editable = true;
        setFocusable(true);
        setEnabled(true);
        setFocusableInTouchMode(true);
        
        // TODO: Test
        measurement = new Speed();
        label = "Test";
        
        callback = new InputViewCallback() {
            @Override
            public void viewDidClear(CalculatorInputView v)
            {
            }

            @Override
            public void viewDidUpdate(CalculatorInputView v)
            {
            }
        };
    }

    /********************************************************************************/
    /*                                                                              */
    /*  Key input                                                                   */
    /*                                                                              */
    /********************************************************************************/

    public static final void doInput(char key)
    {
        if (gInput != null) {
            gInput.doInputKey(key);
        }
    }
    
    private void doInputKey(char key)
    {
        addCharacter(key);
    }
    
    protected void onFocusChanged (boolean gainFocus, int direction, Rect prev)
    {
        if (gainFocus) {
            gInput = this;
            
            // Close keyboard; use my own
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        } else if (gInput == this) {
            gInput = null;
        }
        invalidate();
    }

    /********************************************************************************/
    /*                                                                              */
    /*  Parameters                                                                  */
    /*                                                                              */
    /********************************************************************************/
    
    public void setCallback(InputViewCallback c)
    {
        callback = c;
    }
    
    public InputViewCallback getCallback()
    {
        return callback;
    }
    
    public void setLabel(String v)
    {
        label = v;
        invalidate();
    }
    
    public void setCompact(boolean c)
    {
        compact = c;
        invalidate();
    }
    
    public void setUnit(int i)
    {
        unit = i;
        invalidate();
    }
    
    public void setEditable(boolean e)
    {
        editable = e;
        invalidate();
    }
    
    public void setFixedValues(Collection<InputFixedValues> values)
    {
        fixedValues = new ArrayList<InputFixedValues>(values);
        invalidate();
    }
    
    public void setMeasurement(Measurement m)
    {
        unit = 0;
        measurement = m;
        time = (m instanceof Time);
        invalidate();
    }

    /********************************************************************************/
    /*                                                                              */
    /*  Calculator Support                                                          */
    /*                                                                              */
    /********************************************************************************/
    
    private static boolean isdigit(char c)
    {
        return ((c >= '0') && (c <= '9'));
    }

    /*  valueFromInput
     *
     *      Parse the buffer to get the input stream
     */

    private double valueFromInput()
    {
        int h = 0;
        int m = 0;
        int s = 0;
        double f = 0;
        double fpos = 1;
        boolean fract = false;
        boolean colon = false;

        /*
         *  Run through and pull the elements
         */

        for (int i = 0; i < pos; ++i) {
            char c = buf[i];
            if ((c == ':') && time) {
                colon = true;
                h = m;
                m = s;
                s = 0;
            } else if (c == '.') {
                fract = true;
            } else if (isdigit(c)) {
                if (fract) {
                    fpos /= 10;
                    f += fpos * (c - '0');
                } else {
                    s = s * 10 + (c - '0');
                }
            }
        }

        /*
         *  Assemble the final value. If we have a colon, it's minutes/seconds,
         *  else for time it's hours and fractions of an hour.
         */

        double val;
        if (time && colon) {
            val = h + m / 60.0 + (s + f)/3600.0;
        } else {
            val = s + f;
        }
        if (negate) val = -val;
        return val;
    }

    /*  backspace
     *
     *      Handle backspace operation. This backs up through the fields
     */

    private void backspace()
    {
        if (pos > 0) --pos;
        else clear();
        invalidate();
        callback.viewDidUpdate(this);
    }

    private void resolve()
    {
        if (pos == 0) return;

        double v = valueFromInput();
        switch (lastOp) {
            case '+':
                value = value + v;
                break;
            case '-':
                value = value - v;
                break;
            case '*':
                value = value * v;
                break;
            case '/':
                value = value / v;
                break;
            default:
                value = v;
                break;
        }
        pos = 0;
        negate = false;
    }

    /*  addCharacter
     *
     *      Add a character to the input stream. This runs a sort of state machine
     *  as if this was a calculator, and contains the heart of the calculator-like
     *  logic
     */

    private void addCharacter(char ch)
    {
        if (!editable) return;

        if (ch == 0x08) {
            backspace();
            return;
        } else if (ch == 0x7F) {
            clear();
            return;
        }

        clear = false;
        if (ch == '!') {
            negate = !negate;
            invalidate();
            callback.viewDidUpdate(this);
        } else if (ch == 0x0A) {
            /*
             *  Equals. Collapse the last value
             */

            resolve();
            lastOp = 0;
            invalidate();
            callback.viewDidUpdate(this);
        } else if ((ch == '+') || (ch == '-') || (ch == '*') || (ch == '/')) {
            /*
             *  Operator. Push the current value in the display to the back and
             *  set up for the next value
             */

            if (pos != 0) resolve();
            lastOp = ch;
            invalidate();
            callback.viewDidUpdate(this);
        } else if (isdigit(ch)) {
            /*
             *  Enter digit
             */

            if (pos < buf.length) buf[pos++] = ch;
            invalidate();
            callback.viewDidUpdate(this);
        } else if (ch == '.') {
            /*
             *  Enter decimal, only if not present
             */

            if (pos < buf.length) {
                for (int i = 0; i < pos; ++i) {
                    if (buf[i] == '.') return;
                }
                buf[pos++] = ch;
                invalidate();
                callback.viewDidUpdate(this);
            }
        } else if (time && (ch == ':')) {
            /*
             *  Enter time separator, only if legal
             */

            if (pos < buf.length) {
                int ct = 0;
                for (int i = 0; i < pos; ++i) {
                    if (buf[i] == '.') return;
                    if (buf[i] == ':') {
                        ++ct;
                        if (ct >= 2) return;
                    }
                }
                buf[pos++] = ch;
                invalidate();
                callback.viewDidUpdate(this);
            }
        }
    }

    public boolean isClear()
    {
        return clear;
    }

    private void clear()
    {
        clear = true;
        value = 0;
        negate = false;
        lastOp = 0;
        pos = 0;
        invalidate();
        callback.viewDidClear(this);
    }

    /*  value
     *
     *      Get the current value of this field.
     */

    public double getValue()
    {
        if (pos != 0) return valueFromInput();
        return value;
    }

    public double getValueAsUnit(int uout)
    {
        double val = getValue();
        if (measurement == null) return val;

        val = measurement.toStandardUnit(val,unit);
        val = measurement.fromStandardUnit(val,uout);
        return val;
    }

    public int getUnit()
    {
        return unit;
    }

    public Value saveStoreValue()
    {
        return new Value(getValue(),unit);
    }

    public void loadStoreValue(Value v)
    {
        unit = v.getUnit();
        setValue(v.getValue());
    }

    public void setValue(double v)
    {
        clear = true;
        value = v;
        negate = false;
        lastOp = 0;
        pos = 0;
        invalidate();
    }

    public void setValue(double v, int uin)
    {
        if (measurement == null) {
            setValue(v);
        } else {
            double val = measurement.toStandardUnit(v, uin);
            setValue(measurement.fromStandardUnit(val, unit));
        }
    }
    
    private static String formatValue(boolean time, double value)
    {
        int h,m;
        StringBuffer prefix = new StringBuffer();
        StringBuffer buffer = new StringBuffer();
        
        if (Double.isInfinite(value)) return "--";
        if (Double.isNaN(value)) return "--";

        boolean neg = (value < 0);
        double v = value;
        if (neg) v = -v;
        
        if (neg) prefix.append('-');
        
        if (time) {
            v = Math.floor(v * 3600 + 0.5);
            
            long s = (long)v;
            h = (int)(s/3600);
            s -= h * 3600;
            m = (int)(s / 60);
            s -= m * 60;
            
            buffer.append(String.format("%d:%02d:%02d",h,m,(int)s));
            prefix.append(buffer);
            return prefix.toString();
            
        } else {
            buffer.append(String.format("%.2f",v));
            
            boolean f = false;
            int i = 0;
            while (i < buffer.length()) {
                if (buffer.charAt(i) == '.') f = true;
                ++i;
            }
            if (f) {
                while (i > 0) {
                    if (buffer.charAt(i-1) == '0') {
                        buffer.setLength(--i);
                    } else {
                        break;
                    }
                }
                if (buffer.charAt(i-1) == '.') {
                    buffer.setLength(--i);
                }
            }
            
            prefix.append(buffer);
            return prefix.toString();
        }
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        if (clear || (!negate && (pos == 0))) {
            return formatValue(time,value);

        } else {
            if (negate) buffer.append('-');
            if (pos == 0) {
                buffer.append('0');
            } else {
                buffer.append(buf, 0, pos);
            }
            return buffer.toString();
        }
    }

    /********************************************************************************/
    /*                                                                              */
    /*  Drawing                                                                     */
    /*                                                                              */
    /********************************************************************************/

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        float radius = RADIUS * fScale;
        
        /*
         * Draw if frame is selected
         */
        
        RectF rect = new RectF(2,2,getWidth()-2,getHeight()-2);
        if ((editable && isFocused()) || isInEditMode()) {
            fPaint.setStyle(Paint.Style.FILL);
            fPaint.setColor(0xFF1D3D1D);
            canvas.drawRoundRect(rect, radius, radius, fPaint);
            
            fPaint.setStyle(Paint.Style.STROKE);
            fPaint.setStrokeWidth(3);
            fPaint.setColor(0xFF008000);
            canvas.drawRoundRect(rect, radius, radius, fPaint);
        }
        fPaint.setStyle(Paint.Style.FILL);
        
        /*
         * Determine the string to display depending on what's going on
         */
        
        boolean hasFixed = fixedValues.size() > 0;
        int baseline;
        
        int uwidth = (int)(fScale * (compact ? 45 : 55));
        
        String str = toString();
        float fsize = fScale * (compact ? 23 : 29);
        fPaint.setTextSize(fsize);
        if (editable) {
            fPaint.setColor(0xFF00FF00);
        } else {
            fPaint.setColor(0xFFFFFFFF);
        }
        
        rect.left += 5;
        rect.right -= uwidth - 5;
        if (hasFixed) {
            rect.left += 18;
        }
        
        if (compact) {
            baseline = (int)((getHeight() - fPaint.ascent() - fPaint.descent())/2);
        } else {
            baseline = (int)(getHeight() - 10 * fScale);
        }
        
        float xpos = rect.right - fPaint.measureText(str);
        while ((xpos < 0) && (fsize > 10)) {
            --fsize;
            fPaint.setTextSize(fsize);
            xpos = rect.right - fPaint.measureText(str);
        }
        canvas.drawText(str, xpos, baseline, fPaint);
        
        /*
         * Draw selector label
         */
        if (hasFixed) {
            int w = (int)(fDropArrow.getIntrinsicWidth());
            int h = (int)(fDropArrow.getIntrinsicHeight());
            int left = (int)(2 * fScale);
            int top = (getHeight() - h)/2;
            fDropArrow.setBounds(left, top, left + w, top + h);
            fDropArrow.draw(canvas);
        }
        
        /*
         * Draw label
         */
        
        if (!compact && (label != null)) {
            fPaint.setTextSize(15 * fScale);
            fPaint.setColor(0xFFFFFFFF);
            int left = (int)(getWidth() - uwidth - fPaint.measureText(label));
            canvas.drawText(label, left, 3 - fPaint.ascent(), fPaint);
        }
        
        /*
         * Draw unit
         */
        
        if (!time && (measurement != null)) {
            int s = (int)((compact ? 15 : 17) * fScale);
            fPaint.setTextSize(s);
            fPaint.setColor(0xFFFFFFFF);
            
            int left = getWidth() - (uwidth - 10);
            
            String ulabel = measurement.getAbbrMeasure(unit);
            canvas.drawText(ulabel, left, baseline, fPaint);
        }
    }
    
    private void selectUnit()
    {
        String[] list;
        
        int i,len = measurement.numUnits();
        if (len <= 1) return;
        
        list = new String[len];
        for (i = 0; i < len; ++i) list[i] = measurement.getMeasurement(i);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Unit");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                unit = which;
                invalidate();
                callback.viewDidUpdate(CalculatorInputView.this);
            }
        });
        builder.setCancelable(true);
        
        builder.show();
    }
    
    private void selectFixedValue()
    {
        if ((fixedValues == null) || (fixedValues.size() < 1)) return;
        ArrayList<String> list = new ArrayList<String>();
        
        for (InputFixedValues v: fixedValues) {
            StringBuffer b = new StringBuffer();
            b.append(v.label);
            b.append(" (");
            b.append(formatValue(time,v.value.getValue()));
            if (!time) {
                b.append(' ');
                b.append(measurement.getAbbrMeasure(v.value.getUnit()));
            }
            b.append(")");
            list.add(b.toString());
        }
        
        String[] lstr = list.toArray(new String[list.size()]);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Value");
        builder.setItems(lstr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                clear();

                Value v = fixedValues.get(which).value;
                unit = v.getUnit();
                value = v.getValue();
                invalidate();
                callback.viewDidUpdate(CalculatorInputView.this);
            }
        });
        builder.setCancelable(true);
        
        builder.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() != MotionEvent.ACTION_UP) return true;
        
        int width = getWidth();
        int uwidth = compact ? 44 : 55;
        float x = event.getX();
        
        if ((x > width - uwidth) && (measurement != null)) {
            playSoundEffect(SoundEffectConstants.CLICK);
            selectUnit();
            if (editable) requestFocus();
            return true;
        }
        
        if ((x < 44) && (fixedValues.size() > 0)) {
            playSoundEffect(SoundEffectConstants.CLICK);
            selectFixedValue();
            if (editable) requestFocus();
            return true;
        }
        
        if (editable) {
            playSoundEffect(SoundEffectConstants.CLICK);
            requestFocus();
        }
        return true;
    }
}


