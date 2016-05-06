/*  DescriptionView.java
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

package com.glenviewsoftware.e6b.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Simple view to draw description. Also calculates the height of this thing
 *	Comment
 */
public class DescriptionView extends View
{
    private TextPaint fPaint;
    private float fScale;
    private String fDescription;

    public DescriptionView(Context context)
    {
        super(context);
        initialize();
    }

    public DescriptionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public DescriptionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize()
    {
        fScale = getResources().getDisplayMetrics().density;
        fPaint = new TextPaint();
        fPaint.setAntiAlias(true);
        fPaint.setTextSize(14 * fScale);
    }
    
    public void setDescription(String desc)
    {
        fDescription = desc;
        invalidate();
    }
    
    public int getHeight(int width)
    {
        StaticLayout layout = new StaticLayout(fDescription,fPaint,width,Layout.Alignment.ALIGN_NORMAL,1,0,true);
        return layout.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        StaticLayout layout = new StaticLayout(fDescription,fPaint,getWidth(),Layout.Alignment.ALIGN_NORMAL,1,0,true);
        fPaint.setColor(0xFFFFFFFF);
        layout.draw(canvas);
    }
    
    
}


