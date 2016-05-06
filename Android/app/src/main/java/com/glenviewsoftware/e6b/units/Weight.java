/*  Weight.java
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

public class Weight implements Measurement
{
    public static final int WEIGHT_LBS          = 0;
    public static final int WEIGHT_KILOGRAMS    = 1;
    public static final int WEIGHT_STONE        = 2;

    @Override
    public double toStandardUnit(double value, int index)
    {
        switch (index) {
            case WEIGHT_LBS:    /* pounds to kilos */
                return value / 2.20462234;
            default:
            case WEIGHT_KILOGRAMS:
                return value;
            case WEIGHT_STONE:
                return value * (14 * 0.45359237);
        }
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        switch (index) {
            case WEIGHT_LBS:    /* kilos to pounds */
                return value * 2.20462234;
            default:
            case WEIGHT_KILOGRAMS:
                return value;
            case WEIGHT_STONE:
                return value / (14 * 0.45359237);
        }
    }

    @Override
    public int standardUnit()
    {
        return WEIGHT_KILOGRAMS;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        switch (unit) {
            case WEIGHT_LBS:    /* kilos to pounds */
                return "lbs";
            default:
            case WEIGHT_KILOGRAMS:
                return "kg";
            case WEIGHT_STONE:
                return "st";
        }
    }

    @Override
    public String getMeasurement(int unit)
    {
        switch (unit) {
            case WEIGHT_LBS:    /* kilos to pounds */
                return "Pounds";
            default:
            case WEIGHT_KILOGRAMS:
                return "Kilograms";
            case WEIGHT_STONE:
                return "Stone";
        }
    }

    @Override
    public int numUnits()
    {
        return 3;
    }
}


