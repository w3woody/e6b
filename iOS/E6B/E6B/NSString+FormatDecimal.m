//
//  NSString+FormatDecimal.m
//  E6B
//
//  Created by William Woody on 1/28/15.
//  Copyright (c) 2015 William Woody. All rights reserved.
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

#import "NSString+FormatDecimal.h"

@implementation NSString (FormatDecimal)

- (NSString *)trimTrailingDecimal
{
	BOOL hasDecimal = NO;
	NSInteger trimPoint = 0;
	NSInteger i,len = [self length];
	for (i = 0; i < len; ++i) {
		unichar ch = [self characterAtIndex:i];
		if (ch == '.') {
			hasDecimal = YES;
			trimPoint = i;
		} else if (hasDecimal) {
			if (ch != '0') {
				trimPoint = i+1;
			}
		}
	}

	if (hasDecimal && (trimPoint < len)) {
		return [self substringToIndex:trimPoint];
	} else {
		return self;
	}
}


@end
