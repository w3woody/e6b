//
//  CMInputView.m
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

#import "CMInputView.h"
#import "CMCalculator.h"
#import "CMCalcKeyboard.h"
#import "NSString+DrawString.h"
#import "CMDrawingSupport.h"

@interface CMInputView ()
@property (strong) CMCalculator *calc;
@end

@implementation CMInputView

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

- (void)dealloc
{
	[self.calc detachInputView];
}

- (void)setTimeEditor:(BOOL)timeEditor
{
	_timeEditor = timeEditor;
	[self.calc setCalculatorType:self.timeEditor ? CALCTYPE_TIME : CALCTYPE_REAL];
}

- (void)internalInit
{
	self.calc = [[CMCalculator alloc] initWithInputView:self];
	self.backgroundColor = [UIColor clearColor];
	self.contentMode = UIViewContentModeRedraw;
	self.timeEditor = NO;
}

- (void)drawRect:(CGRect)rect
{
	[self drawInputWithFrame:self.bounds label:self.calc.calcDisplay buttonType:self.isSelected description:self.label unit:self.unit showDropMenu:self.showMenu];
}

- (void)updateContents
{
	[self setNeedsDisplay];
	[[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_INPUTUPDATE object:self];
}

- (double)value
{
	return self.calc.currentValue;
}

- (void)setValue:(double)value
{
	[self.calc setCurrentValue:value];
}

- (void)setUnit:(NSString *)unit
{
	_unit = unit;
	[self setNeedsDisplay];
}

- (void)setLabel:(NSString *)label
{
	_label = label;
	[self setNeedsDisplay];
}

- (BOOL)isSelected
{
	return [[CMCalcKeyboard shared] isAttachedToKeyboard:self.calc];
}

- (BOOL)becomeFirstResponder
{
	BOOL flag = [super becomeFirstResponder];
	if (flag) {
		[[CMCalcKeyboard shared] setKeyboardReceiver:self.calc];
	}
	return flag;
}

- (BOOL)resignFirstResponder
{
	BOOL flag = [super resignFirstResponder];
	if (flag) {
		[[CMCalcKeyboard shared] removeKeyboardReceiver:self.calc];
	}
	return flag;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
	[self becomeFirstResponder];

	CGPoint pt = [[touches anyObject] locationInView:self];
	CGRect r = self.bounds;
	r.size.width = 44;
	if (self.menuPicker && CGRectContainsPoint(r, pt)) {
		self.menuPicker();
		return;
	}

	r = self.bounds;
	r.origin.x += r.size.width - 44;
	r.size.width = 44;
	if (self.unitPicker && CGRectContainsPoint(r, pt)) {
		self.unitPicker();
		return;
	}
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
}

- (BOOL)canBecomeFirstResponder
{
	return YES;
}

- (void)drawDropMenuWithFrame:(CGRect)frame
{
	UIColor *darkGreen = [UIColor colorWithRed:0.149 green:0.603 blue:0.149 alpha:1];
	[darkGreen setFill];

    //// Rectangle Drawing
    UIBezierPath* rectanglePath = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame), CGRectGetMinY(frame), 1, 1)];
    [rectanglePath fill];

    //// Rectangle 2 Drawing
    UIBezierPath* rectangle2Path = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame) + 3, CGRectGetMinY(frame), 11, 1)];
    [rectangle2Path fill];

    //// Rectangle 3 Drawing
    UIBezierPath* rectangle3Path = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame), CGRectGetMinY(frame) + 3, 1, 1)];
    [rectangle3Path fill];

    //// Rectangle 4 Drawing
    UIBezierPath* rectangle4Path = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame) + 3, CGRectGetMinY(frame) + 3, 11, 1)];
    [rectangle4Path fill];

    //// Rectangle 5 Drawing
    UIBezierPath* rectangle5Path = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame), CGRectGetMinY(frame) + 6, 1, 1)];
    [rectangle5Path fill];

    //// Rectangle 6 Drawing
    UIBezierPath* rectangle6Path = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame) + 3, CGRectGetMinY(frame) + 6, 11, 1)];
    [rectangle6Path fill];

    //// Rectangle 7 Drawing
    UIBezierPath* rectangle7Path = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame), CGRectGetMinY(frame) + 9, 1, 1)];
    [rectangle7Path fill];

    //// Rectangle 8 Drawing
    UIBezierPath* rectangle8Path = [UIBezierPath bezierPathWithRect: CGRectMake(CGRectGetMinX(frame) + 3, CGRectGetMinY(frame) + 9, 11, 1)];
    [rectangle8Path fill];
}

- (void)drawInputWithFrame: (CGRect)frame label: (NSString*)label buttonType: (BOOL)buttonType description: (NSString*)description unit: (NSString*)unit showDropMenu: (BOOL)showDropMenu;
{
	UIColor *darkGreen = [UIColor colorWithRed:0.149 green:0.603 blue:0.149 alpha:1];
	UIColor *lightGreen = [UIColor colorWithRed:0.8 green:1.0 blue:0.8 alpha:1.0];

	UIFont *velFont = [UIFont fontWithName:@"HelveticaNeue-Light" size:14];
	UIFont *dispFont = [UIFont fontWithName:@"HelveticaNeue-Light" size:25];
	CGRect tmp;
	CGRect r;

	/*
	 *	Draw the description and calculate the frame
	 */

	if ([description length] == 0) {
		tmp = CGRectInset(frame, 0.5, 3.5);
	} else {
		tmp = frame;
		tmp.size.width -= 30;
		tmp.size.height -= 32;

		// Draw label
		[description drawInRect:tmp font:velFont color:[UIColor darkGrayColor] alignment:NSTextAlignmentRight minSize:11];

		tmp = CGRectInset(frame, 0.5, 0.5);
		tmp.origin.y += tmp.size.height - 30;
		tmp.size.height = 30;
	}

	/*
	 *	Draw ring
	 */

    UIBezierPath* path = [UIBezierPath bezierPathWithRoundedRect:tmp cornerRadius:6];
	if (buttonType) {
		[darkGreen setStroke];
	} else {
		[lightGreen setStroke];
	}
    path.lineWidth = 1;
    [path stroke];

	/*
	 *	Draw unit
	 */

	r = tmp;
	if (showDropMenu) {
		[self drawDropMenuWithFrame:CGRectMake(r.origin.x + 5, r.origin.y + floor((r.size.height - 8)/2), 14, 8)];
		r.origin.x += 24;
		r.size.width -= 24;
	}

	tmp.origin.x += tmp.size.width - 27;
	tmp.size.width = 27;
	tmp.origin.y += 6;
	tmp.size.height -= 6;
	[unit drawInRect:tmp font:velFont color:darkGreen alignment:NSTextAlignmentLeft minSize:9];

	r.size.width -= 30;
	[label drawInRect:r font:dispFont color:darkGreen alignment:NSTextAlignmentRight minSize:11];
}

@end
