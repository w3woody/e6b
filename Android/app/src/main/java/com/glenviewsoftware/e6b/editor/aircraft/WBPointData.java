/*  WBPointData.java
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

public class WBPointData implements EditorData
{
    private int fAircraftID;
    private int fDataRow;
    private int fItem;

    private double weight;
    private double arm;

    public WBPointData(int a, int dRow, int item)
    {
        fAircraftID = a;
        fDataRow = dRow;
        fItem = item;
        
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(dRow);
        WBAircraftDataPoint data = d.getData().get(item);
        
        weight = data.getWeight();
        arm = data.getArm();
    }

    public double getWeight()
    {
        return weight;
    }
    
    public double getArm()
    {
        return arm;
    }
    
    public void setWeight(double w)
    {
        weight = w;
    }

    public void setArm(double a)
    {
        arm = a;
    }
    
    public void save() throws IOException
    {
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange d = ac.getData().get(fDataRow);
        d.getData().set(fItem, new WBAircraftDataPoint(weight,arm));

        AircraftDatabase.shared().save(ac, true);
    }
}


