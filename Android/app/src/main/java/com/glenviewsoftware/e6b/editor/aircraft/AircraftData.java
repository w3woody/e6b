/*  AircraftData.java
 *
 *  Created on Dec 29, 2012 by William Edward Woody
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
import java.util.ArrayList;

import com.glenviewsoftware.e6b.calc.wb.AircraftDatabase;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftStation;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftWBRange;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBFuelTank;
import com.glenviewsoftware.e6b.editor.EditorData;

public class AircraftData implements EditorData
{
    private int fAircraftID;
    
    public AircraftData(int a)
    {
        fAircraftID = a;
    }
    
    public int aircraftID()
    {
        return fAircraftID;
    }

    public String getName()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getName();
    }

    public ArrayList<WBAircraftStation> getStations()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getStation();
    }

    public ArrayList<WBFuelTank> getFuel()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getFuel();
    }

    public ArrayList<WBAircraftWBRange> getData()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getData();
    }

    public String getModel()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        if (a.getModel() == null) return "";
        return a.getModel();
    }

    public String getMaker()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        if (a.getMaker() == null) return "";
        return a.getMaker();
    }

    public int getWeightUnit()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getWeightUnit();
    }

    public int getArmUnit()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getArmUnit();
    }

    public int getMomentUnit()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getMomentUnit();
    }

    public int getSpeedUnit()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getSpeedUnit();
    }

    public double getVA()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getVa();
    }

    public double getWMax()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getWmax();
    }

    public double getArm()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getArm();
    }

    public double getWeight()
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        return a.getWeight();
    }

    public void setWeightUnit(int which) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setWeightUnit(which);
        AircraftDatabase.shared().save(a, true);
    }

    public void setArmUnit(int which) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setArmUnit(which);
        AircraftDatabase.shared().save(a, true);
    }

    public void setMomentUnit(int which) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setMomentUnit(which);
        AircraftDatabase.shared().save(a, true);
    }

    public void setSpeedUnit(int which) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setSpeedUnit(which);
        AircraftDatabase.shared().save(a, true);
    }

    public void setMaker(String value) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setMaker(value);
        AircraftDatabase.shared().save(a, true);
    }

    public void setModel(String value) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setModel(value);
        AircraftDatabase.shared().save(a, true);
    }

    public boolean renameAircraft(String value) throws IOException
    {
        return AircraftDatabase.shared().renameUserAircraft(fAircraftID, value);
    }

    public void setVA(double value) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setVa(value);
        AircraftDatabase.shared().save(a, true);
    }

    public void setWMax(double value) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setWmax(value);
        AircraftDatabase.shared().save(a, true);
    }

    public void setWeight(double value) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setWeight(value);
        AircraftDatabase.shared().save(a, true);
    }

    public void setArm(double value) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.setArm(value);
        AircraftDatabase.shared().save(a, true);
    }

    public void insertFuelTank() throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBFuelTank tank = new WBFuelTank(0,0,"Fuel Tank",0,0);
        a.getFuel().add(tank);
        AircraftDatabase.shared().save(a, true);
    }

    public void deleteFuelTank(int row) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.getFuel().remove(row);
        AircraftDatabase.shared().save(a, true);
    }

    public void insertStation() throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftStation st = new WBAircraftStation(0,"Station");
        a.getStation().add(st);
        AircraftDatabase.shared().save(a, true);
    }

    public void deleteStation(int row) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.getStation().remove(row);
        AircraftDatabase.shared().save(a, true);
    }

    public void deleteData(int row) throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        a.getData().remove(row);
        AircraftDatabase.shared().save(a, true);
    }

    public void insertData() throws IOException
    {
        WBAircraft a = AircraftDatabase.shared().userDefinedAircraft(fAircraftID);
        WBAircraftWBRange st = new WBAircraftWBRange("Envelop");
        a.getData().add(st);
        AircraftDatabase.shared().save(a, true);
    }
}


