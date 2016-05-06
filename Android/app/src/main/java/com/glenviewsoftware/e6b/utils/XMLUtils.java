/*  XMLUtils.java
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

package com.glenviewsoftware.e6b.utils;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.glenviewsoftware.e6b.xml.XMLWriter;

public class XMLUtils
{
    public static String getValue(Element e)
    {
        StringBuffer b = new StringBuffer();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            b.append(n.getNodeValue());
        }
        
        return b.toString();
    }
    
    /**
     * Get the value given a parent node and the data
     * @param parent
     * @param data
     * @return
     */
    public static String getValue(Element parent, String data)
    {
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element)n;
                if (elem.getTagName().equalsIgnoreCase(data)) {
                    return getValue(elem);
                }
            }
        }
        return null;
    }
    
    public static Element findElement(Element parent, String data)
    {
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element)n;
                if (elem.getTagName().equalsIgnoreCase(data)) {
                    return elem;
                }
            }
        }
        return null;
    }
    
    public static NodeList findElement(Element parent, String root, String data)
    {
        Element e = findElement(parent,root);
        if (e == null) return null;
        return e.getElementsByTagName(data);
    }
    
    public static void writeElement(XMLWriter w, String elem, String value) throws IOException
    {
        w.startTag(elem);
        w.write(value);
        w.endTag();
    }
    
    public static void writeElement(XMLWriter w, String elem, long value) throws IOException
    {
        w.startTag(elem);
        w.write(Long.toString(value));
        w.endTag();
    }
    
    public static void writeElement(XMLWriter w, String elem, double value) throws IOException
    {
        w.startTag(elem);
        w.write(Double.toString(value));
        w.endTag();
    }
}


