/*  WBFRow.java
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

package com.glenviewsoftware.e6b.calc.wb.aircraft;

import com.glenviewsoftware.e6b.calc.Value;

/**
 * Fuel weight/balance
 *	Comment
 */
public class WBFRow
{
    private String name;
    private Value volume;
    private Value arm;
    private int momentUnit;
    private int fuelType;
    
    public WBFRow(String n, Value vol, Value a, int mu, int ft)
    {
        name = n;
        volume = vol;
        arm = a;
        momentUnit = mu;
        fuelType = ft;
    }

    public WBFRow(WBFRow r)
    {
        name = r.name;
        volume = r.volume;
        arm = r.arm;
        momentUnit = r.momentUnit;
        fuelType = r.fuelType;
    }
    
    public WBFRow()
    {
        name = "Fuel Tank";
        volume = new Value(0,0);
        arm = new Value(0,0);
        momentUnit = 0;
        fuelType = WBFuelTank.FUELTYPE_UNKNOWN;
    }

    public String getName()
    {
        return name;
    }

    public Value getVolume()
    {
        return volume;
    }

    public Value getArm()
    {
        return arm;
    }

    public int getMomentUnit()
    {
        return momentUnit;
    }

    public int getFuelType()
    {
        return fuelType;
    }
}


