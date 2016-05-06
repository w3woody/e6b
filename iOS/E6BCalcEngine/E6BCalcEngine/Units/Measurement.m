//
//  Measurement.m
//  E6B
//
//  Created by William Woody on 9/15/12.
//  Copyright (c) 2012 William Woody. All rights reserved.
//
//	This may be over-engineered.

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

#import "CMMeasurement.h"

#import "CMDistance.h"
#import "CMTime.h"
#import "CMSpeed.h"
#import "CMWeight.h"
#import "CMTemperature.h"
#import "CMVolume.h"
#import "CMPressure.h"
#import "CMVolumeBurn.h"
#import "CMLength.h"
#import "CMMoment.h"

id<CMMeasurement> GDistance;
id<CMMeasurement> GTime;
id<CMMeasurement> GSpeed;
id<CMMeasurement> GWeight;
id<CMMeasurement> GTemperature;
id<CMMeasurement> GVolume;
id<CMMeasurement> GPressure;
id<CMMeasurement> GVolumeBurn;
id<CMMeasurement> GLength;
id<CMMeasurement> GMoment;


/*	SetupStandardUnits
 *
 *		Create my standard unit objects
 */

void E6BSetupStandardUnits()
{
	GDistance = [[CMDistance alloc] init];
	GTime = [[CMTime alloc] init];
	GSpeed = [[CMSpeed alloc] init];
	GWeight = [[CMWeight alloc] init];
	GTemperature = [[CMTemperature alloc] init];
	GVolume = [[CMVolume alloc] init];
	GVolumeBurn = [[CMVolumeBurn alloc] init];
	GPressure = [[CMPressure alloc] init];
	GLength = [[CMLength alloc] init];
	GMoment = [[CMMoment alloc] init];
}
