/*  Pressure.java
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

public class Pressure implements Measurement
{
    public static final int PRESSURE_INHG       = 0;
    public static final int PRESSURE_KPA        = 1;
    public static final int PRESSURE_MILLIBARS  = 2;

    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case PRESSURE_INHG: /* Feet to meters */
                return value * 3.386389;
            default:
            case PRESSURE_KPA:
                return value;
            case PRESSURE_MILLIBARS:
                return value / 10;
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case PRESSURE_INHG: /* Feet to meters */
                return value / 3.386389;
            default:
            case PRESSURE_KPA:
                return value;
            case PRESSURE_MILLIBARS:
                return value * 10;
        }
    }

    @Override
    public int standardUnit()
    {
        return PRESSURE_KPA;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case PRESSURE_INHG: /* Feet to meters */
                return "inHg";
            default:
            case PRESSURE_KPA:
                return "kPa";
            case PRESSURE_MILLIBARS:
                return "mbar";
        }
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case PRESSURE_INHG: /* Feet to meters */
                return "Inches of Mercury";
            default:
            case PRESSURE_KPA:
                return "Kilopascals";
            case PRESSURE_MILLIBARS:
                return "Millibars";
        }
    }

    @Override
    public int numUnits()
    {
        return 3;
    }
}


