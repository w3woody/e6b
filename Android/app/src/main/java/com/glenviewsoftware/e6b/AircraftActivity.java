/*  AircraftActivity.java
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

package com.glenviewsoftware.e6b;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.glenviewsoftware.e6b.adapters.AircraftAdapter;
import com.glenviewsoftware.e6b.calc.wb.AircraftDatabase;
import com.glenviewsoftware.e6b.calc.wb.AircraftPicker;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;
import com.glenviewsoftware.e6b.editor.EditorActivity;
import com.glenviewsoftware.e6b.editor.aircraft.AircraftData;
import com.glenviewsoftware.e6b.editor.aircraft.AircraftDataEditor;
import com.glenviewsoftware.e6b.utils.AlertUtil;

public class AircraftActivity extends Activity implements AircraftAdapter.Callback
{
    private ListView fList;
    private AircraftAdapter fAdapter;

    protected void onCreate(Bundle savedInstanceState)
    { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aircrafteditor);
        
        fList = (ListView)findViewById(R.id.list);
        fAdapter = new AircraftAdapter(this);
        fAdapter.setCallback(this);
        fList.setAdapter(fAdapter);
        
        View v = findViewById(R.id.add);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw)
            {
                doAdd();
            }
        });
        
        v = findViewById(R.id.duplicate);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw)
            {
                doDuplicate();
            }
        });
    }
    
    private void doAdd()
    {
        int index = 1;
        String name;

        for (;;) {
            if (index == 1) {
                name = "untitled";
            } else {
                name = "untitled " + index;
            }
            ++index;
            
            if (AircraftDatabase.shared().aircraftForName(name) == null) break;
        }
        
        try {
            if (-1 == AircraftDatabase.shared().addUserAircraft(name)) {
                addError();
            }
            
            fAdapter.refreshList();
        }
        catch (IOException ex) {
            addError();
        }
    }
    
    private void addError()
    {
        AlertUtil.message(this, "Unable to add", "A problem occured while attempting to add a new aircraft");
    }
    
    private void doDuplicate()
    {
        new AircraftPicker(this,new AircraftPicker.Callback() {
            @Override
            public void select(String name)
            {
                // TODO Auto-generated method stub
                WBAircraft w = AircraftDatabase.shared().aircraftForName(name);
                
                int index = name.indexOf(" copy");
                if (index != -1) {
                    name = name.substring(0, index);
                }
                int ix = 1;
                for (;;) {
                    String test;
                    if (ix == 1) {
                        test = name + " copy";
                    } else {
                        test = name + " copy " + ix;
                    }
                    ++ix;
                    if (AircraftDatabase.shared().aircraftForName(test) == null) {
                        name = test;
                        break;
                    }
                }
                
                w = new WBAircraft(w);
                w.setName(name);
                try {
                    AircraftDatabase.shared().saveNewAircraft(w);
                    fAdapter.refreshList();
                }
                catch (IOException e) {
                    AlertUtil.message(AircraftActivity.this, "Unable to duplicate", "I/O error while duplicating aircraft");
                    return;
                }
            }

            @Override
            public void cancel()
            {
            }
        });
    }

    @Override
    public void doDelete(final int index)
    {
        String name = AircraftDatabase.shared().aircraftNameForUserIndex(index);
                
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Are you sure you wish to delete " + name + "? This operation cannot be undone");
        
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                AircraftDatabase.shared().deleteUserAircraft(index);
                fAdapter.refreshList();
            }
        });
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        
        builder.show();
    }

    @Override
    public void doClick(int index)
    {
        AircraftData ad = new AircraftData(index);
        AircraftDataEditor ae = new AircraftDataEditor();
        
        /* Start editor */
        Intent intent = new Intent(this,EditorActivity.class);
        intent.putExtra("data", ad);
        intent.putExtra("source", ae);
        startActivity(intent);
    }
}


