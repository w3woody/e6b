/*  Temperature.java
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

public class Temperature implements Measurement
{
    public static final int TEMP_FAHRENHEIT     = 0;
    public static final int TEMP_CELSIUS        = 1;
    public static final int TEMP_KELVIN         = 2;

    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case TEMP_FAHRENHEIT:   /* Fahrenheit to Celsius */
                return (value - 32) * 5.0 / 9.0;
            default:
            case TEMP_CELSIUS:
                return value;
            case TEMP_KELVIN:
                return value - 273.15;
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case TEMP_FAHRENHEIT:   /* Celsius to Fahrenheit */
                return (value * 9.0 / 5.0) + 32;
            default:
            case TEMP_CELSIUS:
                return value;
            case TEMP_KELVIN:
                return value + 273.15;
        }
    }

    @Override
    public int standardUnit()
    {
        return TEMP_CELSIUS;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case TEMP_FAHRENHEIT:   /* Celsius to Fahrenheit */
                return "F";
            default:
            case TEMP_CELSIUS:
                return "C";
            case TEMP_KELVIN:
                return "K";
        }
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case TEMP_FAHRENHEIT:   /* Celsius to Fahrenheit */
                return "Fahrenheit";
            default:
            case TEMP_CELSIUS:
                return "Celsius";
            case TEMP_KELVIN:
                return "Kelvin";
        }
    }

    @Override
    public int numUnits()
    {
        return 3;
    }
}


