//
//  CMFuelEndurance.m
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

#import "CMFuelEndurance.h"
#import "CME6BDataStore.h"

@implementation CMFuelEndurance

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Fuel Endurance";
}

- (NSString *)calculationDescription
{
	return @"Given the current volume of fuel and the current fuel burn rate, calculate the amount of time left before fuel exhaustion.\n\nNote that this simply calculates the amount of time it will take to consume the volume of fuel specified at the burn rate given. This does not take into account varying fuel requirements for climbing to altitude, run-up time, or factors in the required 30 minute buffer (daytime) or 45 minute buffer (nighttime) safety factor.";
}

- (int)inputFieldCount
{
	return 2;
}

- (NSString *)inputFieldName:(int)index
{
	return [@[@"Fuel Amount", @"Fuel Burn Rate"] objectAtIndex:index];
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return (index == 0) ? GVolume : GVolumeBurn;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Fuel Endurance";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GTime;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GCurrentVolume],
				[[CMValue alloc] initWithValue:GCurrentBurn] ];
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
	
	GCurrentVolume = [a storeValue];
	GCurrentBurn = [b storeValue];
	
	double av = [a valueAsUnit:VOLUME_GALLONS withMeasurement:GVolume];
	double bv = [b valueAsUnit:VOLBURN_GALHR withMeasurement:GVolumeBurn];
	double v = av/bv;
	
	CMValue *c = [[CMValue alloc] initWithValue:v unit:TIME_TIME];
	return @[ c ];
}


@end
