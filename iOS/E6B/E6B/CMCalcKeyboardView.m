//
//  CMCalcKeyboardView.m
//  E6B
//
//  Created by William Woody on 9/25/14.
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

#import "CMCalcKeyboardView.h"
#import "CMCalcKeyboard.h"

@interface CMCalcKeyboardView ()
@property (strong) NSArray *keys;
@property (assign) int keyPress;
@property (assign) BOOL keyDown;
@end

@implementation CMCalcKeyboardView

static const char *GKeys = "789+\x7F" "456-\b" "123*=" ":0./N";

- (id)initWithCoder:(NSCoder *)aDecoder
{
	if (nil != (self = [super initWithCoder:aDecoder])) {
		[self internalInit];
	}
	return self;
}

- (id)initWithFrame:(CGRect)frame
{
	if (nil != (self = [super initWithFrame:frame])) {
		[self internalInit];
	}
	return self;
}

- (void)internalInit
{
	self.keys = @[ @"7", @"8", @"9", @"+",		@"C",
				   @"4", @"5", @"6", @"\u2212", @"\u2190",
				   @"1", @"2", @"3", @"\u00D7", @"=",
				   @":", @"0", @".", @"\u00F7", @"\u00B1" ];
}

- (CGRect)buttonAtIndex:(int)index
{
	CGRect r = self.bounds;

	int x = index % 5;
	int y = index / 5;

	int ytop = r.origin.y + (y * r.size.height)/4;
	int ybot = r.origin.y + ((y + 1) * r.size.height)/4;

	int xwidth = r.size.width / 5;
	if (xwidth > 64) xwidth = 64;

	int sep = (r.size.width - xwidth * 5)/3;
	if (sep > 48) sep = 48;

	int xborder = (r.size.width - xwidth * 5 - sep)/2;
	int xleft = xborder + x * xwidth + ((x >= 3) ? sep : 0);
	int xright = xborder + (x+1) * xwidth + ((x >= 3) ? sep : 0);

	return CGRectMake(xleft, ytop, xright-xleft, ybot - ytop);
}

- (void)drawRect:(CGRect)rect
{
	for (int i = 0; i < 20; ++i) {
		BOOL rflag = ((i % 5) >= 3);
		if ((i == self.keyPress) && self.keyDown) {
			[self drawDownButtonWithFrame:[self buttonAtIndex:i] label:self.keys[i] buttonType:rflag];
		} else {
			[self drawButtonWithFrame:[self buttonAtIndex:i] label:self.keys[i] buttonType:rflag];
		}
	}
}

#pragma mark - Audio

- (BOOL)enableInputClicksWhenVisible
{
	return YES;
}

- (BOOL)canBecomeFirstResponder
{
	return YES;
}

#pragma mark - Touch Routines

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
	CGPoint pt = [[touches anyObject] locationInView:self];
	self.keyPress = -1;

	int i;
	for (i = 0; i < 20; ++i) {
		if (CGRectContainsPoint([self buttonAtIndex:i], pt)) {
			self.keyPress = i;
			self.keyDown = YES;
			[self setNeedsDisplay];

			[[UIDevice currentDevice] playInputClick];
			break;
		}
	}
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
	if ((self.keyPress != -1) && self.keyDown) {
		self.keyDown = NO;
		[self setNeedsDisplay];

		/* Send event to our keyboard event singleton */
		[[CMCalcKeyboard shared] sendKeyboardEvent:GKeys[self.keyPress]];
	}
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
	if (self.keyPress != -1) {
		CGPoint pt = [[touches anyObject] locationInView:self];
		if (CGRectContainsPoint([self buttonAtIndex:self.keyPress], pt)) {
			if (!self.keyDown) {
				self.keyDown = YES;
				[self setNeedsDisplay];
			}
		} else {
			if (self.keyDown) {
				self.keyDown = NO;
				[self setNeedsDisplay];
			}
		}
	}
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
	self.keyDown = NO;
	[self setNeedsDisplay];
}

#pragma mark - Draw Routines

- (void)drawButtonWithFrame: (CGRect)frame label: (NSString*)label buttonType: (BOOL)buttonType;
{
    //// General Declarations
    CGContextRef context = UIGraphicsGetCurrentContext();

    //// Color Declarations
    UIColor* buttonColor = [UIColor colorWithRed: 0 green: 0.33 blue: 0 alpha: 1];

    //// Shadow Declarations
    UIColor* buttonShadow = UIColor.lightGrayColor;
    CGSize buttonShadowOffset = CGSizeMake(0.1, 1.1);
    CGFloat buttonShadowBlurRadius = 3;

    //// Variable Declarations
    UIColor* color = buttonType ? buttonColor : [UIColor colorWithRed: 0 green: 0 blue: 0 alpha: 1];

    //// Rectangle Drawing
    CGRect rectangleRect = CGRectMake(CGRectGetMinX(frame) + 4, CGRectGetMinY(frame) + 4, CGRectGetWidth(frame) - 8, CGRectGetHeight(frame) - 8);
    UIBezierPath* rectanglePath = [UIBezierPath bezierPathWithRoundedRect: rectangleRect cornerRadius: 4];
    CGContextSaveGState(context);
    CGContextSetShadowWithColor(context, buttonShadowOffset, buttonShadowBlurRadius, [buttonShadow CGColor]);
    [UIColor.whiteColor setFill];
    [rectanglePath fill];
    CGContextRestoreGState(context);

    NSMutableParagraphStyle* rectangleStyle = NSMutableParagraphStyle.defaultParagraphStyle.mutableCopy;
    rectangleStyle.alignment = NSTextAlignmentCenter;

    NSDictionary* rectangleFontAttributes = @{NSFontAttributeName: [UIFont fontWithName: @"HelveticaNeue-Light" size: 21], NSForegroundColorAttributeName: color, NSParagraphStyleAttributeName: rectangleStyle};

    [label drawInRect: CGRectOffset(rectangleRect, 0, (CGRectGetHeight(rectangleRect) - [label boundingRectWithSize: rectangleRect.size options: NSStringDrawingUsesLineFragmentOrigin attributes: rectangleFontAttributes context: nil].size.height) / 2) withAttributes: rectangleFontAttributes];
}

- (void)drawDownButtonWithFrame: (CGRect)frame label: (NSString*)label buttonType: (BOOL)buttonType;
{
    //// General Declarations
    CGContextRef context = UIGraphicsGetCurrentContext();

    //// Color Declarations
    UIColor* buttonColor = [UIColor colorWithRed: 0 green: 0.33 blue: 0 alpha: 1];

    //// Shadow Declarations
    UIColor* insetShadow = UIColor.grayColor;
    CGSize insetShadowOffset = CGSizeMake(0.1, -1.1);
    CGFloat insetShadowBlurRadius = 3;
    UIColor* interiorShadow = UIColor.lightGrayColor;
    CGSize interiorShadowOffset = CGSizeMake(0.1, -0.1);
    CGFloat interiorShadowBlurRadius = 2;

    //// Variable Declarations
    UIColor* color = buttonType ? buttonColor : [UIColor colorWithRed: 0 green: 0 blue: 0 alpha: 1];

    //// Rectangle 2 Drawing
    CGRect rectangle2Rect = CGRectMake(CGRectGetMinX(frame) + 4, CGRectGetMinY(frame) + 4, CGRectGetWidth(frame) - 8, CGRectGetHeight(frame) - 8);
    UIBezierPath* rectangle2Path = [UIBezierPath bezierPathWithRoundedRect: rectangle2Rect cornerRadius: 4];
    CGContextSaveGState(context);
    CGContextSetShadowWithColor(context, insetShadowOffset, insetShadowBlurRadius, [insetShadow CGColor]);
    [UIColor.whiteColor setFill];
    [rectangle2Path fill];

    ////// Rectangle 2 Inner Shadow
    CGContextSaveGState(context);
    UIRectClip(rectangle2Path.bounds);
    CGContextSetShadowWithColor(context, CGSizeZero, 0, NULL);

    CGContextSetAlpha(context, CGColorGetAlpha([interiorShadow CGColor]));
    CGContextBeginTransparencyLayer(context, NULL);
    {
        UIColor* opaqueShadow = [interiorShadow colorWithAlphaComponent: 1];
        CGContextSetShadowWithColor(context, interiorShadowOffset, interiorShadowBlurRadius, [opaqueShadow CGColor]);
        CGContextSetBlendMode(context, kCGBlendModeSourceOut);
        CGContextBeginTransparencyLayer(context, NULL);

        [opaqueShadow setFill];
        [rectangle2Path fill];

        CGContextEndTransparencyLayer(context);
    }
    CGContextEndTransparencyLayer(context);
    CGContextRestoreGState(context);

    CGContextRestoreGState(context);

    NSMutableParagraphStyle* rectangle2Style = NSMutableParagraphStyle.defaultParagraphStyle.mutableCopy;
    rectangle2Style.alignment = NSTextAlignmentCenter;

    NSDictionary* rectangle2FontAttributes = @{NSFontAttributeName: [UIFont fontWithName: @"HelveticaNeue-Light" size: 21], NSForegroundColorAttributeName: color, NSParagraphStyleAttributeName: rectangle2Style};

    [label drawInRect: CGRectOffset(rectangle2Rect, 0, (CGRectGetHeight(rectangle2Rect) - [label boundingRectWithSize: rectangle2Rect.size options: NSStringDrawingUsesLineFragmentOrigin attributes: rectangle2FontAttributes context: nil].size.height) / 2) withAttributes: rectangle2FontAttributes];
}


@end
