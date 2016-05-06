//
//  CMCalculator.m
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

#import "CMCalculator.h"
#import "CMInputView.h"
#import "CMMutableString.h"

@interface CMCalculator ()
@property (weak) CMInputView *inputView;
@property (assign) int calcType;

@property (assign) double value;	/* Seconds or scalar */
@property (assign) char operator;	/* Current operator */
@property (assign) BOOL inputFlag;	/* True if input, false if value */

@property (assign) BOOL negFlag;
@property (assign) int hasInputColon;
@property (assign) BOOL hasInputDecimal;
@property (strong) CMMutableString *input;
@end

@implementation CMCalculator

- (id)initWithInputView:(CMInputView *)inputView
{
	if (nil != (self = [super init])) {
		self.inputView = inputView;
		self.calcType = CALCTYPE_REAL;

		self.value = 0;
		self.operator = 0;
		self.inputFlag = NO;

		self.negFlag = NO;
		self.input = [[CMMutableString alloc] init];
	}
	return self;
}

- (void)detachInputView
{
	self.inputView = nil;
}

- (void)setCalculatorType:(int)type
{
	self.calcType = type;
}

- (void)dealloc
{
	[[CMCalcKeyboard shared] removeKeyboardReceiver:self];
}

#pragma mark - Calculator Support

- (BOOL)inputRealType
{
	return (self.calcType == CALCTYPE_REAL) || (self.operator == '*') || (self.operator == '/');
}

- (double)keyInputValue
{
	if (self.inputRealType) {
		double val = [self.input doubleValue];
		if (self.negFlag) val = -val;
		return val;
	} else {
		/* Time. Parse to first ':' if present */
		uint32_t h = 0,m = 0,s = 0;
		NSInteger i,len = self.input.length;
		for (i = 0; i < len; ++i) {
			unichar ch = [self.input characterAtIndex:i];
			if ((ch >= '0') && (ch <= '9')) {
				s = (s * 10) + (ch - '0');
			} else if (ch == ':') {
				h = m;
				m = s;
				s = 0;
			}
		}
		return h + (m * 60 + s)/3600.0;
	}
}

- (void)clearInputField
{
	[self.input clear];
	self.negFlag = NO;
	self.hasInputColon = 0;
	self.hasInputDecimal = 0;
}

- (void)collapseOperator
{
	if (!self.inputFlag) return;

	if (self.operator == 0) {
		self.value = self.keyInputValue;
	} else {
		double second = self.keyInputValue;

		switch (self.operator) {
			case '+':
				self.value += second;
				break;
			case '-':
				self.value -= second;
				break;
			case '*':
				self.value *= second;
				break;
			case '/':
				self.value /= second;
				break;
		}
	}

	[self clearInputField];
	self.operator = 0;
	self.inputFlag = NO;
}

#pragma mark - Keyboard Handling

- (void)receiveKeyboardEvent:(char)key
{
	if ((key >= '0') && (key <= '9')) {
		/*
		 *	Append digit
		 */

		if (!self.inputRealType) {
			if (self.hasInputColon >= 3) return;
			if (self.hasInputColon > 0) self.hasInputColon = self.hasInputColon + 1;
		}
		[self.input appendChar:key];
		self.inputFlag = YES;

	} else if ((key == '.') && (self.inputRealType) && (!self.hasInputDecimal)) {
		[self.input appendChar:key];
		self.hasInputDecimal = YES;
		self.inputFlag = YES;

	} else if ((key == ':') && (!self.inputRealType) && (self.hasInputColon == 0)) {
		[self.input appendChar:key];
		self.hasInputColon = 1;
		self.inputFlag = YES;

	} else if (key == '\b') {
		/*
		 *	Backspace
		 */

		unichar ch = [self.input characterAtIndex:self.input.length - 1];
		[self.input deleteChar];
		if (self.hasInputColon > 0) self.hasInputColon = self.hasInputColon - 1;
		if (ch == '.') self.hasInputDecimal = NO;

		if (self.input.length == 0) {
			self.inputFlag = NO;
		}
	} else if (key == '\x7F') {
		[self clearInputField];
		self.inputFlag = NO;
		self.value = 0;
		self.operator = 0;

	} else if (key == '=') {
		[self collapseOperator];
		self.operator = 0;

	} else if ((key == '+') || (key == '-') || (key == '*') || (key == '/')) {
		[self collapseOperator];
		self.operator = key;

	} else if (key == 'N') {
		if (self.inputFlag || self.operator) {
			self.negFlag = !self.negFlag;
			self.inputFlag = YES;
		} else {
			self.value = -self.value;
		}
	}

	[self.inputView updateContents];
}

- (void)didAttachToKeyboard
{
	[self.inputView updateContents];
}

- (void)didDetachFromKeyboard
{
	[self receiveKeyboardEvent:'='];	/* Send '=' sign */
}

#pragma mark - Display Support

- (NSString *)calcDisplay
{
	if (self.inputFlag) {
		if (self.negFlag) {
			NSMutableString *str = [[NSMutableString alloc] init];
			[str appendString:@"-"];
			[str appendString:self.input];
			return str;
		} else {
			if (self.input.length == 0) return @"0";
			return self.input;
		}
	} else {
		if (isinf(self.value)) return @"--";
		if (isnan(self.value)) return @"--";
		if (self.calcType == CALCTYPE_REAL) {
			char buffer[256];
			sprintf(buffer,"%.02f",self.value);
			char *c = buffer;
			while (*c) ++c;
			for (;;) {
				--c;
				if (*c == '.') {
					*c = 0;
					break;
				}
				if (*c == '0') {
					*c = 0;
				} else {
					break;
				}
			}
			return [NSString stringWithUTF8String:buffer];
		} else {
			NSString *format;
			BOOL neg;
			int t = (int)(self.value * 3600 + 0.5);
			if (t < 0) {
				t = -t;
				neg = YES;
			} else {
				neg = NO;
			}

			int h = t / 3600;
			t -= h * 3600;
			int m = t / 60;
			int s = t % 60;

			if (h > 0) {
				format = neg ? @"-%d:%02d:%02d" : @"%d:%02d:%02d";
			} else {
				format = neg ? @"-%d:%02d" : @"%d:%02d";
			}

			return [NSString stringWithFormat:format,m,s];
		}
	}
}

- (double)currentValue
{
	if (self.inputFlag) {
		return [self keyInputValue];
	}
	return self.value;
}

- (void)setCurrentValue:(double)value
{
	self.value = value;

	self.negFlag = NO;
	self.inputFlag = NO;
	self.hasInputColon = 0;
	self.hasInputDecimal = 0;

	[self.inputView updateContents];
}

@end
