//
//  CME6BDataStore.m
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

#import "CME6BDataStore.h"

/************************************************************************/
/*																		*/
/*	Globals																*/
/*																		*/
/************************************************************************/

Value	GIndicatedAltitude;
Value	GBarometerSetting;
Value	GOutsideTemperature;

Value	GPressureAltitude;

Value	GCalibratedAirSpeed;
Value	GTrueAirSpeed;

Value	GGroundTemperature;
Value	GDewPointTemperature;
Value	GFieldElevation;

Value	GWindDirection;
Value	GWindSpeed;
Value	GAirplaneCourse;
Value	GGroundCourse;
Value	GGroundSpeed;
Value	GRunwayNumber;

Value	GElapsedTime;
Value	GElapsedDistance;
Value	GCurrentSpeed;
Value	GCurrentBurn;
Value	GCurrentVolume;

Value	GMagVariation;

Value	GMaxWeight;
Value	GCurrentWeight;
Value	GManuveurWeight;


uint32_t GDensityAltitude;
uint32_t GCloudBase;
uint32_t GCrosswind;
uint32_t GHeadwind;

uint32_t GCurManuveurSpeed;


/************************************************************************/
/*																		*/
/*	Save/Load															*/
/*																		*/
/************************************************************************/

static void WriteValue(FILE *f, Value v)
{
	fwrite(&v.unit,sizeof(uint32_t),1,f);
	fwrite(&v.value,sizeof(double),1,f);
}

static Value ReadValue(FILE *f)
{
	Value v;

	fread(&v.unit, sizeof(uint32_t), 1, f);
	fread(&v.value, sizeof(double), 1, f);
	
	return v;
}

void SaveValues()
{
	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSFileManager *fm = [NSFileManager defaultManager];
	[fm createDirectoryAtPath:rootPath withIntermediateDirectories:YES attributes:nil error:nil];
	NSString *plistPath = [rootPath stringByAppendingPathComponent:@"values.data"];

	FILE *f = fopen([plistPath UTF8String],"wb");
	if (f) {
		uint32_t version = 2;
		fwrite(&version, sizeof(version), 1, f);
		
		fwrite(&GDensityAltitude,1,sizeof(GDensityAltitude),f);
		fwrite(&GCloudBase,1,sizeof(GCloudBase),f);
		fwrite(&GCrosswind,1,sizeof(GCrosswind),f);
		fwrite(&GHeadwind,1,sizeof(GHeadwind),f);
		
		WriteValue(f,GIndicatedAltitude);
		WriteValue(f,GBarometerSetting);
		WriteValue(f,GOutsideTemperature);

		WriteValue(f,GPressureAltitude);

		WriteValue(f,GCalibratedAirSpeed);
		WriteValue(f,GTrueAirSpeed);

		WriteValue(f,GGroundTemperature);
		WriteValue(f,GDewPointTemperature);
		WriteValue(f,GFieldElevation);

		WriteValue(f,GWindDirection);
		WriteValue(f,GWindSpeed);
		WriteValue(f,GAirplaneCourse);
		WriteValue(f,GGroundCourse);
		WriteValue(f,GGroundSpeed);
		WriteValue(f,GRunwayNumber);

		WriteValue(f,GElapsedTime);
		WriteValue(f,GElapsedDistance);
		WriteValue(f,GCurrentSpeed);
		WriteValue(f,GCurrentBurn);
		WriteValue(f,GCurrentVolume);
		
		WriteValue(f,GMagVariation);

		WriteValue(f,GMaxWeight);
		WriteValue(f,GCurrentWeight);
		WriteValue(f,GManuveurWeight);
		
		fwrite(&GCurManuveurSpeed,1,sizeof(GCurManuveurSpeed),f);
		
		fclose(f);
	}
}

void DeleteValues()
{
	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSString *plistPath = [rootPath stringByAppendingPathComponent:@"values.data"];
	
	unlink([plistPath UTF8String]);
}

void LoadValues()
{
	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSString *plistPath = [rootPath stringByAppendingPathComponent:@"values.data"];
	
	/*
	 *	Set to defaults
	 */
	
	GIndicatedAltitude.unit = DISTANCE_FEET;
	GBarometerSetting.unit = PRESSURE_INHG;
	GOutsideTemperature.unit = TEMP_CELSIUS;
	GPressureAltitude.unit = DISTANCE_FEET;
	GCalibratedAirSpeed.unit = SPEED_KNOTS;
	GTrueAirSpeed.unit = SPEED_KNOTS;
	GGroundTemperature.unit = TEMP_CELSIUS;
	GDewPointTemperature.unit = TEMP_CELSIUS;
	GFieldElevation.unit = DISTANCE_FEET;
	GWindSpeed.unit = SPEED_KNOTS;
	GGroundSpeed.unit = SPEED_KNOTS;
	GElapsedTime.unit = TIME_TIME;
	GElapsedDistance.unit = DISTANCE_NMILES;
	GCurrentSpeed.unit = SPEED_KNOTS;
	GCurrentBurn.unit = VOLBURN_GALHR;
	GCurrentVolume.unit = VOLUME_GALLONS;
	
	GDensityAltitude = DISTANCE_FEET;
	GCloudBase = DISTANCE_FEET;
	GCrosswind = SPEED_KNOTS;
	GHeadwind = SPEED_KNOTS;
	

	FILE *f = fopen([plistPath UTF8String],"rb");
	if (f) {
		uint32_t version;
		fread(&version, sizeof(version), 1, f);
		
		if (version >= 1) {
			fread(&GDensityAltitude,1,sizeof(GDensityAltitude),f);
			fread(&GCloudBase,1,sizeof(GCloudBase),f);
			fread(&GCrosswind,1,sizeof(GCrosswind),f);
			fread(&GHeadwind,1,sizeof(GHeadwind),f);

			GIndicatedAltitude = ReadValue(f);
			GBarometerSetting = ReadValue(f);
			GOutsideTemperature = ReadValue(f);

			GPressureAltitude = ReadValue(f);

			GCalibratedAirSpeed = ReadValue(f);
			GTrueAirSpeed = ReadValue(f);

			GGroundTemperature = ReadValue(f);
			GDewPointTemperature = ReadValue(f);
			GFieldElevation = ReadValue(f);

			GWindDirection = ReadValue(f);
			GWindSpeed = ReadValue(f);
			GAirplaneCourse = ReadValue(f);
			GGroundCourse = ReadValue(f);
			GGroundSpeed = ReadValue(f);
			GRunwayNumber = ReadValue(f);

			GElapsedTime = ReadValue(f);
			GElapsedDistance = ReadValue(f);
			GCurrentSpeed = ReadValue(f);
			GCurrentBurn = ReadValue(f);
			GCurrentVolume = ReadValue(f);
		}
		
		if (version >= 2) {
			GMagVariation = ReadValue(f);
		}
		
		if (version >= 3) {
			GMaxWeight = ReadValue(f);
			GCurrentWeight = ReadValue(f);
			GManuveurWeight = ReadValue(f);
			
			fread(&GCurManuveurSpeed,1,sizeof(GCurManuveurSpeed),f);
		}
		
		fclose(f);
	}
}
