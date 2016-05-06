//
//  CMManeuveringSpeed.m
//  E6B
//
//  Created by William Woody on 10/24/12.
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

#import "CMManeuveringSpeed.h"
#import "CME6BDataStore.h"

@implementation CMManeuveringSpeed

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Va for Weight";
}

- (NSString *)calculationDescription
{
	return @"Given the airplane's max gross rated weight and the Va (maneuvering speed) for that weight, and given the airplane's current weight, calculate Va for the current weight.";
}

- (int)inputFieldCount
{
	return 3;
}

- (NSString *)inputFieldName:(int)index
{
	return [@[@"Rated Va at Max Weight", @"Max Weight", @"Current Weight"] objectAtIndex:index];
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		case 0:	return GSpeed;
		default: return GWeight;
	}
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Current Va";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GSpeed;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GManuveurWeight],
				[[CMValue alloc] initWithValue:GMaxWeight],
				[[CMValue alloc] initWithValue:GCurrentWeight] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GCurManuveurSpeed] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GCurManuveurSpeed = value.unit;
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
	CMValue *c = [input objectAtIndex:2];
	
	GManuveurWeight = [a storeValue];
	GMaxWeight = [b storeValue];
	GCurrentWeight = [c storeValue];
	
	double va = [a valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double mw = [b valueAsUnit:WEIGHT_LBS withMeasurement:GWeight];
	double cw = [c valueAsUnit:WEIGHT_LBS withMeasurement:GWeight];
	
	double cva = va * sqrt(cw/mw);

	return @[ [[CMValue alloc] initWithValue:cva unit:SPEED_KNOTS] ];
}


@end
