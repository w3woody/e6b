//
//  CMTextField.m
//  E6B
//
//  Created by William Woody on 11/28/14.
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

#import "CMTextField.h"

@interface CMTextField ()
@property (strong) UITextField *textField;
@end

@implementation CMTextField

- (id)initWithFrame:(CGRect)frame
{
	if (nil != (self = [super initWithFrame:frame])) {
		[self internalInit];
	}
	return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
	if (nil != (self = [super initWithCoder:aDecoder])) {
		[self internalInit];
	}
	return self;
}

- (void)internalInit
{
	self.textField = [[UITextField alloc] initWithFrame:CGRectZero];
	self.textField.textColor = [UIColor colorWithRed: 0 green: 0.750 blue: 0.019 alpha: 1];
	self.textField.font = [UIFont fontWithName: @"HelveticaNeue-Light" size: 25];
	self.textField.text = @"Test";
	self.textField.textAlignment = NSTextAlignmentRight;
	self.textField.contentVerticalAlignment = UIControlContentVerticalAlignmentBottom;

	self.textField.delegate = self;

	self.backgroundColor = [UIColor whiteColor];

	[self addSubview:self.textField];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
	[self setNeedsDisplay];
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
	[self setNeedsDisplay];
	if (self.updateContents) {
		self.updateContents(self.textField.text);
	}
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
	[self.textField resignFirstResponder];
	if (self.updateContents) {
		self.updateContents(self.textField.text);
	}
	return NO;
}

- (void)setText:(NSString *)text
{
	self.textField.text = text;
}

- (NSString *)text
{
	return self.textField.text;
}

- (void)layoutSubviews
{
	CGRect r = self.bounds;
	r.size.width -= 34;
	r.size.height -= 1;
	r.origin.y += 1;

	[self.textField setFrame:r];

	[self setNeedsDisplay];
}

- (void)setLabel:(NSString *)label
{
	_label = label;
	[self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect
{
	[self drawAircraftWithFrame:self.bounds buttonType:self.textField.isFirstResponder description:self.label];
}

- (void)drawAircraftWithFrame: (CGRect)frame buttonType: (BOOL)buttonType description: (NSString*)description
{
    //// Color Declarations
    UIColor* buttonColor = [UIColor colorWithRed: 0 green: 0.750 blue: 0.019 alpha: 1];
    UIColor* underlineColor = [UIColor colorWithRed: 0.816 green: 0.93 blue: 0.815 alpha: 1];
    UIColor* units = [UIColor colorWithRed: 0.454 green: 0.454 blue: 0.454 alpha: 1];

    //// Variable Declarations
    UIColor* underline = buttonType ? buttonColor : underlineColor;

    //// TextLabel Drawing
    CGRect textLabelRect = CGRectMake(CGRectGetMinX(frame), CGRectGetMinY(frame), CGRectGetWidth(frame) - 35, CGRectGetHeight(frame) - 30);
    NSMutableParagraphStyle* textLabelStyle = NSMutableParagraphStyle.defaultParagraphStyle.mutableCopy;
    textLabelStyle.alignment = NSTextAlignmentRight;

    NSDictionary* textLabelFontAttributes = @{NSFontAttributeName: [UIFont fontWithName: @"HelveticaNeue-Light" size: 14], NSForegroundColorAttributeName: units, NSParagraphStyleAttributeName: textLabelStyle};

    [description drawInRect: textLabelRect withAttributes: textLabelFontAttributes];


    //// BottomBorder Drawing
	if (buttonType) {
		UIBezierPath* bottomBorderPath = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame), CGRectGetMinY(frame) + CGRectGetHeight(frame) - 1, CGRectGetWidth(frame), 1)];
		[underline setFill];
		[bottomBorderPath fill];
	}
}

@end
