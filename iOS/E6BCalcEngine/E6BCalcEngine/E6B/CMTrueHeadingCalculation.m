//
//  CMTrueHeadingCalculation.m
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

#import "CMTrueHeadingCalculation.h"
#import "CME6BDataStore.h"
#import "CMMath.h"

@implementation CMTrueHeadingCalculation

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"True Course/Heading";
}

- (NSString *)calculationDescription
{
	return @"Convert Magnetic Course/Heading to True Course/Heading.";
}

- (int)inputFieldCount
{
	return 2;
}

- (NSString *)inputFieldName:(int)index
{
	return [@[@"Magnetic Course/Heading", @"Magnetic Variation (+W/-E)"] objectAtIndex:index];
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return nil;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"True Course/Heading";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return nil;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GAirplaneCourse],
				[[CMValue alloc] initWithValue:GMagVariation] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:0] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
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
	
	GMagVariation = [b storeValue];
	
	double tc = [a value];
	double mv = [b value];
	double th = E6BFixAngle(tc - mv);

	return @[ [[CMValue alloc] initWithValue:th unit:0] ];
}

@end
