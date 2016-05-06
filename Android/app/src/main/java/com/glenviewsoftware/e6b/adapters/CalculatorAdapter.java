/*  SettingsAdapter.java
 *
 *  Created on Nov 16, 2012 by William Edward Woody
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
import android.widget.TextView;

import com.glenviewsoftware.e6b.R;
import com.glenviewsoftware.e6b.calc.Calculation;

/**
 * Settings adapter controls the list view in my settings window
 */
public class CalculatorAdapter extends AbstractAdapter<CalculatorAdapter.Row>
{
    private Context fContext;

    public CalculatorAdapter(Context context)
    {
        fContext = context;
    }

    public static class Row
    {
        private String label;
        private Calculation calc;
        
        public Row(String l)
        {
            label = l;
        }
        
        public Row(Calculation c)
        {
            calc = c;
        }
        
        public boolean isSeparator()
        {
            return (label != null);
        }
        
        public Calculation getCalculation()
        {
            return calc;
        }
        
        public String toString()
        {
            if (label != null) {
                return label;
            } else {
                return calc.getCalculationName();
            }
        }
    }
    
    @Override
    public int getItemViewType(int position)
    {
        if (getItem(position).isSeparator()) return 0;
        return 1;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }
    
    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return !getItem(position).isSeparator();
    }


    private class Handler
    {
        TextView fTextView;
        
        public Handler(View v)
        {
            fTextView = (TextView)v.findViewById(R.id.textView);
        }
        
        public void setContent(Row t)
        {
            fTextView.setText(t.toString());
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Handler h;
        Row s = getItem(position);
        
        if (convertView != null) {
            h = (Handler)convertView.getTag();
        } else {
            LayoutInflater i = LayoutInflater.from(fContext);
            if (s.isSeparator()) {
                convertView = i.inflate(R.layout.list_separator, null);
            } else {
                convertView = i.inflate(R.layout.list_item, null);
            }
            convertView.setTag(h = new Handler(convertView));
        }

        h.setContent(s);
        return convertView;
    }

}


