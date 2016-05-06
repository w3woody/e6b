//
//  CMTestCalculation.m
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

#import "CMSpeedCalculation.h"
#import "CMMeasurement.h"
#import "CME6BDataStore.h"

@implementation CMSpeedCalculation

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Calculate Speed";
}

- (NSString *)calculationDescription
{
	return @"Given the distance traveled and the time traveled, determine the current speed.";
}

- (int)inputFieldCount
{
	return 2;
}

- (NSString *)inputFieldName:(int)index
{
	return [@[@"Distance Traveled", @"Elapsed Time"] objectAtIndex:index];
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return (index == 0) ? GDistance : GTime;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Speed";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GSpeed;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GElapsedDistance],
				[[CMValue alloc] initWithValue:GElapsedTime] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GCurrentSpeed.unit] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		default:
		case 0:
			GCurrentSpeed = value;
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
	GElapsedTime = [b storeValue];
	
	double av = [a valueAsUnit:DISTANCE_NMILES withMeasurement:GDistance];
	double bv = [b valueAsUnit:TIME_TIME withMeasurement:GTime];
	double v = av/bv;

	return @[ [[CMValue alloc] initWithValue:v unit:SPEED_KNOTS] ];
}

@end
