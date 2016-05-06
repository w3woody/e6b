//
//  CMLayoutUtils.m
//  E6B
//
//  Created by William Woody on 12/13/14.
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

#import "CMLayoutUtils.h"

#define BORDER			12
#define HEIGHT			50
#define YINSET			8

@interface CMLayout ()
{
	int fWidth;		/* Width of display area we're laying out into */
	int fYPos;		/* Y pixel position */
	int fHeight;	/* Height */
}
@end

@implementation CMLayout

- (id)initWithWidth:(int)width yStart:(int)yStart
{
	if (nil != (self = [super init])) {
		fWidth = width - BORDER * 2;
		fYPos = yStart + YINSET;
		fHeight = HEIGHT;
	}
	return self;
}

- (void)advanceRowPosition:(int)height
{
	fYPos += height;
}

- (void)advanceNextRow
{
	fYPos += fHeight;
}

- (void)setRowHeight:(int)height
{
	fHeight = height;
}

- (CGRect)cell:(int)index columnCount:(int)col
{
	return [self cell:index width:1 columnCount:col rightIndent:0];
}

- (CGRect)cell:(int)index width:(int)width columnCount:(int)col
{
	return [self cell:index width:width columnCount:col rightIndent:0];
}

- (CGRect)cell:(int)index columnCount:(int)col rightIndent:(int)indent
{
	return [self cell:index width:1 columnCount:col rightIndent:indent];
}

- (CGRect)cell:(int)index width:(int)width columnCount:(int)col rightIndent:(int)indent
{
	int addIndent = indent - BORDER;
	if (addIndent < 0) addIndent = 0;

	int w = fWidth - addIndent - BORDER * (col - 1);

	int xleft = (w * index)/col + BORDER + index * BORDER;
	int xright = (w * (index + width))/col + BORDER * (index + width);
	return CGRectMake(xleft, fYPos, xright - xleft, fHeight);
}

- (CGRect)rightCellIndent:(int)indent
{
	return CGRectMake(fWidth - indent + BORDER * 2, fYPos, indent, fHeight);
}

// Call when done; this returns the bottom (after an implicit call to
// nextRow
- (int)yBottom
{
	return fYPos;
}

@end
