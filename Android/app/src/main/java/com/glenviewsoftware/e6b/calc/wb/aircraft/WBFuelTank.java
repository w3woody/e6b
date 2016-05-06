/*  WBFuelTank.java
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

import org.w3c.dom.Element;

import com.glenviewsoftware.e6b.utils.XMLUtils;
import com.glenviewsoftware.e6b.xml.XMLWriter;

public class WBFuelTank
{
    private double volume;
    private double arm;
    private String name;
    private int fuelType;
    private int fuelUnit;
    
    public static final int FUELTYPE_UNKNOWN = 0;
    public static final int FUELTYPE_AVGAS = 1;
    public static final int FUELTYPE_KEROSENE = 2;
    public static final int FUELTYPE_JETA = 3;
    
    public WBFuelTank(double v, double a, String n, int ft, int fu)
    {
        volume = v;
        arm = a;
        name = n;
        fuelType = ft;
        fuelUnit = fu;
    }
    
    public WBFuelTank(Element e)
    {
        name = e.getAttribute("name");
        fuelType = Integer.parseInt(e.getAttribute("fueltype"));
        
        Element el = XMLUtils.findElement(e, "volume");
        
        fuelUnit = Integer.parseInt(el.getAttribute("unit"));
        volume = Double.parseDouble(XMLUtils.getValue(el));
        
        arm = Double.parseDouble(XMLUtils.getValue(e,"arm"));
    }

    public WBFuelTank(WBFuelTank t)
    {
        volume = t.volume;
        arm = t.arm;
        name = t.name;
        fuelType = t.fuelType;
        fuelUnit = t.fuelUnit;
    }

    public double getVolume()
    {
        return volume;
    }

    public double getArm()
    {
        return arm;
    }

    public String getName()
    {
        return name;
    }

    public int getFuelType()
    {
        return fuelType;
    }

    public int getFuelUnit()
    {
        return fuelUnit;
    }

    public void toXML(XMLWriter writer) throws IOException
    {
        writer.startTag("tank");
        writer.addAttribute("name", name);
        writer.addAttribute("fueltype", fuelType);
        
        writer.startTag("volume");
        writer.addAttribute("unit", fuelUnit);
        writer.write(Double.toString(volume));
        writer.endTag();
        
        writer.startTag("arm");
        writer.write(Double.toString(arm));
        writer.endTag();
        
        writer.endTag();
    }
}


