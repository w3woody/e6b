/*  FuelData.java
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
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBFuelTank;
import com.glenviewsoftware.e6b.editor.EditorData;

public class FuelData implements EditorData
{
    private int fAircraftID;
    private int fFuelTankRow;
    
    private double volume;
    private double arm;
    private String name;
    private int fuelType;
    private int fuelUnit;


    public FuelData(int a, int ftRow)
    {
        fAircraftID = a;
        fFuelTankRow = ftRow;

        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBFuelTank tank = ac.getFuel().get(fFuelTankRow);
        
        name = tank.getName();
        volume = tank.getVolume();
        arm = tank.getArm();
        fuelType = tank.getFuelType();
        fuelUnit = tank.getFuelUnit();
    }


    public double getVolume()
    {
        return volume;
    }


    public void setVolume(double v)
    {
        volume = v;
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


    public int getFuelType()
    {
        return fuelType;
    }


    public void setFuelType(int v)
    {
        fuelType = v;
    }


    public int getFuelUnit()
    {
        return fuelUnit;
    }


    public void setFuelUnit(int v)
    {
        fuelUnit = v;
    }


    public void save() throws IOException
    {
        WBFuelTank tank = new WBFuelTank(volume,arm,name,fuelType,fuelUnit);
        WBAircraft ac = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        ac.getFuel().set(fFuelTankRow, tank);
        AircraftDatabase.shared().save(ac, true);
    }
    
}


