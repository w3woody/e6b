/*  E6BApplication.java
 *
 *  Created on Dec 23, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.glenviewsoftware.e6b.adapters.CalculatorAdapter;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.e6b.altitude.CloudBaseCalculator;
import com.glenviewsoftware.e6b.calc.e6b.altitude.PDAltCalculation;
import com.glenviewsoftware.e6b.calc.e6b.altitude.ReqCalibratedAirspeed;
import com.glenviewsoftware.e6b.calc.e6b.altitude.TrueAirspeedCalculation;
import com.glenviewsoftware.e6b.calc.e6b.convert.OneFuelForWeight;
import com.glenviewsoftware.e6b.calc.e6b.convert.OneFuelWeight;
import com.glenviewsoftware.e6b.calc.e6b.convert.OneUnitCalculator;
import com.glenviewsoftware.e6b.calc.e6b.fuel.FuelBurnRate;
import com.glenviewsoftware.e6b.calc.e6b.fuel.FuelEndurance;
import com.glenviewsoftware.e6b.calc.e6b.fuel.FuelRequired;
import com.glenviewsoftware.e6b.calc.e6b.planning.ManeuveringSpeed;
import com.glenviewsoftware.e6b.calc.e6b.planning.PlanCalculation;
import com.glenviewsoftware.e6b.calc.e6b.tds.DistanceTraveled;
import com.glenviewsoftware.e6b.calc.e6b.tds.LegTimeCalculator;
import com.glenviewsoftware.e6b.calc.e6b.tds.SpeedCalculation;
import com.glenviewsoftware.e6b.calc.e6b.triangle.CrossWind;
import com.glenviewsoftware.e6b.calc.e6b.triangle.FindHeadingCalculation;
import com.glenviewsoftware.e6b.calc.e6b.triangle.HeadingCalculation;
import com.glenviewsoftware.e6b.calc.e6b.triangle.ReqTrueAirspeed;
import com.glenviewsoftware.e6b.calc.e6b.triangle.WindCalculator;
import com.glenviewsoftware.e6b.calc.e6b.variation.MagneticHeadingCalculation;
import com.glenviewsoftware.e6b.calc.e6b.variation.TrueHeadingCalculation;
import com.glenviewsoftware.e6b.calc.wb.AircraftDatabase;
import com.glenviewsoftware.e6b.calc.wb.WBCalculation;
import com.glenviewsoftware.e6b.units.Units;

public class E6BApplication extends Application
{
    private static E6BApplication gApplication;
    private static ArrayList<CalculatorAdapter.Row> gE6BCalculations;

    @Override
    public void onCreate()
    {
        super.onCreate();

        gApplication = this;
        
        CalcStorage.shared().loadValues();
        WBCalculation.loadWBFiles();
        AircraftDatabase.shared();  /* Trigger load of database */
    }
    
    public static E6BApplication shared()
    {
        return gApplication;
    }

    public static List<CalculatorAdapter.Row> e6bCalculations()
    {
        if (gE6BCalculations == null) {
            gE6BCalculations = new ArrayList<CalculatorAdapter.Row>();

            gE6BCalculations.add(new CalculatorAdapter.Row("Planning"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new PlanCalculation()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new ManeuveringSpeed()));
            
            gE6BCalculations.add(new CalculatorAdapter.Row("Altitude"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new PDAltCalculation()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new TrueAirspeedCalculation()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new ReqCalibratedAirspeed()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new CloudBaseCalculator()));
            
            gE6BCalculations.add(new CalculatorAdapter.Row("Wind Triangle"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new HeadingCalculation()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new FindHeadingCalculation()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new WindCalculator()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new ReqTrueAirspeed()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new CrossWind()));
            
            gE6BCalculations.add(new CalculatorAdapter.Row("Magnetic Variation"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new MagneticHeadingCalculation()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new TrueHeadingCalculation()));
            
            gE6BCalculations.add(new CalculatorAdapter.Row("Time/Speed/Distance"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new SpeedCalculation()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new DistanceTraveled()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new LegTimeCalculator()));
            
            gE6BCalculations.add(new CalculatorAdapter.Row("Fuel Consumption"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new FuelRequired()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new FuelBurnRate()));
            gE6BCalculations.add(new CalculatorAdapter.Row(new FuelEndurance()));
            
            gE6BCalculations.add(new CalculatorAdapter.Row("Conversions"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneUnitCalculator("Distance","Convert between units of distance",Units.distance)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneUnitCalculator("Speed","Convert between units of speed",Units.speed)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneUnitCalculator("Weight","Convert between units of weight",Units.weight)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneUnitCalculator("Temperature","Convert between units of temperature",Units.temperature)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneUnitCalculator("Volume","Convert between units of volume",Units.volume)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneUnitCalculator("Pressure","Convert between units of pressure",Units.pressure)));

            gE6BCalculations.add(new CalculatorAdapter.Row("Fuel Weight"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelWeight("Aviation Fuel","Find the weight of fuel for a given volume",6.0)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelWeight("JP-4 Fuel","Find the weight of fuel for a given volume",6.6)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelWeight("Kerosene Fuel","Find the weight of fuel for a given volume",7.0)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelWeight("Oil","Find the weight of oil for a given volume",7.5)));

            gE6BCalculations.add(new CalculatorAdapter.Row("Fuel for Weight"));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelForWeight("Aviation Fuel","Find the amount of fuel for a given weight",6.0)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelForWeight("JP-4 Fuel","Find the amount of fuel for a given weight",6.6)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelForWeight("Kerosene Fuel","Find the amount of fuel for a given weight",7.0)));
            gE6BCalculations.add(new CalculatorAdapter.Row(new OneFuelForWeight("Oil","Find the amount of oil for a given weight",7.5)));
        }
        return gE6BCalculations;
    }
}


