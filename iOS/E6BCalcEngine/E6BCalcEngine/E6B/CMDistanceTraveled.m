//
//  CMDistanceTraveled.m
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

#import "CMDistanceTraveled.h"
#import "CME6BDataStore.h"

@implementation CMDistanceTraveled

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Distance Traveled";
}

- (NSString *)calculationDescription
{
	return @"Given elapsed time and current speed, calculate distance traveled.";
}

- (int)inputFieldCount
{
	return 2;
}

- (NSString *)inputFieldName:(int)index
{
	return [@[@"Current Speed", @"Elapsed Time"] objectAtIndex:index];
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return (index == 0) ? GSpeed : GTime;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Distance Traveled";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GDistance;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GCurrentSpeed],
				[[CMValue alloc] initWithValue:GElapsedTime] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GElapsedDistance.unit] ];
}

- (BOOL)intermixResults
{
	return NO;
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GElapsedDistance = value;
			break;
	}
}


- (NSArray *)calculateWithInput:(NSArray *)input
{
	CMValue *a = [input objectAtIndex:0];
	CMValue *b = [input objectAtIndex:1];
	
	GCurrentSpeed = [a storeValue];
	GElapsedTime = [b storeValue];
	
	double av = [a valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double bv = [b valueAsUnit:TIME_TIME withMeasurement:GTime];
	double v = av*bv;
	
	CMValue *outA = [[CMValue alloc] initWithValue:v unit:DISTANCE_NMILES];
	return @[ outA ];
}

@end
