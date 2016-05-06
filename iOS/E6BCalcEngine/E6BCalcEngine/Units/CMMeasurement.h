//
//  CMMeasurement.h
//  E6B
//
//  Created by William Woody on 9/15/12.
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

#import <Foundation/Foundation.h>

/*
 *	Standard units. This is probably over-engineered.
 */

#define DISTANCE_FEET			0
#define DISTANCE_STMILES		1
#define DISTANCE_NMILES			2
#define DISTANCE_METERS			3
#define DISTANCE_KILOMETERS		4

#define TIME_TIME				0

#define SPEED_KNOTS				0
#define SPEED_MPH				1
#define SPEED_KPH				2		/* Kilometers/hour */
#define SPEED_FPS				3
#define SPEED_MS				4

#define WEIGHT_LBS				0
#define WEIGHT_KILOGRAMS		1
#define WEIGHT_STONE			2

#define TEMP_FAHRENHEIT			0
#define TEMP_CELSIUS			1
#define TEMP_KELVIN				2

#define VOLUME_GALLONS			0
#define VOLUME_LITERS			1
#define VOLUME_IMPGALLONS		2
#define VOLUME_QUARTS			3

#define VOLBURN_GALHR			0
#define VOLBURN_LITERSHR		1
#define VOLBURN_IMPGALLONSHR	2

#define PRESSURE_INHG			0
#define PRESSURE_KPA			1
#define PRESSURE_MILLIBARS		2

#define LENGTH_INCHES			0
#define LENGTH_CENTIMETERS		1

#define MOMENT_POUNDINCH		0
#define MOMENT_KGCM				1

/*
 *	Measurement interface
 */

@protocol CMMeasurement <NSObject>

- (NSArray *)measurements;			/* Measurement names */
- (NSArray *)abbrMeasure;			/* Measurement names */

- (int)standardUnit;				/* Standard unit index */
- (double)toStandardUnit:(double)value withUnit:(int)index;
- (double)fromStandardUnit:(double)value withUnit:(int)index;

@end


extern id<CMMeasurement> GDistance;
extern id<CMMeasurement> GTime;
extern id<CMMeasurement> GSpeed;
extern id<CMMeasurement> GWeight;
extern id<CMMeasurement> GTemperature;
extern id<CMMeasurement> GVolume;
extern id<CMMeasurement> GVolumeBurn;
extern id<CMMeasurement> GPressure;
extern id<CMMeasurement> GLength;
extern id<CMMeasurement> GMoment;

extern void E6BSetupStandardUnits();