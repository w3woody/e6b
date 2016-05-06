//
//  CME6BCalculation.h
//  E6BCalcEngine
//
//  Created by William Woody on 10/20/14.
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

#ifndef E6BCalcEngine_CME6BCalculation_h
#define E6BCalcEngine_CME6BCalculation_h

#import "CMValue.h"
#import "CMMeasurement.h"

/************************************************************************/
/*																		*/
/*	Interface															*/
/*																		*/
/************************************************************************/

/*
 *	Common protocol for calculations kept by E6B
 */

@protocol CME6BCalculation

- (NSString *)calculationName;
- (NSString *)calculationDescription;

- (int)inputFieldCount;
- (NSString *)inputFieldName:(int)index;
- (id<CMMeasurement>)inputFieldUnit:(int)index;

- (int)outputFieldCount;
- (NSString *)outputFieldName:(int)index;
- (id<CMMeasurement>)outputFieldUnit:(int)index;

- (BOOL)intermixResults;

- (void)setOutputValue:(Value)value forField:(int)field;

- (NSArray *)startInputValues;
- (NSArray *)startOutputValues;
- (NSArray *)calculateWithInput:(NSArray *)input;

@end

#endif
