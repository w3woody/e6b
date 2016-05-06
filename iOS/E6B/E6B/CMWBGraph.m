//
//  CMWBGraph.m
//  E6B
//
//  Created by William Woody on 10/23/12.
//  Copyright (c) 2012 William Woody. All rights reserved.
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

#import "CMWBGraph.h"
#import "CMWBData.h"
#import "CMMeasurement.h"
#import "CMMath.h"
#import "NSString+DrawString.h"


/************************************************************************/
/*																		*/
/*	Class Common														*/
/*																		*/
/************************************************************************/

@implementation CMWBPair
@synthesize weight;
@synthesize arm;
@end


@implementation CMWBGraph

@synthesize aircraft;
@synthesize totArm;
@synthesize totWeight;
@synthesize weights;

/************************************************************************/
/*																		*/
/*	Class Common														*/
/*																		*/
/************************************************************************/

- (void)internalInit
{
	self.backgroundColor = [UIColor clearColor];
	[self reloadGraph];
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

/************************************************************************/
/*																		*/
/*	Test Support														*/
/*																		*/
/************************************************************************/

- (BOOL)inEnvelopeWithWeight:(double)w arm:(double)a
{
	// TODO: Calculate if inside or outside
	return YES;
}

/************************************************************************/
/*																		*/
/*	Va Calculation														*/
/*																		*/
/************************************************************************/

- (double)vaForWeight:(double)weight
{
	/* Va' = Va * sqrt( weight / gross_weight ) */
	
	double tmp = weight / aircraft.wmax;
	return aircraft.va * sqrt(tmp);
}

/************************************************************************/
/*																		*/
/*	Graph layout/drawing												*/
/*																		*/
/************************************************************************/

- (void)layoutSubviews
{
	[self reloadGraph];
}

/*	ValueFromIndex
 *
 *		Returns 1,2,5,10,20,50,etc., for tick mark and value placement
 */

static double ValueFromIndex(int index)
{
	int a[3] = { 1, 2, 5 };
	int x = index % 3;
	int y = index / 3;
	
	double v = a[x];
	while (y-- > 0) v *= 10;
	return v;
}

/*	StepSpacing
 *
 *		Give the step spacing given the width
 */

static double StepSpacing(double scale, int pwidth)
{
	int i = 0;
	scale = fabs(scale);
	for (;;) {
		double v = ValueFromIndex(i++);
		if (v * scale >= pwidth) return v;
	}
}

/*
 *	Given the scale, find line spacing to give us about 15 to 30 pixel
 *	tickmarks
 */

static double LineSpacing(double scale)
{
	return StepSpacing(scale,15);
}

/*
 *	Calculate all of the graphing parameters to quickly graph the W&B plot
 */

- (void)reloadGraph
{
	double w,a,m;
	
	/*
	 *	Find the min weight, max weight on the graph
	 */
	
	minWeight = 99999999;
	maxWeight = 0;
	minArm = 99999999;
	maxArm = 0;
	if (aircraft) {
		for (CMWBAircraftWBRange *wg in aircraft.data) {
			for (CMWBAircraftDataPoint *dp in wg.data) {
				w = dp.weight;
				a = dp.arm;
				
				if (minWeight > w) minWeight = w;
				if (maxWeight < w) maxWeight = w;
				if (minArm > a) minArm = a;
				if (maxArm < a) maxArm = a;
			}
		}
	}
	
	/*
	 *	Factor in total weight/arm
	 */
		
	if (minWeight > totWeight) minWeight = totWeight;
	if (maxWeight < totWeight) maxWeight = totWeight;
	if (minArm > totArm) minArm = totArm;
	if (maxArm < totArm) maxArm = totArm;
	
	/*
	 *	Factor in fuel ranges
	 */
	
	m = totWeight * totArm;
	w = totWeight;
	for (CMWBPair *pair in weights) {
		w -= pair.weight;
		m -= pair.weight * pair.arm;
	}
	if (w > 0) {
		a = m / w;
	} else {
		a = 0;
	}
	if (minWeight > w) minWeight = w;
	if (maxWeight < w) maxWeight = w;
	if (minArm > a) minArm = a;
	if (maxArm < a) maxArm = a;
	emptyWeight = w;
	emptyArm = a;
	
	/*
	 *	If this is zeroish, create a default range
	 */
	
	if ((maxWeight < minWeight) || (maxArm < minArm)) {
		minWeight = 500;
		maxWeight = 1500;
		minArm = 10;
		maxArm = 30;
	}
	
	/*
	 *	Guarantee minimum range
	 */
	
	maxWeight += 100;		/* Buffer (with min at zero) */
	minArm -= 2;
	maxArm += 2;
	
	/*
	 *	Calculate the tick marks and graph drawing area
	 */
	
	UIFont *f = [UIFont systemFontOfSize:12];	/* Tiny label font */
	NSString *slabel = [NSString stringWithFormat:@"%d",(int)maxWeight];
	
	CGSize size = [slabel stringSizeWithFont:f];
	
	graphArea = self.bounds;
	graphArea.size.height -= size.height;		/* Bump up for bottom label */
	graphArea.origin.x += size.width + 10;		/* Bump in left for labels */
	graphArea.size.width -= size.width + 10;
	
	/*
	 *	Calculate the scale and offset to draw in graph area
	 */
	
	double minp = graphArea.origin.x;
	double maxp = graphArea.origin.x + graphArea.size.width;
	scaleArm = (maxp - minp)/(maxArm - minArm);
	offsetArm = minp - scaleArm * minArm;		/* arm * scaleArm + offsetArm = pos */
	
	maxp = graphArea.origin.y;
	minp = graphArea.origin.y + graphArea.size.height;
	scaleWeight = (maxp - minp)/(maxWeight - minWeight);
	offsetWeight = minp - scaleWeight * minWeight;
	
	/*
	 *	Calculate the tick mark locations
	 */
	
	lineWeight = LineSpacing(scaleWeight);
	lineArm = LineSpacing(scaleArm);
	
	/*
	 *	Calculate the label locations
	 */
	
	stepWeight = StepSpacing(scaleWeight, 50);
	stepArm = StepSpacing(scaleArm, 80);
	
	double tmpb = ceil(minWeight / stepWeight) + 1;
	double tmpt = floor(maxWeight / stepWeight);
	startWeight = tmpb * stepWeight;
	numWeight = (int)(tmpt - tmpb + 1);
	
	tmpb = ceil(minArm / stepArm);
	tmpt = floor(maxArm / stepArm);
	startArm = tmpb * stepArm;
	numArm = (int)(tmpt - tmpb + 1);
	
	[self setNeedsDisplay];
}

- (void)drawWeight:(double)weight
{
	UIFont *font = [UIFont systemFontOfSize:12];

	int ypos = offsetWeight + scaleWeight * weight;
	CGRect r = self.bounds;
	r.origin.y = ypos - (font.lineHeight)/2;
	r.size.height = font.lineHeight;
	if (r.origin.y < 0) r.origin.y = 0;
	if (r.size.height + r.origin.y > graphArea.size.height + graphArea.origin.y) {
		r.origin.y = graphArea.size.height + graphArea.origin.y - r.size.height;
	}
	
	NSString *str = [NSString stringWithFormat:@"%d",(int)weight];
	[str drawInRect:r font:font color:[UIColor darkGrayColor] alignment:NSTextAlignmentLeft];
}

- (void)drawArm:(double)arm
{
	UIFont *font = [UIFont systemFontOfSize:12];

	int xpos = offsetArm + scaleArm * arm;
	NSString *str = [NSString stringWithFormat:@"%d",(int)arm];
	CGSize size = [str stringSizeWithFont:font];

	CGRect r = graphArea;
	r.origin.y += r.size.height;
	r.size.height = font.lineHeight;
	r.origin.x = xpos - (size.width)/2;
	r.size.width = size.width;
	
	if (r.origin.x < graphArea.origin.x) r.origin.x = graphArea.origin.x;
	if (r.origin.x + r.size.width > graphArea.origin.x + graphArea.size.width) {
		r.origin.x = graphArea.origin.x + graphArea.size.width - r.size.width;
	}
	
	[str drawInRect:r font:font color:[UIColor darkGrayColor] alignment:NSTextAlignmentLeft];
}

- (void)drawVALabel:(CGPoint)loc speed:(double)s
{
	CGRect r = self.bounds;
	
	CGContextRef ref = UIGraphicsGetCurrentContext();
	UIFont *font = [UIFont systemFontOfSize:12];
	NSString *str = [NSString stringWithFormat:@"Va: %d %@",(int)(s + 0.5),[GSpeed.abbrMeasure objectAtIndex:aircraft.speedUnit]];

	BOOL rhs = YES;
	CGSize size = [str stringSizeWithFont:font];
	if (loc.x + size.width + 40 > r.size.width) rhs = NO;
	
	if (rhs) loc.x += 5;
	else loc.x -= 5;
	
	/*
	 *	Build the flag
	 */
	
	int h2 = font.lineHeight/2 + 4;
	int dx = rhs ? h2 : -h2;
	int dw = rhs ? size.width + 10 : -(size.width + 10);
	CGContextMoveToPoint(ref, loc.x, loc.y);
	CGContextAddLineToPoint(ref, loc.x + dx, loc.y + h2);
	CGContextAddLineToPoint(ref, loc.x + dx + dw, loc.y + h2);
	CGContextAddLineToPoint(ref, loc.x + dx + dw, loc.y - h2);
	CGContextAddLineToPoint(ref, loc.x + dx, loc.y - h2);
	CGContextAddLineToPoint(ref, loc.x, loc.y);
	
	CGPathRef path = CGContextCopyPath(ref);
	[[UIColor whiteColor] setFill];
	CGContextFillPath(ref);
	[[UIColor grayColor] setStroke];
	CGContextAddPath(ref, path);
	CGContextStrokePath(ref);
	CGPathRelease(path);
	
	if (rhs) {
		r = CGRectMake(loc.x + dx + 5, loc.y - h2 + 2,size.width,size.height);
	} else {
		r = CGRectMake(loc.x + dx + dw + 5, loc.y - h2 + 2,size.width,size.height);
	}

	[str drawInRect:r font:font color:[UIColor blackColor] alignment:NSTextAlignmentLeft];
}

/*
 *	Given weight/arm, find point
 */

- (CGPoint)findPointWithWeight:(double)w arm:(double)a
{
	CGPoint pt;
	pt.x = offsetArm + scaleArm * a;
	pt.y = offsetWeight + scaleWeight * w;
	return pt;
}


- (void)drawRect:(CGRect)rect
{
	/*
	 *	Step 1: Draw the graph grid
	 */
	
	CGContextRef ref = UIGraphicsGetCurrentContext();

	/* Border */
	[[UIColor colorWithRed:0.85 green:0.85 blue:0.85 alpha:1.0] setStroke];
	CGContextMoveToPoint(ref, graphArea.origin.x + 0.5, graphArea.origin.y + 0.5);
	CGContextAddLineToPoint(ref, graphArea.origin.x + graphArea.size.width - 0.5, graphArea.origin.y + 0.5);
	CGContextAddLineToPoint(ref, graphArea.origin.x + graphArea.size.width - 0.5, graphArea.origin.y + graphArea.size.height - 0.5);
	CGContextAddLineToPoint(ref, graphArea.origin.x + 0.5, graphArea.origin.y + graphArea.size.height - 0.5);
	CGContextAddLineToPoint(ref, graphArea.origin.x + 0.5, graphArea.origin.y + 0.5);
	CGContextStrokePath(ref);

	/* Clip and grid lines */

	CGContextSaveGState(ref);
	CGContextClipToRect(ref, graphArea);

	int min = (int)ceil(minArm / lineArm);
	int max = (int)floor(maxArm / lineArm);
	for (int i = min; i <= max; ++i) {
		float pos = floor(scaleArm * lineArm * i + offsetArm) + 0.5;
		CGContextMoveToPoint(ref,pos,graphArea.origin.y + 0.5);
		CGContextAddLineToPoint(ref, pos, graphArea.origin.y + graphArea.size.height - 0.5);
	}
	CGContextStrokePath(ref);

	min = (int)ceil(minWeight / lineWeight);
	max = (int)floor(maxWeight / lineWeight);
	for (int i = min; i <= max; ++i) {
		float pos = floor(scaleWeight * lineWeight * i + offsetWeight) + 0.5;
		CGContextMoveToPoint(ref,graphArea.origin.x + 0.5,pos);
		CGContextAddLineToPoint(ref, graphArea.origin.x + graphArea.size.width - 0.5,pos);
	}
	CGContextStrokePath(ref);

	CGContextRestoreGState(ref);

	/* Draw labels */
	[self drawWeight:minWeight];
	for (int i = 0; i < numWeight; ++i) [self drawWeight:startWeight + stepWeight * i];
	for (int i = 0; i < numArm; ++i) [self drawArm:startArm + stepArm * i];
	
	/* Draw the contents */
	if (aircraft) {
		/*
		 *	Draw the graph
		 */
		
		[[UIColor colorWithRed:0.3 green:0.3 blue:0.3 alpha:1.0] setStroke];
		[[UIColor colorWithRed:0.3 green:0.3 blue:0.3 alpha:0.1] setFill];
		
		for (CMWBAircraftWBRange *r in aircraft.data) {
			NSInteger i,len = [r.data count];
			if (len == 0) continue;
			
			CMWBAircraftDataPoint *pt = [r.data objectAtIndex:len-1];
			double y = offsetWeight + scaleWeight * pt.weight;
			double x = offsetArm + scaleArm * pt.arm;
			CGContextMoveToPoint(ref, x, y);
			
			for (i = 0; i < len; ++i) {
				CMWBAircraftDataPoint *pt = [r.data objectAtIndex:i];
				y = offsetWeight + scaleWeight * pt.weight;
				x = offsetArm + scaleArm * pt.arm;
				CGContextAddLineToPoint(ref, x, y);
			}
			
			CGPathRef path = CGContextCopyPath(ref);
			CGContextFillPath(ref);
			CGContextAddPath(ref, path);
			CGContextStrokePath(ref);
			CGPathRelease(path);
		}
	}
	
	/* Draw the total, total less fuel points */
	if ((totWeight != 0) && (totArm != 0)) {
		CGPoint twhere;
		CGPoint where;
		double w,a,m;

		[[UIColor colorWithRed:0 green:0.5 blue:0 alpha:1] setFill];
		[[UIColor colorWithRed:0 green:0.5 blue:0 alpha:1] setStroke];

		twhere = [self findPointWithWeight:totWeight arm:totArm];
		CGContextFillEllipseInRect(ref, CGRectMake(twhere.x-4,twhere.y-4,8,8));
		
		if ([weights count] > 0) {
			if ([weights count] == 1) {
				CMWBPair *p = [weights objectAtIndex:0];
				
				/*
				 *	This is a line. Draw it
				 */
				
				CGContextMoveToPoint(ref, twhere.x, twhere.y);
				for (double fw = 10; fw < p.weight; fw += 10) {
					w = totWeight - fw;
					m = totWeight * totArm - fw * p.arm;
					a = m / w;
					where = [self findPointWithWeight:w arm:a];
					CGContextAddLineToPoint(ref, where.x, where.y);
				}
				
				w = totWeight - p.weight;
				m = totWeight * totArm - p.weight * p.arm;
				a = m / w;
				where = [self findPointWithWeight:w arm:a];
				CGContextAddLineToPoint(ref, where.x, where.y);

				CGContextStrokePath(ref);
			} else {
				/*
				 *	This is a matrix of values. We need to find the bounding
				 *	box of this. We do this by (a) finding all the min/max
				 *	points (meaning the number of possible bounding points is
				 *	2 ** (# weights), and (b) finding all of the weight/moment
				 *	points. We then find the bounding polygon then draw back
				 *	in weight/arm space
				 */
				
				NSInteger nw = [weights count];
				int num = 1 << nw;
				CMPoint *plist = (CMPoint *)malloc(sizeof(CMPoint) * num);
				
				for (int index = 0; index < num; ++index) {
					w = totWeight;
					m = totWeight * totArm;
					for (int n = 0; n < nw; ++n) {
						if (index & (1 << n)) {
							CMWBPair *p = [weights objectAtIndex:n];
							w -= p.weight;
							m -= p.weight * p.arm;
						}
					}
					plist[index].x = m;
					plist[index].y = w;
				}
				
				/*
				 *	At this point plist is all possible min/max bounds on the
				 *	weight/moment space. Find the bounding polygon
				 */
				
				CMPolygon *poly = E6BFindConvexHull(num, plist);
				free(plist);
				
				/*
				 *	Draw the polygon in w/a space
				 */
				
				// Note: min w will be first item
				m = poly->points[0].x;
				w = poly->points[0].y;
				a = m / w;
				where = [self findPointWithWeight:w arm:a];
				CGContextMoveToPoint(ref, where.x, where.y);

				for (int index = poly->length-1; index >= 0; --index) {
					double mm = poly->points[index].x;
					double ww = poly->points[index].y;
					double aa = mm / ww;
					
					for (double alpha = 0.1; alpha < 1.0; alpha += 0.1) {
						double lm = m * (1 - alpha) + mm * alpha;
						double lw = w * (1 - alpha) + ww * alpha;
						double la = lm / lw;
						CGPoint lpt = [self findPointWithWeight:lw arm:la];
						CGContextAddLineToPoint(ref, lpt.x, lpt.y);
					}
					
					CGPoint lpt = [self findPointWithWeight:ww arm:aa];
					CGContextAddLineToPoint(ref, lpt.x, lpt.y);
					
					m = mm;
					w = ww;
				}
				
				free(poly);

				[[UIColor colorWithRed:0 green:0.5 blue:0 alpha:0.3] setFill];

				CGPathRef path = CGContextCopyPath(ref);
				CGContextFillPath(ref);
				CGContextAddPath(ref, path);
				CGContextStrokePath(ref);
				CGPathRelease(path);
			}
		}

		/*
		 *	Draw VA markers
		 */
		
		[self drawVALabel:twhere speed:[self vaForWeight:totWeight]];
		
		if ([weights count] > 0) {
			where = [self findPointWithWeight:emptyWeight arm:emptyArm];
			[self drawVALabel:where speed:[self vaForWeight:emptyWeight]];
		}
	}
}

@end
