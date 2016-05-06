/*  Units.java
 *
 *  Created on Dec 24, 2012 by William Edward Woody
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

public class Units
{
    public static final Measurement distance = new Distance();
    public static final Measurement length = new Length();
    public static final Measurement moment = new Moment();
    public static final Measurement pressure = new Pressure();
    public static final Measurement speed = new Speed();
    public static final Measurement temperature = new Temperature();
    public static final Measurement time = new Time();
    public static final Measurement volume = new Volume();
    public static final Measurement volumeBurn = new VolumeBurn();
    public static final Measurement weight = new Weight();
}


