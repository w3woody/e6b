//
//  CMValue.h
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

#import <Foundation/Foundation.h>

@protocol CMMeasurement;

/*	Value
 *
 *		Save/store value. The type of unit is implicit
 */

typedef struct Value
{
	uint32_t unit;
	double value;
} Value;

extern Value CMMakeValue(double value, uint32_t unit);


@interface CMValue : NSObject

@property (assign) uint32_t unit;
@property (assign) double value;

- (id)initWithValue:(Value)value;
- (id)initWithValue:(double)value unit:(uint32_t)unit;
- (id)initWithUnit:(uint32_t)unit;

- (double)standardValueWithMeasurement:(id<CMMeasurement>)measurement;
- (Value)storeValue;
- (double)valueAsUnit:(uint32_t)unit withMeasurement:(id<CMMeasurement>)measurement;

@end
