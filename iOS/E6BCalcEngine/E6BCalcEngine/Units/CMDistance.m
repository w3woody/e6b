//
//  CMDistance.m
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

#import "CMDistance.h"

@implementation CMDistance

- (NSArray *)measurements
{
	return @[ @"Feet", @"Statute Miles", @"Nautical Miles", @"Meters", @"Kilometers" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"ft", @"mi", @"nm", @"m", @"km" ];
}

- (int)standardUnit
{
	return DISTANCE_METERS;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case DISTANCE_FEET:	/* Feet to meters */
			return value * 0.3048;
		case DISTANCE_STMILES:
			return value * 5280 * 0.3048;
		case DISTANCE_NMILES:
			return value * 1852;
		case DISTANCE_METERS:
		default:
			return value;
		case DISTANCE_KILOMETERS:
			return value * 1000;
	}
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case DISTANCE_FEET:	/* Feet to meters */
			return value / 0.3048;
		case DISTANCE_STMILES:
			return value / (5280 * 0.3048);
		case DISTANCE_NMILES:
			return value / 1852;
		case DISTANCE_METERS:
		default:
			return value;
		case DISTANCE_KILOMETERS:
			return value / 1000;
	}
}

@end
