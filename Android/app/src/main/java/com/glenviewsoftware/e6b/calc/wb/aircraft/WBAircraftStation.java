/*  WBAircraftStation.java
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

public class WBAircraftStation
{
    private double arm;
    private String name;
    
    public WBAircraftStation(double a, String n)
    {
        arm = a;
        name = n;
    }
    
    public WBAircraftStation(Element as)
    {
        name = as.getAttribute("name");
        arm = Double.parseDouble(XMLUtils.getValue(as));
    }

    public WBAircraftStation(WBAircraftStation s)
    {
        name = s.name;
        arm = s.arm;
    }

    public double getArm()
    {
        return arm;
    }

    public String getName()
    {
        return name;
    }

    public void toXML(XMLWriter writer) throws IOException
    {
        writer.startTag("arm");
        writer.addAttribute("name", name);
        writer.write(Double.toString(arm));
        writer.endTag();
    }
}


