/*  StationData.java
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
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftStation;
import com.glenviewsoftware.e6b.editor.EditorData;

public class StationData implements EditorData
{
    private int fAircraftID;
    private int fStationRow;
    
    private double arm;
    private String name;
    
    public StationData(int a, int stRow)
    {
        fAircraftID = a;
        fStationRow = stRow;

        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftStation station = ac.getStation().get(stRow);
        
        name = station.getName();
        arm = station.getArm();
    }
    
    public double getArm()
    {
        return arm;
    }


    public void setArm(double v)
    {
        arm = v;
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
        WBAircraftStation station = new WBAircraftStation(arm,name);
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        ac.getStation().set(fStationRow, station);
        AircraftDatabase.shared().save(ac, true);
    }
}


