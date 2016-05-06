//
//  CMMutableString.m
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


#import "CMMutableString.h"

@implementation CMMutableString

- (id)init
{
	if (nil != (self = [super init])) {
		strLength = 0;
	}
	return self;
}

- (void)appendChar:(unichar)ch
{
	if (strLength < MAXLENGTH) {
		strBuffer[strLength++] = ch;
	}
}

- (void)deleteChar
{
	if (strLength > 0) {
		--strLength;
	}
}

- (void)clear
{
	strLength = 0;
}

- (NSUInteger)length
{
	return strLength;
}

- (unichar)characterAtIndex:(NSUInteger)index
{
	return strBuffer[index];
}

- (void)getCharacters:(unichar *)buffer range:(NSRange)aRange
{
	memmove(buffer, strBuffer + aRange.location, sizeof(unichar) * aRange.length);
}

@end
