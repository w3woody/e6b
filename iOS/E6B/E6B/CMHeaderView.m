//
//  CMHeaderView.m
//  E6B
//
//  Created by William Woody on 10/27/14.
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

#import "CMHeaderView.h"

@implementation CMHeaderView

- (id)initWithCoder:(NSCoder *)aDecoder
{
	if (nil != (self = [super initWithCoder:aDecoder])) {
		self.backgroundColor = [UIColor colorWithWhite:0.93 alpha:1.0];
	}
	return self;
}

- (id)initWithFrame:(CGRect)frame
{
	if (nil != (self = [super initWithFrame:frame])) {
		self.backgroundColor = [UIColor colorWithWhite:0.93 alpha:1.0];
	}
	return self;
}

- (void)setLabel:(NSString *)label
{
	_label = label;
	[self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect
{
	[self drawHeaderWithFrame:self.bounds label:self.label];
}

- (void)drawHeaderWithFrame: (CGRect)frame label: (NSString*)label
{
    //// General Declarations
    CGContextRef context = UIGraphicsGetCurrentContext();

    //// Text Drawing
    CGRect textRect = CGRectMake(CGRectGetMinX(frame) + 10, CGRectGetMinY(frame), CGRectGetWidth(frame) - 10, CGRectGetHeight(frame));
    NSMutableParagraphStyle* textStyle = NSMutableParagraphStyle.defaultParagraphStyle.mutableCopy;
    textStyle.alignment = NSTextAlignmentLeft;

    NSDictionary* textFontAttributes = @{NSFontAttributeName: [UIFont systemFontOfSize: 13], NSForegroundColorAttributeName: UIColor.blackColor, NSParagraphStyleAttributeName: textStyle};

    CGFloat textTextHeight = [label boundingRectWithSize: CGSizeMake(textRect.size.width, INFINITY)  options: NSStringDrawingUsesLineFragmentOrigin attributes: textFontAttributes context: nil].size.height;
    CGContextSaveGState(context);
    CGContextClipToRect(context, textRect);
    [label drawInRect: CGRectMake(CGRectGetMinX(textRect), CGRectGetMinY(textRect) + (CGRectGetHeight(textRect) - textTextHeight) / 2, CGRectGetWidth(textRect), textTextHeight) withAttributes: textFontAttributes];
    CGContextRestoreGState(context);
}

@end
