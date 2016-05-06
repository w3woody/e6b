/*  Volume.java
 *
 *  Created on Dec 22, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.units;

public class Volume implements Measurement
{
    public static final int VOLUME_GALLONS      = 0;
    public static final int VOLUME_LITERS       = 1;
    public static final int VOLUME_IMPGALLONS   = 2;
    public static final int VOLUME_QUARTS       = 3;

    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case VOLUME_GALLONS:    /* Fahrenheit to Celsius */
                return value * 3.785411784;
            default:
            case VOLUME_LITERS:
                return value;
            case VOLUME_IMPGALLONS:
                return value * 4.54609;
            case VOLUME_QUARTS: /* Fahrenheit to Celsius */
                return value * 3.785411784 / 4.0;
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case VOLUME_GALLONS:    /* Fahrenheit to Celsius */
                return value / 3.785411784;
            default:
            case VOLUME_LITERS:
                return value;
            case VOLUME_IMPGALLONS:
                return value / 4.54609;
            case VOLUME_QUARTS: /* Quarts */
                return value / 3.785411784 * 4.0;
        }
    }

    @Override
    public int standardUnit()
    {
        return VOLUME_LITERS;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case VOLUME_GALLONS:   /* Feet to meters */
                return "us g";
            case VOLUME_LITERS:
            default:
                return "l";
            case VOLUME_IMPGALLONS:
                return "i g";
            case VOLUME_QUARTS:
                return "qt";
        }
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case VOLUME_GALLONS:   /* Feet to meters */
                return "Gallons";
            case VOLUME_LITERS:
            default:
                return "Liters";
            case VOLUME_IMPGALLONS:
                return "Imperial Gallons";
            case VOLUME_QUARTS:
                return "Quarts";
        }
    }

    @Override
    public int numUnits()
    {
        return 4;
    }
}


