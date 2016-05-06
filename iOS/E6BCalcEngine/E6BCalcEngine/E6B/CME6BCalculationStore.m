//
//  CME6BCalculationStore.m
//  E6BCalcEngine
//
//  Created by William Woody on 10/20/14.
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

#import "CME6BCalculationStore.h"
#import "CME6BDataStore.h"

#import "CMPlanCalculation.h"
#import "CMManeuveringSpeed.h"

#import "CMPDAltCalculation.h"
#import "CMTrueAirspeedCalculation.h"
#import "CMReqCalibratedAirspeed.h"
#import "CMCloudBaseCalculator.h"

#import "CMHeadingCalculation.h"
#import "CMFindHeadingCalculation.h"
#import "CMWindCalculator.h"
#import "CMReqTrueAirspeed.h"
#import "CMCrossWind.h"

#import "CMMagneticHeadingCalculation.h"
#import "CMTrueHeadingCalculation.h"

#import "CMSpeedCalculation.h"
#import "CMDistanceTraveled.h"
#import "CMLegTimeCalculator.h"

#import "CMFuelRequired.h"
#import "CMFuelBurnRate.h"
#import "CMFuelEndurance.h"

#import "CMUnitCalculations.h"
#import "CMFuelWeight.h"
#import "CMFuelForWeight.h"


@interface CME6BCalculationStore ()
@property (strong) NSArray *groupNames;
@property (strong) NSArray *itemArray;
@end

@implementation CME6BCalculationStore

- (id)init
{
	if (nil != (self = [super init])) {
		E6BSetupStandardUnits();
		LoadValues();

		self.groupNames = @[ @"Planning", @"Altitude", @"Wind Triangle",
							 @"Magnetic Variation", @"Time/Distance/Speed",
							 @"Fuel Consumption", @"Conversions" ];

		self.itemArray =
			@[
				// Planning
				@[	[[CMPlanCalculation alloc] init],
					[[CMManeuveringSpeed alloc] init] ],

				// Altitude
				@[	[[CMPDAltCalculation alloc] init],
					[[CMTrueAirspeedCalculation alloc] init],
					[[CMReqCalibratedAirspeed alloc] init],
					[[CMCloudBaseCalculator alloc] init],
				],

				// Wind Triangle
				@[	[[CMHeadingCalculation alloc] init],
					[[CMFindHeadingCalculation alloc] init],
					[[CMWindCalculator alloc] init],
					[[CMReqTrueAirspeed alloc] init],
					[[CMCrossWind alloc] init],
				],

				// Magnetic Variation
				@[	[[CMMagneticHeadingCalculation alloc] init],
					[[CMTrueHeadingCalculation alloc] init],
				],

				// Time/Distance/Speed
				@[	[[CMSpeedCalculation alloc] init],
					[[CMDistanceTraveled alloc] init],
					[[CMLegTimeCalculator alloc] init],
				],

				// Fuel Consumption
				@[	[[CMFuelRequired alloc] init],
					[[CMFuelBurnRate alloc] init],
					[[CMFuelEndurance alloc] init],
				],

				// Conversions
				@[	[[CMUnitCalculations alloc] init],
					[[CMFuelWeight alloc] init],
					[[CMFuelForWeight alloc] init],
				]
			];
	}
	return self;
}

+ (void)saveDefaults
{
	SaveValues();
}

+ (void)deleteDefaults
{
	DeleteValues();
}

+ (CME6BCalculationStore *)shared
{
	static CME6BCalculationStore *store;
	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		store = [[CME6BCalculationStore alloc] init];
	});
	return store;
}

- (NSInteger)numberGroups
{
	return self.groupNames.count;
}

- (NSString *)groupNameForIndex:(NSInteger)index
{
	return self.groupNames[index];
}

- (NSInteger)numberItemsInGroup:(NSInteger)group
{
	NSArray *a = self.itemArray[group];
	return [a count];
}

- (id<CME6BCalculation>)calculationAtIndex:(NSInteger)index inGroup:(NSInteger)group
{
	NSArray *a = self.itemArray[group];
	return a[index];
}

@end
