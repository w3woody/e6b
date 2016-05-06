/*  ContentLayout.java
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
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * ContentLayout extends RelativeLayout, and provides a mechanism to detect if the 
 * view was laid out. This allows a callback which is triggered only when the width
 * changes
 */
public class ContentLayout extends RelativeLayout
{
    private OnDidLayout fCallback;
    
    public interface OnDidLayout
    {
        void didLayout(ContentLayout l);
    }
    
    public ContentLayout(Context context)
    {
        super(context);
    }

    public ContentLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ContentLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    /**
     * Set callback to fire when a layout takes place. Will fire only once
     * @param d
     */
    public void setOnDidLayout(OnDidLayout d)
    {
        fCallback = d;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        
        if ((fCallback != null) && (oldw != w)) {
            post(new Runnable() {
                @Override
                public void run()
                {
                    fCallback.didLayout(ContentLayout.this);
                }
            });
        }
    }

}


