/*  ListItemLayout.java
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
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class ListItemLayout extends RelativeLayout implements Checkable
{
    private boolean fChecked;

    public ListItemLayout(Context context)
    {
        super(context);
    }

    public ListItemLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ListItemLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isChecked()
    {
        return fChecked;
    }

    @Override
    public void setChecked(boolean checked)
    {
        fChecked = checked;
        invalidate();
    }

    @Override
    public void toggle()
    {
        setChecked(!isChecked());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (fChecked) {
            canvas.drawColor(0xFF333333);
        } else {
            super.onDraw(canvas);
        }
    }
}


