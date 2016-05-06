/*  AircraftDataEditor.java
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

import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftStation;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftWBRange;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBFuelTank;
import com.glenviewsoftware.e6b.editor.EditorActivity;
import com.glenviewsoftware.e6b.editor.EditorData;
import com.glenviewsoftware.e6b.editor.EditorDataSource;
import com.glenviewsoftware.e6b.units.Measurement;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.utils.AlertUtil;

public class AircraftDataEditor implements EditorDataSource
{

    @Override
    public String editorTitle(EditorActivity editor, EditorData groupData)
    {
        return ((AircraftData)groupData).getName();
    }

    @Override
    public int numberOfSections(EditorActivity editor, EditorData groupData)
    {
        return 6;
    }

    @Override
    public String headerOfSection(EditorActivity editor, int section, EditorData groupData)
    {
        switch (section) {
            case 0: return "Aircraft Name";
            case 1: return "Measurement Units";
            case 2: return "Aircraft Data";
            case 3: return "Station Locations";
            case 4: return "Fuel Tanks";
            case 5: return "Weight/Balance Envelope";
        }
        return null;
    }

    @Override
    public int numberOfRowsInSection(EditorActivity editor, int section, EditorData groupData)
    {
        AircraftData a = (AircraftData)groupData;
        
        switch (section) {
            case 0: return 3;
            case 1: return 4;
            case 2: return 4;
            case 3: return a.getStations().size();
            case 4: return a.getFuel().size();
            case 5: return a.getData().size();
        }
        return 0;
    }

    @Override
    public boolean sectionIsArray(EditorActivity editor, int index, EditorData groupData)
    {
        if ((index == 3) || (index == 4) || (index == 5)) return true;
        return false;
    }

    @Override
    public String rowLabel(EditorActivity editor, int row, int section, EditorData groupData)
    {
        AircraftData a = (AircraftData)groupData;
        
        switch (section) {
            case 0:
                switch (row) {
                    case 0: return "Manufacturer";
                    case 1: return "Aircraft Type";
                    case 2: return "Tail Number";
                }
                return null;
            case 1:
                switch (row) {
                    case 0: return "Weight";
                    case 1: return "Arm";
                    case 2: return "Moment";
                    case 3: return "Speed";
                }
                return null;
            case 2:
                switch (row) {
                    case 0: return "Va";
                    case 1: return "Max Weight (for Va)";
                    case 2: return "Empty Weight";
                    case 3: return "Empty Arm";
                }
                return null;
            case 3:
                return a.getStations().get(row).getName();
            case 4:
                return a.getFuel().get(row).getName();
            case 5:
                return a.getData().get(row).getName();
        }
        return null;
    }
    
    private static String countAsString(int ct)
    {
        if (ct == 1) return "1 item";
        return ct + " items";
    }
    
    private static String format(double val)
    {
        return String.format("%.2f", val);
    }

    @Override
    public String rowData(EditorActivity editor, int row, int section, EditorData groupData)
    {
        AircraftData a = (AircraftData)groupData;
        
        switch (section) {
            case 0:
                switch (row) {
                    case 0: return a.getMaker();
                    case 1: return a.getModel();
                    case 2: return a.getName();
                }
                return null;
            case 1:
                switch (row) {
                    case 0: return Units.weight.getMeasurement(a.getWeightUnit());
                    case 1: return Units.length.getMeasurement(a.getArmUnit());
                    case 2: return Units.moment.getMeasurement(a.getMomentUnit());
                    case 3: return Units.speed.getMeasurement(a.getSpeedUnit());
                }
                return null;
            case 2:
                switch (row) {
                    case 0: return format(a.getVA());
                    case 1: return format(a.getWMax());
                    case 2: return format(a.getWeight());
                    case 3: return format(a.getArm());
                }
                return null;
            case 3:
                {
                    WBAircraftStation s = a.getStations().get(row);
                    return format(s.getArm());
                }
            case 4:
                {
                    WBFuelTank t = a.getFuel().get(row);
                    return format(t.getVolume());
                }
            case 5:
                {
                    WBAircraftWBRange r = a.getData().get(row);
                    return countAsString(r.getData().size());
                }
        }
        return null;
    }

    @Override
    public int rowType(EditorActivity editor, int row, int section, EditorData groupData)
    {
        switch (section) {
            case 0: return ROWTYPE_TEXT;
            case 1: return ROWTYPE_MENU;
            case 2: return ROWTYPE_NUMERIC;
            case 3: return ROWTYPE_CHILD;
            case 4: return ROWTYPE_CHILD;
            case 5: return ROWTYPE_CHILD;
        }
        return 0;
    }

    @Override
    public EditorData childAt(EditorActivity editor, int row, int section, EditorData groupData)
    {
        int ix = ((AircraftData)groupData).aircraftID();
        switch (section) {
            case 3: // stations
                return new StationData(ix,row);
            case 4: // fuel
                return new FuelData(ix,row);
            case 5: // data
                return new WBData(ix,row);
        }
        return null;
    }

    @Override
    public EditorDataSource editorAt(EditorActivity editor, int row, int section, EditorData groupData)
    {
        switch (section) {
            case 3: // stations
                return new StationDataEditor();
            case 4: // fuel
                return new FuelDataEditor();
            case 5: // data
                return new WBDataEditor();
        }
        return null;
    }

    @Override
    public void updateRow(EditorActivity editor, int row, int section, EditorData groupData, String value) throws IOException
    {
        AircraftData a = (AircraftData)groupData;
        switch (section) {
            case 0:
                switch (row) {
                    case 0: 
                        a.setMaker(value);
                        break;
                    case 1:
                        a.setModel(value);
                        break;
                    case 2:
                        if (!a.renameAircraft(value)) {
                            AlertUtil.message(editor, "Error", "Unable to rename aircraft");
                        } else {
                            editor.reloadTitle();
                        }
                        break;
                }
                break;
            case 2:
                try {
                    switch (row) {
                        case 0:
                            a.setVA(Double.parseDouble(value));
                            break;
                        case 1:
                            a.setWMax(Double.parseDouble(value));
                            break;
                        case 2:
                            a.setWeight(Double.parseDouble(value));
                            break;
                        case 3:
                            a.setArm(Double.parseDouble(value));
                            break;
                    }
                }
                catch (NumberFormatException n) {
                    AlertUtil.message(editor, "Error", "Number format error");
                }
                break;
        }
    }

    @Override
    public void deleteRow(EditorActivity editor, int row, int section, EditorData groupData) throws IOException
    {
        // TODO
        switch (section) {
            case 3: // stations
                ((AircraftData)groupData).deleteStation(row);
                break;
            case 4: // fuel
                ((AircraftData)groupData).deleteFuelTank(row);
                break;
            case 5: // data
                ((AircraftData)groupData).deleteData(row);
                break;
        }
    }

    @Override
    public void insertRow(EditorActivity editor, int section, EditorData groupData) throws IOException
    {
        switch (section) {
            case 3: // stations
                ((AircraftData)groupData).insertStation();
                break;
            case 4: // fuel
                ((AircraftData)groupData).insertFuelTank();
                break;
            case 5: // data
                ((AircraftData)groupData).insertData();
                break;
        }
    }


    @Override
    public void save(EditorActivity editor, EditorData data)
    {
    }

    @Override
    public String[] menuList(EditorActivity editor, int row, int sec, EditorData data)
    {
        if (sec == 1) {
            ArrayList<String> a = new ArrayList<String>();
            Measurement m;
            switch (row) {
                default:
                case 0:
                    m = Units.weight;
                    break;
                case 1:
                    m = Units.length;
                    break;
                case 2:
                    m = Units.moment;
                    break;
                case 3:
                    m = Units.speed;
                    break;
            }
            int len = m.numUnits();
            for (int i = 0; i < len; ++i) {
                a.add(m.getMeasurement(i));
            }
            return a.toArray(new String[a.size()]);
        } else {
            return null;
        }
    }

    @Override
    public void setSelectedMenu(EditorActivity editor, int row, int sec, EditorData data, int which) throws IOException
    {
        if (sec == 1) {
            AircraftData a = (AircraftData)data;
            switch (row) {
                default:
                case 0:
                    a.setWeightUnit(which);
                    break;
                case 1:
                    a.setArmUnit(which);
                    break;
                case 2:
                    a.setMomentUnit(which);
                    break;
                case 3:
                    a.setSpeedUnit(which);
                    break;
            }
        }
    }

    @Override
    public void onPause(EditorActivity editor, EditorData fData)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onResume(EditorActivity editor, EditorData fData)
    {
        // TODO Auto-generated method stub
        
    }
}


