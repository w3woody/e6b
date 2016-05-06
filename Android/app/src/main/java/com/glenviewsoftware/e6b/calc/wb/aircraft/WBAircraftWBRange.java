/*  WBAircraftWBRange.java
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

/**
 * Weight and balance polygon
 */
public class WBAircraftWBRange
{
    private String name;
    private ArrayList<WBAircraftDataPoint> data;
    
    public WBAircraftWBRange(String n)
    {
        name = n;
        data = new ArrayList<WBAircraftDataPoint>();
    }

    public WBAircraftWBRange(Element ar)
    {
        name = ar.getAttribute("name");
        
        NodeList nl = ar.getElementsByTagName("point");
        int i,len = nl.getLength();
        
        data = new ArrayList<WBAircraftDataPoint>();
        for (i = 0; i < len; ++i) {
            Element p = (Element)nl.item(i);
            
            double weight = Double.parseDouble(XMLUtils.getValue(p,"weight"));
            double arm = Double.parseDouble(XMLUtils.getValue(p,"arm"));
            
            data.add(new WBAircraftDataPoint(weight,arm));
        }
    }

    public WBAircraftWBRange(WBAircraftWBRange r)
    {
        name = r.name;
        data = new ArrayList<WBAircraftDataPoint>();
        for (WBAircraftDataPoint i: r.data) {
            data.add(new WBAircraftDataPoint(i));
        }
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String v)
    {
        name = v;
    }

    public ArrayList<WBAircraftDataPoint> getData()
    {
        return data;
    }

    public void toXML(XMLWriter writer) throws IOException
    {
        writer.startTag("range");
        writer.addAttribute("name", name);
        
        for (WBAircraftDataPoint pt: data) {
            writer.startTag("point");
            XMLUtils.writeElement(writer, "weight", pt.getWeight());
            XMLUtils.writeElement(writer, "arm", pt.getArm());
            writer.endTag();
        }
        
        writer.endTag();
    }
}


