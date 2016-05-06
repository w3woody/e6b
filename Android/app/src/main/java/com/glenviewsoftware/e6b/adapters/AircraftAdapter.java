/*  AircraftAdapter.java
 *
 *  Created on Dec 30, 2012 by William Edward Woody
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
import com.glenviewsoftware.e6b.calc.wb.AircraftDatabase;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;

public class AircraftAdapter extends AbstractAdapter<WBAircraft>
{
    public interface Callback
    {
        void doDelete(int index);
        void doClick(int index);
    }

    private Context fContext;
    private Callback fCallback;

    public AircraftAdapter(Context context)
    {
        fContext = context;
    }
    
    public void setCallback(Callback c)
    {
        fCallback = c;
    }

    @Override
    public int getCount()
    {
        return AircraftDatabase.shared().userAircraftCount();
    }

    @Override
    public WBAircraft getItem(int position)
    {
        return AircraftDatabase.shared().userDefinedAircraft(position);
    }
    
    public void refreshList()
    {
        fireInvalidated();
    }

    private class Handler
    {
        View fRootView;
        TextView fTextView;
        ImageView fInsDel;
        ImageView fDisclose;
        TextView fFixedText;
        WBAircraft fAircraft;
        int fIndex;
        
        public Handler(View v)
        {
            fRootView = v;
            fTextView = (TextView)v.findViewById(R.id.textView);
            fFixedText = (TextView)v.findViewById(R.id.fixedView);
            fInsDel = (ImageView)v.findViewById(R.id.insDel);
            fDisclose = (ImageView)v.findViewById(R.id.open);
        }
        
        public void setContent(int index, WBAircraft t)
        {
            fIndex = index;
            fAircraft = t;
            
            fTextView.setText(t.getName());
            fRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    doClick();
                }
            });
            fInsDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    doDelete();
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
                    doClick();
                }
            });
        }

        protected void doDelete()
        {
            fCallback.doDelete(fIndex);
        }

        protected void doClick()
        {
            fCallback.doClick(fIndex);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Handler h;
        WBAircraft s = getItem(position);
        
        if (convertView != null) {
            h = (Handler)convertView.getTag();
        } else {
            LayoutInflater i = LayoutInflater.from(fContext);
            convertView = i.inflate(R.layout.editor_line, null);
            convertView.setTag(h = new Handler(convertView));
        }

        h.setContent(position,s);
        return convertView;
    }

}


