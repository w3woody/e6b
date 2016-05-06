//
//  CMCloudBaseCalculator.m
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

#import "CMCloudBaseCalculator.h"
#import "CME6BDataStore.h"

@implementation CMCloudBaseCalculator

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Cloud Base Estimator";
}

- (NSString *)calculationDescription
{
	return @"Given the current reported airport temperature, dew point, and the airport's elevation, calculates the approximate cloud base MSL.";
}

- (int)inputFieldCount
{
	return 3;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"Ground Temperature";
		case 1: return @"Dew Point Temperature";
		case 2: return @"Field Elevation";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return GTemperature;
		case 1: return GTemperature;
		case 2:	return GDistance;
	}
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"Cloud Base Altitude";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return GDistance;
	}
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GGroundTemperature],
				[[CMValue alloc] initWithValue:GDewPointTemperature],
				[[CMValue alloc] initWithValue:GFieldElevation] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GCloudBase] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GCloudBase = value.unit;
			break;
	}
}

- (BOOL)intermixResults
{
	return NO;
}

/************************************************************************/
/*																		*/
/*	Math																*/
/*																		*/
/************************************************************************/


/*	Calculate density altitude, pressure altitude using equations at
 *
 *		http://wahiduddin.net/calc/density_altitude.htm
 *	and	http://en.wikipedia.org/wiki/Density_altitude
 */

- (NSArray *)calculateWithInput:(NSArray *)input
{
	CMValue *a = [input objectAtIndex:0];
	CMValue *b = [input objectAtIndex:1];
	CMValue *c = [input objectAtIndex:2];
	
	GGroundTemperature = [a storeValue];
	GDewPointTemperature = [b storeValue];
	GFieldElevation = [c storeValue];

	double av = [a valueAsUnit:TEMP_CELSIUS withMeasurement:GTemperature];
	double bv = [b valueAsUnit:TEMP_CELSIUS withMeasurement:GTemperature];
	double cv = [c valueAsUnit:DISTANCE_FEET withMeasurement:GDistance];
	
	/*
	 *	Source: http://en.wikipedia.org/wiki/Cloud_base
	 */
	
	double cl = cv + (av - bv) * 400;
	
	
	/*
	 *	Populate results
	 */

	CMValue *outA = [[CMValue alloc] initWithValue:cl unit:DISTANCE_FEET];
	return @[ outA ];
}

@end
