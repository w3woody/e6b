/*  WBData.java
 *
 *  Created on Dec 25, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.calc.wb.aircraft;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.glenviewsoftware.e6b.calc.Value;
import com.glenviewsoftware.e6b.utils.XMLUtils;
import com.glenviewsoftware.e6b.xml.XMLWriter;

/**
 * Weight/Balance record
 */
public class WBData
{
    private String name;
    private String aircraft;
    
    private Value aircraftWeight;
    private Value aircraftArm;
    private int aircraftMomentUnit;
    
    private ArrayList<WBRow> list;
    private ArrayList<WBFRow> fuel;
    
    private int weightUnit;
    private int armUnit;
    private int momentumUnit;
    
    public WBData()
    {
        name = "Untitled";
        aircraft = "";
        
        aircraftWeight = new Value(0,0);
        aircraftArm = new Value(0,0);
        list = new ArrayList<WBRow>();
        fuel = new ArrayList<WBFRow>();
        
        fuel.add(new WBFRow());
        list.add(new WBRow());
        list.add(new WBRow());
    }
    
    public WBData(String n, String a, Value aw, Value aa, int amu, int wu, int au, int mu)
    {
        name = n;
        aircraft = a;
        aircraftWeight = aw;
        aircraftArm = aa;
        aircraftMomentUnit = amu;
        weightUnit = wu;
        armUnit = au;
        momentumUnit = mu;
        
        list = new ArrayList<WBRow>();
        fuel = new ArrayList<WBFRow>();
    }


    public WBData(WBData data)
    {
        name = data.name;
        aircraft = data.aircraft;

        aircraftWeight = data.aircraftWeight;
        aircraftArm = data.aircraftArm;
        aircraftMomentUnit = data.aircraftMomentUnit;
        
        weightUnit = data.weightUnit;
        armUnit = data.armUnit;
        momentumUnit = data.momentumUnit;
        
        list = new ArrayList<WBRow>();
        fuel = new ArrayList<WBFRow>();
        
        for (WBRow r: data.list) {
            list.add(new WBRow(r));
        }
        for (WBFRow r: data.fuel) {
            fuel.add(new WBFRow(r));
        }
    }

    /**
     * Load from node. Format:
     * 
     */
    public WBData(Element e)
    {
        name = e.getAttribute("name");
        aircraft = e.getAttribute("aircraft");
        weightUnit = Integer.parseInt(e.getAttribute("wunit"));
        armUnit = Integer.parseInt(e.getAttribute("aunit"));
        momentumUnit = Integer.parseInt(e.getAttribute("munit"));
        
        list = new ArrayList<WBRow>();
        fuel = new ArrayList<WBFRow>();
        
        NodeList nl = e.getElementsByTagName("datum");
        int i,len = nl.getLength();
        for (i = 0; i < len; ++i) {
            Element elem = (Element)nl.item(i);
            Value wv,av,vv;
            int vtype = 0;
            int munit = Integer.parseInt(elem.getAttribute("unit"));
            double val = 0;
            int unit = 0;
            
            Element tmpElem = XMLUtils.findElement(elem, "weight");
            if (tmpElem != null) {
                val = Double.parseDouble(XMLUtils.getValue(tmpElem));
                unit = Integer.parseInt(tmpElem.getAttribute("unit"));
            }
            wv = new Value(val,unit);
            
            tmpElem = XMLUtils.findElement(elem, "volume");
            if (tmpElem != null) {
                val = Double.parseDouble(XMLUtils.getValue(tmpElem));
                unit = Integer.parseInt(tmpElem.getAttribute("unit"));
                vtype = Integer.parseInt(tmpElem.getAttribute("fueltype"));
            }
            vv = new Value(val,unit);
            
            tmpElem = XMLUtils.findElement(elem, "arm");
            if (tmpElem != null) {
                val = Double.parseDouble(XMLUtils.getValue(tmpElem));
                unit = Integer.parseInt(tmpElem.getAttribute("unit"));
            }
            av = new Value(val,unit);
            
            String item = elem.getAttribute("item");
            if ("aircraft".equals(item)) {
                aircraftMomentUnit = munit;
                aircraftWeight = wv;
                aircraftArm = av;
                
            } else if ("fuel".equals(item)) {
                String nm = elem.getAttribute("name");
                if (nm.length() == 0) nm = "Fuel Tank";
                
                WBFRow frow = new WBFRow(nm,vv,av,munit,vtype);
                fuel.add(frow);
                
            } else {
                WBRow row = new WBRow(wv,av,munit);
                list.add(row);
            }
        }
    }

    /**
     * Write XML data
     * @param w
     * @throws IOException 
     */
    public void writeXML(XMLWriter w) throws IOException
    {
        w.startTag("wb");
        w.addAttribute("name", name);
        w.addAttribute("aircraft",aircraft);
        w.addAttribute("wunit",weightUnit);
        w.addAttribute("aunit",armUnit);
        w.addAttribute("munit",momentumUnit);
     
        /* Aircraft */
        w.startTag("datum");
        w.addAttribute("item", "aircraft");
        w.addAttribute("unit", aircraftMomentUnit);

        w.startTag("weight");
        w.addAttribute("unit", aircraftWeight.getUnit());
        w.write(Double.toString(aircraftWeight.getValue()));
        w.endTag();
        
        w.startTag("arm");
        w.addAttribute("unit", aircraftArm.getUnit());
        w.write(Double.toString(aircraftArm.getValue()));
        w.endTag();
        
        w.endTag();
        
        /* Fuel */
        for (WBFRow r: fuel) {
            w.startTag("datum");
            w.addAttribute("item", "fuel");
            w.addAttribute("unit", r.getMomentUnit());
            w.addAttribute("name", r.getName());
            
            w.startTag("volume");
            w.addAttribute("unit", r.getVolume().getUnit());
            w.addAttribute("fueltype", r.getFuelType());
            w.write(Double.toString(r.getVolume().getValue()));
            w.endTag();
            
            w.startTag("arm");
            w.addAttribute("unit", r.getArm().getUnit());
            w.write(Double.toString(r.getArm().getValue()));
            w.endTag();

            w.endTag();
        }
        
        /* Datum */
        for (WBRow r: list) {
            w.startTag("datum");
            w.addAttribute("unit", r.getMomentUnit());

            w.startTag("weight");
            w.addAttribute("unit", r.getWeight().getUnit());
            w.write(Double.toString(r.getWeight().getValue()));
            w.endTag();
            
            w.startTag("arm");
            w.addAttribute("unit", r.getArm().getUnit());
            w.write(Double.toString(r.getArm().getValue()));
            w.endTag();

            w.endTag();
        }
        
        w.endTag();
    }

    public String getName()
    {
        return name;
    }

    public String getAircraft()
    {
        return aircraft;
    }
    
    public ArrayList<WBRow> getList()
    {
        return list;
    }

    public Value getAircraftWeight()
    {
        return aircraftWeight;
    }

    public Value getAircraftArm()
    {
        return aircraftArm;
    }

    public int getAircraftMomentUnit()
    {
        return aircraftMomentUnit;
    }

    public ArrayList<WBFRow> getFuel()
    {
        return fuel;
    }

    public int getWeightUnit()
    {
        return weightUnit;
    }

    public int getArmUnit()
    {
        return armUnit;
    }

    public int getMomentumUnit()
    {
        return momentumUnit;
    }

    public void setName(String v)
    {
        name = v;
    }

    public void setAircraft(String v)
    {
        aircraft = v;
    }

    public void setAircraftWeight(Value v)
    {
        aircraftWeight = v;
    }

    public void setAircraftArm(Value v)
    {
        aircraftArm = v;
    }

    public void setAircraftMomentUnit(int v)
    {
        aircraftMomentUnit = v;
    }

    public void setWeightUnit(int v)
    {
        weightUnit = v;
    }

    public void setArmUnit(int v)
    {
        armUnit = v;
    }

    public void setMomentumUnit(int v)
    {
        momentumUnit = v;
    }

}


