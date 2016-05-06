/*  VolumeBurn.java
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

public class VolumeBurn implements Measurement
{
    public static final int VOLBURN_GALHR           = 0;
    public static final int VOLBURN_LITERSHR        = 1;
    public static final int VOLBURN_IMPGALLONSHR    = 2;


    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case VOLBURN_GALHR: /* Fahrenheit to Celsius */
                return value * 3.785411784;
            default:
            case VOLBURN_LITERSHR:
                return value;
            case VOLBURN_IMPGALLONSHR:
                return value * 4.54609;
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case VOLBURN_GALHR: /* Fahrenheit to Celsius */
                return value / 3.785411784;
            default:
            case VOLBURN_LITERSHR:
                return value;
            case VOLBURN_IMPGALLONSHR:
                return value / 4.54609;
        }
    }

    @Override
    public int standardUnit()
    {
        return VOLBURN_LITERSHR;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case VOLBURN_GALHR: /* Fahrenheit to Celsius */
                return "gph";
            default:
            case VOLBURN_LITERSHR:
                return "lph";
            case VOLBURN_IMPGALLONSHR:
                return "igph";
        }
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case VOLBURN_GALHR: /* Fahrenheit to Celsius */
                return "Gallons/Hour";
            default:
            case VOLBURN_LITERSHR:
                return "Liters/Hour";
            case VOLBURN_IMPGALLONSHR:
                return "Imp Gallons/Hour";
        }
    }

    @Override
    public int numUnits()
    {
        return 3;
    }
}


