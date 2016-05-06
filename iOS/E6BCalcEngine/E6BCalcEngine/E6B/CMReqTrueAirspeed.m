//
//  CMReqTrueAirspeed.m
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

#import "CMReqTrueAirspeed.h"
#import "CME6BDataStore.h"
#import "CMMath.h"

@implementation CMReqTrueAirspeed

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"True Heading/Speed for Course";
}

- (NSString *)calculationDescription
{
	return @"Given the current wind speed and direction, and the desired ground speed and (ground) true course, calculate the required true airspeed and (airplane) true heading direction.";
}

- (int)inputFieldCount
{
	return 4;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"Wind Direction";
		case 1: return @"Wind Speed";
		case 2: return @"(Ground) True Course";
		case 3: return @"Ground Speed";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return nil;
		case 1: return GSpeed;
		case 2:	return nil;
		case 3: return GSpeed;
	}
}

- (int)outputFieldCount
{
	return 2;
}

- (NSString *)outputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"(Airplane) True Heading";
		case 1: return @"True Air Speed";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return nil;
		case 1: return GSpeed;
	}
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GWindDirection],
				[[CMValue alloc] initWithValue:GWindSpeed],
				[[CMValue alloc] initWithValue:GGroundCourse],
				[[CMValue alloc] initWithValue:GGroundSpeed] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:0],
				[[CMValue alloc] initWithUnit:GTrueAirSpeed.unit] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		default:
		case 0:
			GAirplaneCourse = value;
			break;
		case 1:
			GTrueAirSpeed = value;
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
	CMValue *d = [input objectAtIndex:3];
	
	GWindDirection = [a storeValue];
	GWindSpeed = [b storeValue];
	GGroundCourse = [c storeValue];
	GGroundSpeed = [d storeValue];

	double av = ([a value] + 180)*M_PI/180.0;
	double bv = [b valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double cv = [c value]*M_PI/180.0;
	double dv = [d valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	
	double ax = sin(cv) * dv - sin(av) * bv;
	double ay = cos(cv) * dv - cos(av) * bv; /* gv - wv = cv */
	
	double ar = E6BFixAngle(atan2(ax,ay)*180.0/M_PI);
	double dr = sqrt(ax * ax + ay * ay);
	if (dr <= 0) ar = 0;

	a = [[CMValue alloc] initWithValue:ar unit:0];
	b = [[CMValue alloc] initWithValue:dr unit:SPEED_KNOTS];
	return @[ a, b ];
}


@end
