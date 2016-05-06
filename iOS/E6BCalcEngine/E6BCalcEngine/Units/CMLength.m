//
//  CMLength.m
//  E6B
//
//  Created by William Woody on 9/22/12.
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

#import "CMLength.h"

@implementation CMLength

- (NSArray *)measurements
{
	return @[ @"Inches", @"Centimeters" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"in", @"cm" ];
}

- (int)standardUnit
{
	return LENGTH_CENTIMETERS;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case LENGTH_INCHES:	/* in to cm */
			return value * 2.54;
		default:
		case LENGTH_CENTIMETERS:
			return value;
	}
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case LENGTH_INCHES:	/* cm to in */
			return value / 2.54;
		default:
		case LENGTH_CENTIMETERS:
			return value;
	}
}

@end
