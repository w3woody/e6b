//
//  CMOneUnitCalculator.m
//  E6B
//
//  Created by William Woody on 9/27/12.
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

#import "CMOneUnitCalculator.h"
#import "CME6BDataStore.h"

@implementation CMOneUnitCalculator

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (id)initWithName:(NSString *)name desc:(NSString *)desc unit:(id<CMMeasurement>)unit
{
	if (nil != (self = [super init])) {
		unitName = [name copy];
		unitDescription = [desc copy];
		measurement = unit;
	}
	return self;
}

- (NSString *)calculationName
{
	return unitName;
}

- (NSString *)calculationDescription
{
	return unitDescription;
}

- (int)inputFieldCount
{
	return 1;
}

- (NSString *)inputFieldName:(int)index
{
	return @"Input";
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return measurement;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Output";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return measurement;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:0 unit:[measurement standardUnit]] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:[measurement standardUnit]] ];
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
	return input;	/* Relying on infrastructure above me to actually convert */
}

@end
