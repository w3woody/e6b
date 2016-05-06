/*  Speed.java
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

public class Speed implements Measurement
{
    public static final int SPEED_KNOTS =        0;
    public static final int SPEED_MPH   =        1;
    public static final int SPEED_KPH   =        2;      /* Kilometers/hour */
    public static final int SPEED_FPS   =        3;
    public static final int SPEED_MS    =        4;

    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case SPEED_KNOTS:   /* Feet to meters */
                return value * (1852.0 / 3600.0);
            case SPEED_MPH:
                return value * (5280.0 * 0.3048 / 3600.0);
            case SPEED_KPH:
                return value * (1000.0 / 3600.0);
            case SPEED_FPS:
                return value * 0.3048;
            case SPEED_MS:
            default:
                return value;
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case SPEED_KNOTS:   /* Feet to meters */
                return value / (1852.0 / 3600.0);
            case SPEED_MPH:
                return value / (5280.0 * 0.3048 / 3600.0);
            case SPEED_KPH:
                return value / (1000.0 / 3600.0);
            case SPEED_FPS:
                return value / 0.3048;
            case SPEED_MS:
            default:
                return value;
        }
    }

    @Override
    public int standardUnit()
    {
        return SPEED_MS;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case SPEED_KNOTS:   /* Feet to meters */
                return "kt";
            case SPEED_MPH:
                return "mph";
            case SPEED_KPH:
                return "km/h";
            case SPEED_FPS:
                return "f/s";
            case SPEED_MS:
            default:
                return "m/s";
        }
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case SPEED_KNOTS:   /* Feet to meters */
                return "Knots";
            case SPEED_MPH:
                return "Miles/Hour";
            case SPEED_KPH:
                return "Kilometers/Hour";
            case SPEED_FPS:
                return "Feet/Second";
            case SPEED_MS:
            default:
                return "Meters/Second";
        }
    }

    @Override
    public int numUnits()
    {
        return 5;
    }
}


