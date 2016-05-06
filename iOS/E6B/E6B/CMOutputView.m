//
//  CMOutputView.m
//  E6B
//
//  Created by William Woody on 10/3/14.
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

#import "CMOutputView.h"
#import "NSString+DrawString.h"

@interface CMOutputView ()
@property (assign) double value;
@end

@implementation CMOutputView

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
#if TARGET_INTERFACE_BUILDER
	self.value = 0;			/* Display Value */
#else
	self.value = INFINITY;	/* Illegal value */
#endif
	self.backgroundColor = [UIColor clearColor];
	self.contentMode = UIViewContentModeRedraw;
}

- (void)drawRect:(CGRect)rect
{
	NSString *disp;
	if (isinf(self.value)) disp = @"--";
	else if (isnan(self.value)) disp = @"--";
	else if (!self.displayTime) {
		char buffer[256];
		sprintf(buffer,"%.02f",self.value);
		char *c = buffer;
		while (*c) ++c;
		for (;;) {
			--c;
			if (*c == '.') {
				*c = 0;
				break;
			}
			if (*c == '0') {
				*c = 0;
			} else {
				break;
			}
		}
		disp = [NSString stringWithUTF8String:buffer];

	} else {
		NSString *format;
		BOOL neg;
		int t = (int)(self.value * 3600 + 0.5);
		if (t < 0) {
			t = -t;
			neg = YES;
		} else {
			neg = NO;
		}

		int h = t / 3600;
		t -= h * 3600;
		int m = t / 60;
		int s = t % 60;

		if (h > 0) {
			format = neg ? @"-%d:%02d:%02d" : @"%d:%02d:%02d";
		} else {
			format = neg ? @"-%d:%02d" : @"%d:%02d";
		}

		disp = [NSString stringWithFormat:format,m,s];
	}


	[self drawOutputWithFrame:self.bounds label:disp description:self.label unit:self.unit];
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

- (void)setOutputValue:(double)value
{
	self.value = value;
	[self setNeedsDisplay];
}

- (void)drawOutputWithFrame: (CGRect)frame label: (NSString*)label description: (NSString*)description unit: (NSString*)unit;
{
	UIColor *darkGreen = [UIColor colorWithRed:0.149 green:0.603 blue:0.149 alpha:1];

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
	 *	Draw unit
	 */

	r = tmp;

	tmp.origin.x += tmp.size.width - 27;
	tmp.size.width = 27;
	tmp.origin.y += 6;
	tmp.size.height -= 6;
	[unit drawInRect:tmp font:velFont color:darkGreen alignment:NSTextAlignmentLeft minSize:9];

	r.size.width -= 30;
	[label drawInRect:r font:dispFont color:[UIColor blackColor] alignment:NSTextAlignmentRight minSize:11];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
	CGPoint pt = [[touches anyObject] locationInView:self];
	CGRect r = self.bounds;

	r.origin.x += r.size.width - 44;
	r.size.width = 44;
	if (self.unitPicker && CGRectContainsPoint(r, pt)) {
		self.unitPicker();
	}
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
}

@end
