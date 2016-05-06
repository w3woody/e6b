/*  AircraftPicker.java
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

package com.glenviewsoftware.e6b.calc.wb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.glenviewsoftware.e6b.R;
import com.glenviewsoftware.e6b.adapters.AircraftPickerAdapter;

public class AircraftPicker
{
    public interface Callback
    {
        void select(String name);
        void cancel();
    }

    private AlertDialog fDialog;
    
    public AircraftPicker(Context ctx, final Callback c)
    {
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        LayoutInflater i = LayoutInflater.from(ctx);
        final View v = i.inflate(R.layout.aircraftpicker, null);
        b.setTitle("Select aircraft");
        b.setView(v);
        
        ListView lv = (ListView)v.findViewById(R.id.list);
        final AircraftPickerAdapter picker = new AircraftPickerAdapter(ctx);
        lv.setAdapter(picker);
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
            {
                AircraftPickerAdapter.Row r = picker.getItem(index);
                
                int g = r.getGroupID();
                int ix = r.getItemID();
                
                String name;
                if (g == -1) {
                    name = AircraftDatabase.shared().aircraftNameForUserIndex(ix);
                } else {
                    name = AircraftDatabase.shared().aircraftNameInGroup(ix, g);
                }
                
                if (c != null) {
                    c.select(name);
                }
               
                fDialog.dismiss();
            }
        });
        
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                c.cancel();
            }
        });
        b.setCancelable(true);
        b.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                c.cancel();
            }
        });
        
        fDialog = b.create();
        fDialog.show();
    }
}
