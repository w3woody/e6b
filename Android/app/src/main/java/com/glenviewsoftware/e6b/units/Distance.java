/*  Distance.java
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

public class Distance implements Measurement
{
    public static final int DISTANCE_FEET       = 0;
    public static final int DISTANCE_STMILES    = 1;
    public static final int DISTANCE_NMILES     = 2;
    public static final int DISTANCE_METERS     = 3;
    public static final int DISTANCE_KILOMETERS = 4;

    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case DISTANCE_FEET: /* Feet to meters */
                return value * 0.3048;
            case DISTANCE_STMILES:
                return value * 5280 * 0.3048;
            case DISTANCE_NMILES:
                return value * 1852;
            case DISTANCE_METERS:
            default:
                return value;
            case DISTANCE_KILOMETERS:
                return value * 1000;
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case DISTANCE_FEET: /* Feet to meters */
                return value / 0.3048;
            case DISTANCE_STMILES:
                return value / (5280 * 0.3048);
            case DISTANCE_NMILES:
                return value / 1852;
            case DISTANCE_METERS:
            default:
                return value;
            case DISTANCE_KILOMETERS:
                return value / 1000;
        }
    }

    @Override
    public int standardUnit()
    {
        return DISTANCE_METERS;
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case DISTANCE_FEET:
                return "Feet";
            case DISTANCE_STMILES:
                return "Statute Miles";
            case DISTANCE_NMILES:
                return "Nautical Miles";
            case DISTANCE_METERS:
            default:
                return "Meters";
            case DISTANCE_KILOMETERS:
                return "Kilometers";
        }
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case DISTANCE_FEET:
                return "ft";
            case DISTANCE_STMILES:
                return "mi";
            case DISTANCE_NMILES:
                return "nm";
            case DISTANCE_METERS:
            default:
                return "m";
            case DISTANCE_KILOMETERS:
                return "km";
        }
    }

    @Override
    public int numUnits()
    {
        return 5;
    }
}


