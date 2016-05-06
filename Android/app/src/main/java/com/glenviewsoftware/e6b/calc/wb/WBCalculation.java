/*  WBCalculation.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.glenviewsoftware.e6b.E6BApplication;
import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.Calculation;
import com.glenviewsoftware.e6b.calc.Value;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBData;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBFRow;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBFuelTank;
import com.glenviewsoftware.e6b.units.Length;
import com.glenviewsoftware.e6b.units.Weight;
import com.glenviewsoftware.e6b.xml.XMLWriter;

public class WBCalculation implements Calculation
{
    private WBData data;
    private WBAircraft aircraft;

    public WBCalculation()
    {
        data = new WBData();
        aircraft = null;
    }

    public WBCalculation(WBData d)
    {
        data = d;
        setAircraftName(data.getAircraft());
    }

    public WBCalculation(WBCalculation c)
    {
        data = new WBData(c.data);
        aircraft = c.aircraft;
    }

    /************************************************************************/
    /*                                                                      */
    /*  WB Rows                                                             */
    /*                                                                      */
    /************************************************************************/

    @Override
    public String getCalculationName()
    {
        return data.getName();
    }

    @Override
    public CalcManager getCalculationManager()
    {
        return new WBCalcManager(this);
    }

    public void setAircraftName(String name)
    {
        data.setAircraft(name);
        aircraft = AircraftDatabase.shared().aircraftForName(name);
        if (aircraft == null) return;

        data.setAircraftWeight(new Value(aircraft.getWeight(),aircraft.getWeightUnit()));
        data.setAircraftArm(new Value(aircraft.getArm(),aircraft.getArmUnit()));

        data.getFuel().clear();
        for (WBFuelTank tank: aircraft.getFuel()) {
            WBFRow row = new WBFRow(tank.getName(),
                    new Value(tank.getVolume(),tank.getFuelUnit()),
                    new Value(tank.getArm(),data.getArmUnit()),
                    data.getMomentumUnit(),
                    tank.getFuelType());
            data.getFuel().add(row);
        }
    }

    public WBData getData()
    {
        return data;
    }

    public WBAircraft getAircraft()
    {
        return aircraft;
    }


    public int getWeightUnit()
    {
        if (aircraft == null) return Weight.WEIGHT_LBS;
        else return aircraft.getWeightUnit();
    }
    
    public int getArmUnit()
    {
        if (aircraft == null) return Length.LENGTH_INCHES;
        return aircraft.getArmUnit();
    }

    /************************************************************************/
    /*                                                                      */
    /*  File I/O                                                            */
    /*                                                                      */
    /************************************************************************/

    private static ArrayList<WBCalculation> gArray;

    public static List<WBCalculation> getWBValues()
    {
        loadWBFiles();
        return gArray;
    }

    /**
     * Load the weight and balance files
     */
    public static void loadWBFiles()
    {
        if (gArray == null) {
            gArray = new ArrayList<WBCalculation>();

            try {
                FileInputStream out = E6BApplication.shared().openFileInput("wb.data");
                ObjectInputStream ois = new ObjectInputStream(out);

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(ois);
                
                ois.close();

                NodeList nl = doc.getDocumentElement().getElementsByTagName("wb");
                int i,len = nl.getLength();
                for (i = 0; i < len; ++i) {
                    Element e = (Element)nl.item(i);

                    WBData data = new WBData(e);
                    WBCalculation calc = new WBCalculation(data);
                    gArray.add(calc);
                }
            }
            catch (Exception ex) {
                /* Error: delete the file */
                Log.d("E6B","File wb.data unreadable; removing.",ex);
            }

            if (gArray.size() == 0) {
                gArray.add(new WBCalculation());
            }
        }
    }

    public static void saveWBFiles()
    {
        try {
            FileOutputStream out = E6BApplication.shared().openFileOutput("wb.data", 0);
            ObjectOutputStream oos = new ObjectOutputStream(out);

            OutputStreamWriter writer = new OutputStreamWriter(oos,"UTF-8");
            XMLWriter w = new XMLWriter(writer);

            w.startTag("wbdata");
            for (WBCalculation wc: gArray) {
                wc.data.writeXML(w);
            }
            w.endTag();
            w.flush();
            w.close();
        }
        catch (Exception ex) {
            Log.d("E6B","Save WB Files failed",ex);
        }
    }
    
    /**
     * Create a new W&B file
     * @return
     */
    public static int createNewWBFile()
    {
        int len = gArray.size();
        int n = 1;
        
        WBCalculation calc = new WBCalculation();

        for (;;) {
            String str;
            if (n == 1) str = "Untitled";
            else str = "Untitled " + n;
            ++n;

            boolean found = false;
            for (WBCalculation c: gArray) {
                if (str.equals(c.getData().getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                calc.getData().setName(str);
                break;
            }
        }
        
        gArray.add(calc);
        return len;
    }
}


