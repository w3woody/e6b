/*  Calculation.java
 *
 *  Created on Dec 23, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.calc;

/**
 * A calculation is the object which handles calculating a formula or set of formulas.
 * A calculation is associated with a calculation manager, which is responsible for
 * laying out the calculation.
 * 
 * The calculation is essentially the controller in a model/view/controller object,
 * the calculation manager is the view (or rather, view management code).
 */
public interface Calculation
{
    /**
     * Returns the calculation name
     * @return
     */
    String getCalculationName();
    
    /**
     * Get the calculation manager associated with this
     * @return
     */
    CalcManager getCalculationManager();
}


