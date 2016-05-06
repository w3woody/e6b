/*  Moment.java
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

public class Moment implements Measurement
{
    public static final int MOMENT_POUNDINCH    = 0;
    public static final int MOMENT_KGCM         = 1;

    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case MOMENT_POUNDINCH:  /* Fahrenheit to Celsius */
                return value * 2.54 / 2.20462234;
            default:
            case MOMENT_KGCM:
                return value;
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case MOMENT_POUNDINCH:  /* Fahrenheit to Celsius */
                return value / 2.54 * 2.20462234;
            default:
            case MOMENT_KGCM:
                return value;
        }
    }

    @Override
    public int standardUnit()
    {
        return MOMENT_KGCM;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case MOMENT_POUNDINCH:  /* Fahrenheit to Celsius */
                return "ft-in";
            default:
            case MOMENT_KGCM:
                return "kgcm";
        }
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case MOMENT_POUNDINCH:  /* Fahrenheit to Celsius */
                return "Foot-Inches";
            default:
            case MOMENT_KGCM:
                return "Kilogram-Centimeters";
        }
    }

    @Override
    public int numUnits()
    {
        return 2;
    }
}


