/*  WBAircraft.java
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

import com.glenviewsoftware.e6b.utils.XMLUtils;
import com.glenviewsoftware.e6b.xml.XMLWriter;

public class WBAircraft
{
    private String maker;
    private String model;
    private String name;
    
    private int weightUnit;
    private int armUnit;
    private int momentUnit;
    private int speedUnit;
    
    private double va;
    private double wmax;
    private double weight;
    private double arm;
    
    private ArrayList<WBFuelTank> fuel;
    private ArrayList<WBAircraftStation> station;
    private ArrayList<WBAircraftWBRange> data;
    
    public WBAircraft(String mk, String md, String n, int wu, int au, int mu, int su, 
            double v, double wm, double w, double a)
    {
        maker = mk;
        model = md;
        name = n;
        weightUnit = wu;
        armUnit = au;
        momentUnit = mu;
        speedUnit = su;
        va = v;
        wmax = wm;
        weight = w;
        arm = a;
        
        fuel = new ArrayList<WBFuelTank>();
        station = new ArrayList<WBAircraftStation>();
        data = new ArrayList<WBAircraftWBRange>();
    }
    
    /**
     * Parse the contents of the aircraft object from the element
     */
    public WBAircraft(Element elem)
    {
        fuel = new ArrayList<WBFuelTank>();
        station = new ArrayList<WBAircraftStation>();
        data = new ArrayList<WBAircraftWBRange>();

        /*
         *  The XML for the WBAircraft object is:
         *
         *      <aircraft name="name" wunit="0" aunit="0" munit="0" sunit="0">
         *          <va>155</va>
         *          <wmax>3000</va>
         *          <weight>1450</weight>
         *          <arm>38</arm>
         *          <fuel>
         *              <tank name="name" fueltype="1">
         *                  <volume unit="0">56</volume>
         *                  <arm>38.5</arm>
         *              </tank>
         *          </fuel>
         *          <station>
         *              <arm name="Location">37</arm>
         *              <arm name="Location 2">39</arm>
         *          </station>
         *          <data>
         *              <range name="utility">     <!-- Multiple sets -->
         *                  <point>              <!-- Multiple points -->
         *                      <weight>2050</weight>
         *                      <arm>38.5</arm>
         *                  </point>
         *                  <point>
         *                      <weight>2950</weight>
         *                      <arm>42.5</arm>
         *                  </point>
         *              </range>
         *          </data>
         *      </aircraft>
         */

        name = elem.getAttribute("name");
        maker = elem.getAttribute("maker");
        model = elem.getAttribute("model");
        
        weightUnit = Integer.parseInt(elem.getAttribute("wunit"));
        armUnit = Integer.parseInt(elem.getAttribute("aunit"));
        momentUnit = Integer.parseInt(elem.getAttribute("munit"));
        speedUnit = Integer.parseInt(elem.getAttribute("sunit"));
        
        va = Double.parseDouble(XMLUtils.getValue(elem,"va"));
        wmax = Double.parseDouble(XMLUtils.getValue(elem,"wmax"));
        weight = Double.parseDouble(XMLUtils.getValue(elem,"weight"));
        arm = Double.parseDouble(XMLUtils.getValue(elem,"arm"));
        
        NodeList nl = XMLUtils.findElement(elem, "fuel", "tank");
        int i,len = nl.getLength();
        for (i = 0; i < len; ++i) {
            Element tank = (Element)nl.item(i);
            fuel.add(new WBFuelTank(tank));
        }
        
        nl = XMLUtils.findElement(elem, "station", "arm");
        len = nl.getLength();
        for (i = 0; i < len; ++i) {
            Element as = (Element)nl.item(i);
            station.add(new WBAircraftStation(as));
        }
        
        nl = XMLUtils.findElement(elem, "data", "range");
        len = nl.getLength();
        for (i = 0; i < len; ++i) {
            Element ar = (Element)nl.item(i);
            data.add(new WBAircraftWBRange(ar));
        }
    }
    
    public WBAircraft(WBAircraft src)
    {
        maker = src.maker;
        model = src.model;
        name = src.name;
        weightUnit = src.weightUnit;
        armUnit = src.armUnit;
        momentUnit = src.momentUnit;
        speedUnit = src.speedUnit;
        va = src.va;
        wmax = src.wmax;
        weight = src.weight;
        arm = src.arm;
        
        fuel = new ArrayList<WBFuelTank>();
        for (WBFuelTank t: src.fuel) {
            fuel.add(new WBFuelTank(t));
        }
        station = new ArrayList<WBAircraftStation>();
        for (WBAircraftStation s: src.station) {
            station.add(new WBAircraftStation(s));
        }
        data = new ArrayList<WBAircraftWBRange>();
        for (WBAircraftWBRange r: src.data) {
            data.add(new WBAircraftWBRange(r));
        }
    }
    
    public WBAircraft(String n)
    {
        name = n;
        fuel = new ArrayList<WBFuelTank>();
        station = new ArrayList<WBAircraftStation>();
        data = new ArrayList<WBAircraftWBRange>();
    }

    public void toXML(XMLWriter writer) throws IOException
    {
        /*
         * Start aircraft header
         */
        writer.startTag("aircraft");
        writer.addAttribute("name", name);
        writer.addAttribute("maker", maker);
        writer.addAttribute("model", model);
        
        writer.addAttribute("wunit",weightUnit);
        writer.addAttribute("aunit",armUnit);
        writer.addAttribute("munit",momentUnit);
        writer.addAttribute("sunit",speedUnit);
        
        /*
         * Values
         */
        XMLUtils.writeElement(writer,"va",va);
        XMLUtils.writeElement(writer,"wmax",wmax);
        XMLUtils.writeElement(writer,"weight",weight);
        XMLUtils.writeElement(writer,"arm",arm);
        
        /*
         * Fuel
         */
        
        writer.startTag("fuel");
        for (WBFuelTank tank: fuel) {
            tank.toXML(writer);
        }
        writer.endTag();
        
        /*
         * Stations
         */
        writer.startTag("station");
        for (WBAircraftStation s: station) {
            s.toXML(writer);
        }
        writer.endTag();
        
        /*
         * Data
         */
        writer.startTag("data");
        for (WBAircraftWBRange d: data) {
            d.toXML(writer);
        }
        writer.endTag();
        
        writer.endTag();
    }

    public String getMaker()
    {
        return maker;
    }

    public String getModel()
    {
        return model;
    }

    public String getName()
    {
        return name;
    }

    public int getWeightUnit()
    {
        return weightUnit;
    }

    public int getArmUnit()
    {
        return armUnit;
    }

    public int getMomentUnit()
    {
        return momentUnit;
    }

    public int getSpeedUnit()
    {
        return speedUnit;
    }

    public double getVa()
    {
        return va;
    }

    public double getWmax()
    {
        return wmax;
    }

    public double getWeight()
    {
        return weight;
    }

    public double getArm()
    {
        return arm;
    }

    public ArrayList<WBFuelTank> getFuel()
    {
        return fuel;
    }

    public ArrayList<WBAircraftStation> getStation()
    {
        return station;
    }

    public ArrayList<WBAircraftWBRange> getData()
    {
        return data;
    }

    public void setName(String v)
    {
        name = v;
    }

    public void setWeightUnit(int v)
    {
        weightUnit = v;
    }

    public void setArmUnit(int v)
    {
        armUnit = v;
    }

    public void setMomentUnit(int v)
    {
        momentUnit = v;
    }

    public void setSpeedUnit(int v)
    {
        speedUnit = v;
    }

    public void setMaker(String v)
    {
        maker = v;
    }

    public void setModel(String v)
    {
        model = v;
    }

    public void setVa(double v)
    {
        va = v;
    }

    public void setWmax(double v)
    {
        wmax = v;
    }

    public void setWeight(double v)
    {
        weight = v;
    }

    public void setArm(double v)
    {
        arm = v;
    }
}


