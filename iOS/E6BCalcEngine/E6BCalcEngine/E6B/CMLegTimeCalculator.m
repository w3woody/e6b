//
//  CMLegTimeCalculator.m
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

#import "CMLegTimeCalculator.h"
#import "CME6BDataStore.h"

@implementation CMLegTimeCalculator

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Leg Time Calculator";
}

- (NSString *)calculationDescription
{
	return @"Given distance to travel and current speed, calculate necessary time.";
}

- (int)inputFieldCount
{
	return 2;
}

- (NSString *)inputFieldName:(int)index
{
	return [@[@"Distance To Travel", @"Current Speed"] objectAtIndex:index];
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return (index == 0) ? GDistance : GSpeed;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Elapsed Time";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GTime;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GElapsedDistance],
				[[CMValue alloc] initWithValue:GCurrentSpeed] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GElapsedTime.unit] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GElapsedTime = value;
			break;
	}
}

- (BOOL)intermixResults
{
	return NO;
}

- (NSArray *)calculateWithInput:(NSArray *)input
{
	CMValue *a = [input objectAtIndex:0];
	CMValue *b = [input objectAtIndex:1];
	
	GElapsedDistance = [a storeValue];
	GCurrentSpeed = [b storeValue];
	
	double av = [a valueAsUnit:DISTANCE_NMILES withMeasurement:GDistance];
	double bv = [b valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double v = av/bv;

	CMValue *c = [[CMValue alloc] initWithValue:v unit:TIME_TIME];
	return @[ c ];
}


@end
