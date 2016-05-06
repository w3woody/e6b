//
//  CMValue.m
//  E6BCalcEngine
//
//  Created by William Woody on 10/21/14.
//  Copyright (c) 2014 William Woody. All rights reserved.
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

#import "CMValue.h"
#import "CMMeasurement.h"

@implementation CMValue

/************************************************************************/
/*																		*/
/*	Construction														*/
/*																		*/
/************************************************************************/

- (id)initWithValue:(double)value unit:(uint32_t)unit
{
	if (nil != (self = [super init])) {
		self.value = value;
		self.unit = unit;
	}
	return self;
}

- (id)initWithValue:(Value)value
{
	if (nil != (self = [super init])) {
		self.value = value.value;
		self.unit = value.unit;
	}
	return self;
}

- (id)initWithUnit:(uint32_t)unit
{
	if (nil != (self = [super init])) {
		self.value = 0;
		self.unit = unit;
	}
	return self;
}

- (double)standardValueWithMeasurement:(id<CMMeasurement>)measurement
{
	return [measurement toStandardUnit:self.value withUnit:self.unit];
}

- (Value)storeValue
{
	Value v;
	v.unit = self.unit;
	v.value = self.value;
	return v;
}

- (double)valueAsUnit:(uint32_t)unit withMeasurement:(id<CMMeasurement>)measurement
{
	double std = [self standardValueWithMeasurement:measurement];
	return [measurement fromStandardUnit:std withUnit:unit];
}

/************************************************************************/
/*																		*/
/*	Support																*/
/*																		*/
/************************************************************************/


Value CMMakeValue(double value, uint32_t unit)
{
	Value ret;

	ret.value = value;
	ret.unit = unit;

	return ret;
}

@end
