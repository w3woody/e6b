/*  AircraftPickerAdapter.java
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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glenviewsoftware.e6b.R;
import com.glenviewsoftware.e6b.calc.wb.AircraftDatabase;

public class AircraftPickerAdapter extends AbstractAdapter<AircraftPickerAdapter.Row>
{
    public static class Row
    {
        private boolean separator;
        private String label;
        private String subLabel;
        
        private int groupID;
        private int itemID;
        
        private Row(String sep)
        {
            separator = true;
            label = sep;
        }
        
        private Row(int g, int i, String name)
        {
            groupID = g;
            itemID = i;
            label = name;
        }
        
        private Row(int i, String name, String mk)
        {
            groupID = -1;
            itemID = i;
            label = name;
            subLabel = mk;
        }
        
        public boolean isSeparator()
        {
            return separator;
        }
        
        public int getGroupID()
        {
            return groupID;
        }
        
        public int getItemID()
        {
            return itemID;
        }
    }

    private Context fContext;
    
    public AircraftPickerAdapter(Context a)
    {
        fContext = a;
        
        ArrayList<Row> l = new ArrayList<Row>();
        int i,len = AircraftDatabase.shared().userAircraftCount();
        if (len != 0) {
            l.add(new Row("User Defined"));
            for (i = 0; i < len; ++i) {
                String n = AircraftDatabase.shared().aircraftNameForUserIndex(i);
                String lb = AircraftDatabase.shared().aircraftIdentForUserIndex(i);
                l.add(new Row(i,n,lb));
            }
        }
        
        int g,glen = AircraftDatabase.shared().groupCount();
        for (g = 0; g < glen; ++g) {
            l.add(new Row(AircraftDatabase.shared().groupName(g)));
            len = AircraftDatabase.shared().aircraftCountForGroupIndex(g);
            for (i = 0; i < len; ++i) {
                String n = AircraftDatabase.shared().aircraftNameInGroup(i, g);
                l.add(new Row(g,i,n));
            }
        }
        
        setData(l);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (getItem(position).isSeparator()) return 0;
        if (getItem(position).subLabel == null) return 1;
        return 2;
    }

    @Override
    public int getViewTypeCount()
    {
        return 3;
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
        TextView fTextView2;
        
        public Handler(View v)
        {
            fTextView = (TextView)v.findViewById(R.id.textView);
            fTextView2 = (TextView)v.findViewById(R.id.textView2);
        }
        
        public void setContent(Row t)
        {
            fTextView.setText(t.label);
            if (t.subLabel != null) fTextView2.setText(t.subLabel);
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
            } else if (s.subLabel == null) {
                convertView = i.inflate(R.layout.list_item, null);
            } else {
                convertView = i.inflate(R.layout.list_twoitem, null);
            }
            convertView.setTag(h = new Handler(convertView));
        }

        h.setContent(s);
        return convertView;
    }
}


