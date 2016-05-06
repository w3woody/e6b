//
//  NSString+DrawString.m
//  E6B
//
//  Created by William Woody on 12/27/14.
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

#import "NSString+DrawString.h"

@implementation NSString (DrawString)

- (void)drawInRect:(CGRect)area font:(UIFont *)font color:(UIColor *)color alignment:(NSTextAlignment)alignment minSize:(CGFloat)minSize
{
	CGFloat fsize = [font pointSize];
	NSMutableParagraphStyle *para = [[NSParagraphStyle defaultParagraphStyle] mutableCopy];
	para.alignment = alignment;
	NSString *name = [font fontName];

	CGFloat oldDescender = font.descender;

	NSDictionary *d;
	CGSize size;
	for (;;) {
		d = @{ NSFontAttributeName: font,
			   NSForegroundColorAttributeName: color,
			   NSParagraphStyleAttributeName: para };
		size = [self sizeWithAttributes:d];
		if (size.width <= area.size.width) break;
		if (fsize <= minSize) break;

		--fsize;
		font = [UIFont fontWithName:name size:fsize];
	}

	CGFloat newAscender = font.ascender;

	/*
	 *	Note: we use the original descender despite how we shrink things.
	 *	This keeps the base line at the same offset
	 */

	area.origin.y += floor((area.size.height + oldDescender - newAscender)/2);
	area.size.height = ceil(size.height);

	[self drawInRect:area withAttributes:d];
}

- (void)drawInRect:(CGRect)area font:(UIFont *)font color:(UIColor *)color alignment:(NSTextAlignment)alignment
{
	NSMutableParagraphStyle *para = [[NSParagraphStyle defaultParagraphStyle] mutableCopy];
	para.alignment = alignment;

	NSDictionary *d;
	CGSize size;

	d = @{ NSFontAttributeName: font,
		   NSForegroundColorAttributeName: color,
		   NSParagraphStyleAttributeName: para };
	size = [self sizeWithAttributes:d];

	area.origin.y += floor((area.size.height - size.height)/2);
	area.size.height = ceil(size.height);

	[self drawInRect:area withAttributes:d];
}

- (CGSize)stringSizeWithFont:(UIFont *)font
{
	NSDictionary *d = @{ NSFontAttributeName: font };
	CGSize size = [self sizeWithAttributes:d];
	return size;
}

@end
