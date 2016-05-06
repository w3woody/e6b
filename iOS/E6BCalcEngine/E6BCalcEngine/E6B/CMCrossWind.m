//
//  CMCrossWind.m
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

#import "CMCrossWind.h"
#import "CME6BDataStore.h"

@implementation CMCrossWind

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Cross Wind Calculator";
}

- (NSString *)calculationDescription
{
	return @"Given a runway number, wind direction and speed, calculate the cross wind and headwind components.";
}

- (int)inputFieldCount
{
	return 3;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"Runway Number (1-36)";
		case 1: return @"Wind Direction";
		case 2: return @"Wind Speed";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:
		case 1: return nil;
		case 2: return GSpeed;
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
		case 0:	return @"- Left/+ Right Crosswind";
		case 1: return @"- Headwind/+ Tailwind";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0: return GSpeed;
		case 1: return GSpeed;
	}
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GRunwayNumber],
				[[CMValue alloc] initWithValue:GWindDirection],
				[[CMValue alloc] initWithValue:GWindSpeed] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GCrosswind],
				[[CMValue alloc] initWithUnit:GHeadwind]	];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GCrosswind = value.unit;
			break;
		case 1:
			GHeadwind = value.unit;
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
	
	GRunwayNumber = [a storeValue];
	GWindDirection = [b storeValue];
	GWindSpeed = [c storeValue];

	double av = [a value] * 10 + 180;
	double bv = [b value];
	double cv = [c valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];

	double r = (av-bv);
	r *= M_PI / 180;
	double x = cos(r) * cv;
	double y = sin(r) * cv;

	CMValue *outA = [[CMValue alloc] initWithValue:y unit:SPEED_KNOTS];
	CMValue *outB = [[CMValue alloc] initWithValue:x unit:SPEED_KNOTS];

	return @[ outA, outB ];
}


@end
