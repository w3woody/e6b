//
//  CMCalculator.h
//  E6B
//
//  Created by William Woody on 9/28/14.
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
#import "CMCalcKeyboard.h"

#define CALCTYPE_REAL			1
#define CALCTYPE_TIME			2

@class CMInputView;

/*
 *	CMCalculator is a controller class which should be associated with a
 *	display class for displaying the results of that calculation. This
 *	serves as a sort of embedded "calculator" as part of an input field,
 *	and is aware of certain types of calculations on time and on scalar
 *	quantities.
 *
 *	This class should be married to a CMInputView object, and when it
 *	receives focus, it should be married to the CMCalcKeyboard object.
 */

@interface CMCalculator : NSObject <CMCalcKeyboardReceiver>

- (id)initWithInputView:(CMInputView *)inputView;
- (void)detachInputView;

- (void)setCalculatorType:(int)type;
- (NSString *)calcDisplay;
- (void)setCurrentValue:(double)value;
- (double)currentValue;

@end
