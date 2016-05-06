/*  Time.java
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

public class Time implements Measurement
{
    public static final int TIME_TIME = 0;

    @Override
    public double toStandardUnit(double value, int index)
    {
        return value;
    }

    @Override
    public double fromStandardUnit(double value, int index)
    {
        return value;
    }

    @Override
    public int standardUnit()
    {
        return TIME_TIME;
    }

    @Override
    public String getAbbrMeasure(int unit)
    {
        return "time";
    }

    @Override
    public String getMeasurement(int unit)
    {
        return "Time (H:M:S)";
    }
    
    @Override
    public int numUnits()
    {
        return 1;
    }
}


