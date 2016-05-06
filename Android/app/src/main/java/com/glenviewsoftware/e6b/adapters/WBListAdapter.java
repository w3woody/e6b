/*  WBListAdapter.java
 *
 *  Created on Dec 31, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.glenviewsoftware.e6b.R;
import com.glenviewsoftware.e6b.calc.wb.WBCalculation;

public class WBListAdapter extends AbstractAdapter<WBCalculation>
{
    private Context fContext;
    private Callback fCallback;

    public interface Callback
    {
        void doDelete(int index);
        void doClick(int index);
    }

    
    public WBListAdapter(Context c)
    {
        fContext = c;
    }

    public void setCallback(Callback c)
    {
        fCallback = c;
    }


    private class Handler
    {
        View fRootView;
        TextView fTextView;
        ImageView fInsDel;
        ImageView fDisclose;
        TextView fFixedText;
        
        public Handler(View v)
        {
            fRootView = v;
            fTextView = (TextView)v.findViewById(R.id.textView);
            fFixedText = (TextView)v.findViewById(R.id.fixedView);
            fInsDel = (ImageView)v.findViewById(R.id.insDel);
            fDisclose = (ImageView)v.findViewById(R.id.open);
        }
        
        public void setContent(final int index, WBCalculation c)
        {
            fTextView.setText(c.getCalculationName());
            fRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (fCallback != null) {
                        fCallback.doClick(index);
                    }
                }
            });
            fInsDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (fCallback != null) {
                        fCallback.doDelete(index);
                    }
                }
            });
            
            fInsDel.setVisibility(View.VISIBLE);
            fInsDel.setImageResource(R.drawable.delete);
            fDisclose.setVisibility(View.GONE);
            
            fFixedText.setText("");
            fFixedText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (fCallback != null) {
                        fCallback.doClick(index);
                    }
                }
            });
        }
    }
    
    public void reloadNamed()
    {
        fireChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Handler h;
        WBCalculation s = getItem(position);
        
        if (convertView != null) {
            h = (Handler)convertView.getTag();
        } else {
            LayoutInflater i = LayoutInflater.from(fContext);
            convertView = i.inflate(R.layout.editor_line, null);
            convertView.setTag(h = new Handler(convertView));
        }

        h.setContent(position, s);
        return convertView;
    }
}


