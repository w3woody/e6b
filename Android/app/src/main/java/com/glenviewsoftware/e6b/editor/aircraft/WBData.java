/*  WBData.java
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

package com.glenviewsoftware.e6b.editor.aircraft;

import java.io.IOException;

import com.glenviewsoftware.e6b.calc.wb.AircraftDatabase;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftDataPoint;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftWBRange;
import com.glenviewsoftware.e6b.editor.EditorData;

public class WBData implements EditorData
{
    private int fAircraftID;
    private int fDataRow;

    private String name;
    
    public WBData(int a, int dRow)
    {
        fAircraftID = a;
        fDataRow = dRow;
        
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(dRow);
        name = d.getName();
    }
    
    public int getAircraftID()
    {
        return fAircraftID;
    }
    
    public int getDataRow()
    {
        return fDataRow;
    }
    
    public String getName()
    {
        return name;
    }


    public void setName(String v)
    {
        name = v;
    }

    public void save() throws IOException
    {
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(fDataRow);
        
        d.setName(name);

        AircraftDatabase.shared().save(ac, true);
    }

    public int getDataCount()
    {
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(fDataRow);
        return d.getData().size();
    }
    
    public WBAircraftDataPoint getDataPoint(int ct)
    {
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(fDataRow);
        return d.getData().get(ct);
    }

    public void deleteRow(int row) throws IOException
    {
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(fDataRow);
        d.getData().remove(row);
        AircraftDatabase.shared().save(ac, true);
    }

    public void insertRow() throws IOException
    {
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(fDataRow);
        d.getData().add(new WBAircraftDataPoint(0,0));
        AircraftDatabase.shared().save(ac, true);
    }
}


