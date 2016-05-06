//
//  CMPressure.m
//  E6B
//
//  Created by William Woody on 9/15/12.
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

#import "CMPressure.h"

@implementation CMPressure

- (NSArray *)measurements
{
	return @[ @"Inches of Mercury", @"Kilopascals", @"Millibars" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"inHg", @"kPa", @"mbar" ];
}

- (int)standardUnit
{
	return PRESSURE_KPA;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case PRESSURE_INHG:	/* Feet to meters */
			return value * 3.386389;
		default:
		case PRESSURE_KPA:
			return value;
		case PRESSURE_MILLIBARS:
			return value / 10;
	}
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case PRESSURE_INHG:	/* Feet to meters */
			return value / 3.386389;
		default:
		case PRESSURE_KPA:
			return value;
		case PRESSURE_MILLIBARS:
			return value * 10;
	}
}

@end
