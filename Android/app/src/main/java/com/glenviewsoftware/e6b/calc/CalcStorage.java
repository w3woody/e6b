/*  CalcStorage.java
 *
 *  Created on Dec 24, 2012 by William Edward Woody
 */
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

package com.glenviewsoftware.e6b.calc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.glenviewsoftware.e6b.E6BApplication;
import com.glenviewsoftware.e6b.units.Distance;
import com.glenviewsoftware.e6b.units.Pressure;
import com.glenviewsoftware.e6b.units.Speed;
import com.glenviewsoftware.e6b.units.Temperature;
import com.glenviewsoftware.e6b.units.Time;
import com.glenviewsoftware.e6b.units.Volume;
import com.glenviewsoftware.e6b.units.VolumeBurn;
import com.glenviewsoftware.e6b.units.Weight;

public class CalcStorage
{
    private static CalcStorage gData = new CalcStorage();

    /*
     * Public values
     */
    
    public Value   indicatedAltitude;
    public Value   barometerSetting;
    public Value   outsideTemperature;

    public Value   pressureAltitude;

    public Value   calibratedAirSpeed;
    public Value   trueAirSpeed;

    public Value   groundTemperature;
    public Value   dewPointTemperature;
    public Value   fieldElevation;

    public Value   windDirection;
    public Value   windSpeed;
    public Value   airplaneCourse;
    public Value   groundCourse;
    public Value   groundSpeed;
    public Value   runwayNumber;

    public Value   elapsedTime;
    public Value   elapsedDistance;
    public Value   currentSpeed;
    public Value   currentBurn;
    public Value   currentVolume;

    public Value   magVariation;

    public Value   maxWeight;
    public Value   currentWeight;
    public Value   manuveurWeight;


    public int     densityAltitude;
    public int     cloudBase;
    public int     crosswind;
    public int     headwind;

    public int     curManuveurWeight;

    private CalcStorage()
    {
        indicatedAltitude = new Value(Distance.DISTANCE_FEET);
        barometerSetting = new Value(Pressure.PRESSURE_INHG);
        outsideTemperature = new Value(Temperature.TEMP_CELSIUS);
        pressureAltitude = new Value(Distance.DISTANCE_FEET);
        calibratedAirSpeed = new Value(Speed.SPEED_KNOTS);
        
        trueAirSpeed = new Value(Speed.SPEED_KNOTS);
        groundTemperature = new Value(Temperature.TEMP_CELSIUS);
        dewPointTemperature = new Value(Temperature.TEMP_CELSIUS);
        fieldElevation = new Value(Distance.DISTANCE_FEET);
        windDirection = new Value(0);
        
        windSpeed = new Value(Speed.SPEED_KNOTS);
        airplaneCourse = new Value(0);
        groundCourse = new Value(0);
        groundSpeed = new Value(Speed.SPEED_KNOTS);
        runwayNumber = new Value(0);
        
        elapsedTime = new Value(Time.TIME_TIME);
        elapsedDistance = new Value(Distance.DISTANCE_NMILES);
        currentSpeed = new Value(Speed.SPEED_KNOTS);
        currentBurn = new Value(VolumeBurn.VOLBURN_GALHR);
        currentVolume = new Value(Volume.VOLUME_GALLONS);
        
        magVariation = new Value(0);
        maxWeight = new Value(Weight.WEIGHT_LBS);
        currentWeight = new Value(Weight.WEIGHT_LBS);
        manuveurWeight = new Value(Weight.WEIGHT_LBS);
        
        densityAltitude = Distance.DISTANCE_FEET;
        cloudBase = Distance.DISTANCE_FEET;
        crosswind = Speed.SPEED_KNOTS;
        headwind = Speed.SPEED_KNOTS;
        curManuveurWeight = Weight.WEIGHT_LBS;
    }
    
    public static CalcStorage shared()
    {
        return gData;
    }
    
    public void loadValues()
    {
        try {
            FileInputStream out = E6BApplication.shared().openFileInput("e6b.data");
            ObjectInputStream ois = new ObjectInputStream(out);
            
            int ver = ois.readInt();
            if (ver >= 1) {
                indicatedAltitude = (Value)ois.readObject();
                barometerSetting = (Value)ois.readObject();
                outsideTemperature = (Value)ois.readObject();
                pressureAltitude = (Value)ois.readObject();
                calibratedAirSpeed = (Value)ois.readObject();

                trueAirSpeed = (Value)ois.readObject();
                groundTemperature = (Value)ois.readObject();
                dewPointTemperature = (Value)ois.readObject();
                fieldElevation = (Value)ois.readObject();
                windDirection = (Value)ois.readObject();

                windSpeed = (Value)ois.readObject();
                airplaneCourse = (Value)ois.readObject();
                groundCourse = (Value)ois.readObject();
                groundSpeed = (Value)ois.readObject();
                runwayNumber = (Value)ois.readObject();

                elapsedTime = (Value)ois.readObject();
                elapsedDistance = (Value)ois.readObject();
                currentSpeed = (Value)ois.readObject();
                currentBurn = (Value)ois.readObject();
                currentVolume = (Value)ois.readObject();

                magVariation = (Value)ois.readObject();
                maxWeight = (Value)ois.readObject();
                currentWeight = (Value)ois.readObject();
                manuveurWeight = (Value)ois.readObject();
                
                densityAltitude = ois.readInt();
                cloudBase = ois.readInt();
                crosswind = ois.readInt();
                headwind = ois.readInt();
                curManuveurWeight = ois.readInt();
            }
            
            ois.close();
        }
        catch (IOException e) {
        }
        catch (ClassNotFoundException e) {
        }
    }
    
    public void saveValues()
    {
        try {
            FileOutputStream out = E6BApplication.shared().openFileOutput("e6b.data", 0);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            
            oos.writeInt(1);        /* Version number */
            
            oos.writeObject(indicatedAltitude);
            oos.writeObject(barometerSetting);
            oos.writeObject(outsideTemperature);
            oos.writeObject(pressureAltitude);
            oos.writeObject(calibratedAirSpeed);
            
            oos.writeObject(trueAirSpeed);
            oos.writeObject(groundTemperature);
            oos.writeObject(dewPointTemperature);
            oos.writeObject(fieldElevation);
            oos.writeObject(windDirection);
            
            oos.writeObject(windSpeed);
            oos.writeObject(airplaneCourse);
            oos.writeObject(groundCourse);
            oos.writeObject(groundSpeed);
            oos.writeObject(runwayNumber);
            
            oos.writeObject(elapsedTime);
            oos.writeObject(elapsedDistance);
            oos.writeObject(currentSpeed);
            oos.writeObject(currentBurn);
            oos.writeObject(currentVolume);
            
            oos.writeObject(magVariation);
            oos.writeObject(maxWeight);
            oos.writeObject(currentWeight);
            oos.writeObject(manuveurWeight);
            
            oos.writeInt(densityAltitude);
            oos.writeInt(cloudBase);
            oos.writeInt(crosswind);
            oos.writeInt(headwind);
            oos.writeInt(curManuveurWeight);
            
            oos.close();
        }
        catch (IOException e) {
        }
    }
}


