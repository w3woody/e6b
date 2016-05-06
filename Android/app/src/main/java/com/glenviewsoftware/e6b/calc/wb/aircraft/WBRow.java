/*  WBRow.java
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
 * Weight/balance point
 */
public class WBRow
{
    private Value weight;
    private Value arm;
    private int momentUnit;
    
    public WBRow(Value w, Value a, int mu)
    {
        weight = w;
        arm = a;
        momentUnit = mu;
    }

    public WBRow(WBRow r)
    {
        weight = r.weight;
        arm = r.arm;
        momentUnit = r.momentUnit;
    }
    

    public WBRow()
    {
        weight = new Value(0,0);
        arm = new Value(0,0);
        momentUnit = 0;
    }

    public Value getWeight()
    {
        return weight;
    }

    public Value getArm()
    {
        return arm;
    }

    public int getMomentUnit()
    {
        return momentUnit;
    }
}


