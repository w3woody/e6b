//
//  CMSpeed.m
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

#import "CMSpeed.h"

@implementation CMSpeed

- (NSArray *)measurements
{
	return @[ @"Knots", @"Miles/Hour", @"Kilometers/Hour", @"Feet/Second", @"Meters/Second" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"kt", @"mph", @"km/h", @"f/s", @"m/s" ];
}

- (int)standardUnit
{
	return SPEED_MS;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case SPEED_KNOTS:	/* Feet to meters */
			return value * (1852.0 / 3600.0);
		case SPEED_MPH:
			return value * (5280.0 * 0.3048 / 3600.0);
		case SPEED_KPH:
			return value * (1000.0 / 3600.0);
		case SPEED_FPS:
			return value * 0.3048;
		case SPEED_MS:
		default:
			return value;
	}
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case SPEED_KNOTS:	/* Feet to meters */
			return value / (1852.0 / 3600.0);
		case SPEED_MPH:
			return value / (5280.0 * 0.3048 / 3600.0);
		case SPEED_KPH:
			return value / (1000.0 / 3600.0);
		case SPEED_FPS:
			return value / 0.3048;
		case SPEED_MS:
		default:
			return value;
	}
}

@end
