//
//  CMCalcKeyboard.m
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

/*
 *	This is a dirt-simple class but allows me to have my keyboard sender
 *	at arms length from the receiver class
 */

#import "CMCalcKeyboard.h"

@interface CMCalcKeyboard ()
@property (weak) id<CMCalcKeyboardReceiver> receiver;
@end

@implementation CMCalcKeyboard

+ (CMCalcKeyboard *)shared
{
	static CMCalcKeyboard *k;

	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		k = [[CMCalcKeyboard alloc] init];
	});
	return k;
}


- (void)sendKeyboardEvent:(char)key
{
	if (self.receiver) [self.receiver receiveKeyboardEvent:key];
}

- (void)setKeyboardReceiver:(id<CMCalcKeyboardReceiver>)receiver
{
	if (self.receiver) {
		[self.receiver didDetachFromKeyboard];
	}
	self.receiver = receiver;
	if (self.receiver) {
		[self.receiver didAttachToKeyboard];
	}
}

- (void)removeKeyboardReceiver:(id<CMCalcKeyboardReceiver>)receiver
{
	/* Detach only if me */
	if ((self.receiver == receiver) && (receiver != nil)) {
		[self.receiver didDetachFromKeyboard];
		self.receiver = nil;
	}
}

- (BOOL)isAttachedToKeyboard:(id<CMCalcKeyboardReceiver>)receiver
{
	return (receiver == self.receiver);
}

@end
