//
//  CMWindCalculator.m
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

#import "CMWindCalculator.h"
#import "CME6BDataStore.h"
#import "CMMath.h"

@implementation CMWindCalculator

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Wind Speed and Direction";
}


- (NSString *)calculationDescription
{
	return @"Given the current ground speed and (ground) true course, the current (airplane) true heading and direction, calculate the wind speed and direction.";
}

- (int)inputFieldCount
{
	return 4;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"(Ground) True Course";
		case 1: return @"Ground Speed";
		case 2: return @"(Airplane) True Heading";
		case 3: return @"True Air Speed";
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
		case 0:	return @"Wind Heading";
		case 1: return @"Wind Speed";
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
	return @[	[[CMValue alloc] initWithValue:GGroundCourse],
				[[CMValue alloc] initWithValue:GGroundSpeed],
				[[CMValue alloc] initWithValue:GAirplaneCourse],
				[[CMValue alloc] initWithValue:GTrueAirSpeed] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:0],
				[[CMValue alloc] initWithUnit:GWindSpeed.unit] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
			GWindDirection = value;
			break;
		case 1:
			GWindSpeed = value;
			break;
		default:
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
	
	GGroundCourse = [a storeValue];
	GGroundSpeed = [b storeValue];
	GAirplaneCourse = [c storeValue];
	GTrueAirSpeed = [d storeValue];

	double av = ([a value])*M_PI/180.0;
	double bv = [b valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double cv = [c value]*M_PI/180.0;
	double dv = [d valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	
	double ax = sin(av) * bv - sin(cv) * dv;
	double ay = cos(av) * bv - cos(cv) * dv;
	
	double ar = E6BFixAngle(atan2(ax,ay)*180.0/M_PI + 180);	/* Wind is measured *from* */
	double dr = sqrt(ax * ax + ay * ay);
	if (dr <= 0) ar = 0;

	a = [[CMValue alloc] initWithValue:ar unit:0];
	b = [[CMValue alloc] initWithValue:dr unit:SPEED_KNOTS];
	return @[ a, b ];
}

@end
