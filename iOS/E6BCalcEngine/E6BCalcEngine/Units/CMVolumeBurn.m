//
//  CMVolumeBurn.m
//  E6B
//
//  Created by William Woody on 9/17/12.
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

#import "CMVolumeBurn.h"

@implementation CMVolumeBurn

- (NSArray *)measurements
{
	return @[ @"Gallons/Hour", @"Liters/Hour", @"Imp Gallons/Hour" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"gph", @"lph", @"igph" ];
}

- (int)standardUnit
{
	return VOLBURN_LITERSHR;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case VOLBURN_GALHR:	/* Fahrenheit to Celsius */
			return value * 3.785411784;
		default:
		case VOLBURN_LITERSHR:
			return value;
		case VOLBURN_IMPGALLONSHR:
			return value * 4.54609;
	}
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case VOLBURN_GALHR:	/* Fahrenheit to Celsius */
			return value / 3.785411784;
		default:
		case VOLBURN_LITERSHR:
			return value;
		case VOLBURN_IMPGALLONSHR:
			return value / 4.54609;
	}
}

@end
