//
//  CMMoment.m
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

#import "CMMoment.h"

@implementation CMMoment

- (NSArray *)measurements
{
	return @[ @"Foot-Inches", @"Kilogram-Centimeters" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"ft-in", @"kgcm" ];
}

- (int)standardUnit
{
	return MOMENT_KGCM;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case MOMENT_POUNDINCH:	/* Fahrenheit to Celsius */
			return value * 2.54 / 2.20462234;
		default:
		case MOMENT_KGCM:
			return value;
	}
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	switch (index) {
		case MOMENT_POUNDINCH:	/* Fahrenheit to Celsius */
			return value / 2.54 * 2.20462234;
		default:
		case MOMENT_KGCM:
			return value;
	}
}

@end
