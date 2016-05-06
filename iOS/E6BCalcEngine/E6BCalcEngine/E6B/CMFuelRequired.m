//
//  CMFuelRequired.m
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

#import "CMFuelRequired.h"
#import "CME6BDataStore.h"

@implementation CMFuelRequired

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Fuel Required";
}

- (NSString *)calculationDescription
{
	return @"Given the time traveled and the fuel consumption rate, calculates fuel required.\n\nNote that this simply calculates fuel burn rate times consumption rate and does not take into account run-up fuel consumption, consumption while climbing to altitude, or the required 30 minute buffer (daytime) or 45 minute buffer (nighttime) safety factor.";
}

- (int)inputFieldCount
{
	return 2;
}

- (NSString *)inputFieldName:(int)index
{
	return [@[@"Fuel Burn Rate", @"Elapsed Time"] objectAtIndex:index];
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return (index == 0) ? GVolumeBurn : GTime;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Required Fuel";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GVolume;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GCurrentBurn],
				[[CMValue alloc] initWithValue:GElapsedTime] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GCurrentVolume.unit] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GCurrentVolume = value;
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
	
	GCurrentBurn = [a storeValue];
	GElapsedTime = [b storeValue];
	
	double av = [a valueAsUnit:VOLBURN_GALHR withMeasurement:GVolumeBurn];
	double bv = [b valueAsUnit:TIME_TIME withMeasurement:GVolume];
	double v = av*bv;
	
	CMValue *c = [[CMValue alloc] initWithValue:v unit:VOLUME_GALLONS];
	return @[ c ];
}

@end
