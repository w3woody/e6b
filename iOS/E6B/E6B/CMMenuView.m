//
//  CMMenuView.m
//  E6B
//
//  Created by William Woody on 11/27/14.
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

#import "CMMenuView.h"

@implementation CMMenuView

- (void)internalInit
{
	self.backgroundColor = [UIColor whiteColor];
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
		[self internalInit];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        // Initialization code
		[self internalInit];
    }
    return self;
}

- (void)setLabel:(NSString *)label
{
	_label = label;
	[self setNeedsDisplay];
}

- (void)setValue:(NSString *)value
{
	_value = value;
	[self setNeedsDisplay];
}

- (void)setHighlighted:(BOOL)highlighted
{
	[super setHighlighted:highlighted];
	[self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect
{
	[self drawMenuWithFrame:self.bounds label:self.value buttonType:self.highlighted description:self.label];
}


- (void)drawMenuWithFrame: (CGRect)frame label: (NSString*)label buttonType: (BOOL)buttonType description: (NSString*)description
{
    //// General Declarations
    CGContextRef context = UIGraphicsGetCurrentContext();

    //// Color Declarations
    UIColor* buttonColor = [UIColor colorWithRed: 0 green: 0.750 blue: 0.019 alpha: 1];
    UIColor* underlineColor = [UIColor colorWithRed: 0.816 green: 0.93 blue: 0.815 alpha: 1];
    UIColor* units = [UIColor colorWithRed: 0.454 green: 0.454 blue: 0.454 alpha: 1];

    //// Variable Declarations
    UIColor* underline = buttonType ? buttonColor : underlineColor;

    //// BottomBorder Drawing
	if (buttonType) {
		UIBezierPath* bottomBorderPath = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame), CGRectGetMinY(frame) + CGRectGetHeight(frame) - 1, CGRectGetWidth(frame), 1)];
		[underline setFill];
		[bottomBorderPath fill];
	}

    //// TextDisplay Drawing
    CGRect textDisplayRect = CGRectMake(CGRectGetMinX(frame) + 35, CGRectGetMinY(frame) + CGRectGetHeight(frame) - 30, CGRectGetWidth(frame) - 69, 29);
    NSMutableParagraphStyle* textDisplayStyle = NSMutableParagraphStyle.defaultParagraphStyle.mutableCopy;
    textDisplayStyle.alignment = NSTextAlignmentRight;

    NSDictionary* textDisplayFontAttributes = @{NSFontAttributeName: [UIFont fontWithName: @"HelveticaNeue-Light" size: 25], NSForegroundColorAttributeName: buttonColor, NSParagraphStyleAttributeName: textDisplayStyle};

    CGFloat textDisplayTextHeight = [label boundingRectWithSize: CGSizeMake(textDisplayRect.size.width, INFINITY)  options: NSStringDrawingUsesLineFragmentOrigin attributes: textDisplayFontAttributes context: nil].size.height;
    CGContextSaveGState(context);
    CGContextClipToRect(context, textDisplayRect);
    [label drawInRect: CGRectMake(CGRectGetMinX(textDisplayRect), CGRectGetMinY(textDisplayRect) + CGRectGetHeight(textDisplayRect) - textDisplayTextHeight, CGRectGetWidth(textDisplayRect), textDisplayTextHeight) withAttributes: textDisplayFontAttributes];
    CGContextRestoreGState(context);


    //// TextLabel Drawing
    CGRect textLabelRect = CGRectMake(CGRectGetMinX(frame) + 35, CGRectGetMinY(frame), CGRectGetWidth(frame) - 70, CGRectGetHeight(frame) - 30);
    NSMutableParagraphStyle* textLabelStyle = NSMutableParagraphStyle.defaultParagraphStyle.mutableCopy;
    textLabelStyle.alignment = NSTextAlignmentRight;

    NSDictionary* textLabelFontAttributes = @{NSFontAttributeName: [UIFont fontWithName: @"HelveticaNeue-Light" size: 14], NSForegroundColorAttributeName: units, NSParagraphStyleAttributeName: textLabelStyle};

    [description drawInRect: textLabelRect withAttributes: textLabelFontAttributes];


    //// Bezier Drawing
    UIBezierPath* bezierPath = UIBezierPath.bezierPath;
    [bezierPath moveToPoint: CGPointMake(CGRectGetMaxX(frame) - 21, CGRectGetMinY(frame) + 0.34000 * CGRectGetHeight(frame))];
    [bezierPath addLineToPoint: CGPointMake(CGRectGetMaxX(frame) - 13, CGRectGetMinY(frame) + 0.50000 * CGRectGetHeight(frame))];
    [bezierPath addLineToPoint: CGPointMake(CGRectGetMaxX(frame) - 21, CGRectGetMinY(frame) + 0.66000 * CGRectGetHeight(frame))];
    [buttonColor setStroke];
    bezierPath.lineWidth = 1;
    [bezierPath stroke];
}

@end
