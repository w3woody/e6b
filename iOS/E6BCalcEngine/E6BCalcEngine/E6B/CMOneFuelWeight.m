//
//  CMOneFuelWeight.m
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

#import "CMOneFuelWeight.h"
#import "CME6BDataStore.h"

@implementation CMOneFuelWeight

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (id)initWithName:(NSString *)name desc:(NSString *)desc factor:(double)cv
{
	if (nil != (self = [super init])) {
		cvName = [name copy];
		cvDescription = [desc copy];
		cvConvert = cv;
	}
	return self;
}

- (NSString *)calculationName
{
	return cvName;
}

- (NSString *)calculationDescription
{
	return cvDescription;
}

- (int)inputFieldCount
{
	return 1;
}

- (NSString *)inputFieldName:(int)index
{
	return @"Volume";
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return GVolume;
}

- (int)outputFieldCount
{
	return 1;
}

- (NSString *)outputFieldName:(int)index
{
	return @"Weight";
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GWeight;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:0 unit:VOLUME_GALLONS] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:WEIGHT_LBS] ];
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
	CMValue *iv = input[0];

	double val = [iv valueAsUnit:VOLUME_GALLONS withMeasurement:GVolume];
	val *= cvConvert;

	return @[ [[CMValue alloc] initWithValue:val unit:WEIGHT_LBS] ];
}


@end
