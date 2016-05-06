//
//  CMFindHeadingCalculation.h
//  E6B
//
//  Created by William Woody on 9/16/12.
//  Copyright (c) 2012 William Woody. All rights reserved.
//

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

#import <Foundation/Foundation.h>
#import "CME6BCalculation.h"

@interface CMFindHeadingCalculation : NSObject <CME6BCalculation>

@end


struct FHResult
{
	double th;		/* True heading (degrees) */
	double gs;		/* Ground Speed */
};

/*	FindHeading
 *
 *		Find the heading given tc (true course), tas (true air speed), wd (wind
 *	direction), and ws (wind speed), with angles in degrees and speed in knots.
 *	Returns the true heading and ground speed
 */

extern struct FHResult FindHeading(double tc, double tas, double wd, double ws);