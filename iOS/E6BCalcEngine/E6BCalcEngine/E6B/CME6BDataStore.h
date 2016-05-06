//
//  CME6BDataStore.h
//  E6BCalcEngine
//
//  Created by William Woody on 10/21/14.
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

#import <Foundation/Foundation.h>

#import "CMMath.h"
#import "CMValue.h"
#import "CMMeasurement.h"

/************************************************************************/
/*																		*/
/*	Globals																*/
/*																		*/
/************************************************************************/
/*
 *	Global values carry over across calculations. These are inputs only
 */

extern Value	GIndicatedAltitude;
extern Value	GBarometerSetting;
extern Value	GOutsideTemperature;

extern Value	GPressureAltitude;

extern Value	GCalibratedAirSpeed;
extern Value	GTrueAirSpeed;

extern Value	GGroundTemperature;
extern Value	GDewPointTemperature;
extern Value	GFieldElevation;

extern Value	GWindDirection;
extern Value	GWindSpeed;
extern Value	GAirplaneCourse;	// should be GAirplaneHeading
extern Value	GGroundCourse;
extern Value	GGroundSpeed;
extern Value	GRunwayNumber;

extern Value	GElapsedTime;
extern Value	GElapsedDistance;
extern Value	GCurrentSpeed;
extern Value	GCurrentBurn;
extern Value	GCurrentVolume;

extern Value	GMagVariation;

extern Value	GMaxWeight;
extern Value	GCurrentWeight;
extern Value	GManuveurWeight;

/*
 *	Global default output units
 */

extern uint32_t	GDensityAltitude;
extern uint32_t GCloudBase;
extern uint32_t GCrosswind;
extern uint32_t GHeadwind;

extern uint32_t GCurManuveurSpeed;

extern void SaveValues();
extern void LoadValues();
extern void DeleteValues();
