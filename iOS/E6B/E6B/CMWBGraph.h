//
//  CMWBGraph.h
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

#import <UIKit/UIKit.h>

@class CMWBAircraft;

@interface CMWBPair : NSObject
{
	double weight;
	double arm;
}
@property (assign) double weight;
@property (assign) double arm;
@end

@interface CMWBGraph : UIView
{
	CMWBAircraft *aircraft;
	double totWeight;
	double totArm;
	double emptyWeight;
	double emptyArm;
	NSArray *weights;
	
	double minWeight,maxWeight;
	double minArm,maxArm;
	
	CGRect graphArea;
	double scaleWeight;
	double offsetWeight;
	double scaleArm;
	double offsetArm;
	
	double startWeight;
	double stepWeight;
	int numWeight;
	double lineWeight;
	
	double startArm;
	double stepArm;
	int numArm;
	double lineArm;
}

@property (retain) CMWBAircraft *aircraft;
@property (assign) double totWeight;
@property (assign) double totArm;
@property (retain) NSArray *weights;	/* Different fuel weights */

- (void)reloadGraph;

@end
