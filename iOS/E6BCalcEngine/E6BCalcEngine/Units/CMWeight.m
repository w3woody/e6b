//
//  CMWeight.m
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

#import "CMWeight.h"

@implementation CMWeight

- (NSArray *)measurements
{
	return @[ @"Pounds", @"Kilograms", @"Stone" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"lbs", @"kg", @"st" ];
}

- (int)standardUnit
{
	return WEIGHT_KILOGRAMS;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case WEIGHT_LBS:	/* pounds to kilos */
			return value / 2.20462234;
		default:
		case WEIGHT_KILOGRAMS:
			return value;
		case WEIGHT_STONE:
			return value * (14 * 0.45359237);
	}
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case WEIGHT_LBS:	/* kilos to pounds */
			return value * 2.20462234;
		default:
		case WEIGHT_KILOGRAMS:
			return value;
		case WEIGHT_STONE:
			return value / (14 * 0.45359237);
	}
}


@end
