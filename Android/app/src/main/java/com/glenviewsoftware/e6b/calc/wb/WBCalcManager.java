/*  WBCalcManager.java
 *
 *  Created on Dec 31, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.calc.wb;

import java.util.ArrayList;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glenviewsoftware.e6b.R;
import com.glenviewsoftware.e6b.calc.CalcDelegate;
import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.Value;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftStation;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBData;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBFRow;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBFuelTank;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBRow;
import com.glenviewsoftware.e6b.notifications.NotificationCenter;
import com.glenviewsoftware.e6b.units.Length;
import com.glenviewsoftware.e6b.units.Moment;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.units.Volume;
import com.glenviewsoftware.e6b.units.Weight;
import com.glenviewsoftware.e6b.view.AircraftPickerView;
import com.glenviewsoftware.e6b.view.CalculatorEditText;
import com.glenviewsoftware.e6b.view.CalculatorInputView;
import com.glenviewsoftware.e6b.view.CalculatorInputView.InputViewCallback;
import com.glenviewsoftware.e6b.view.DescriptionView;
import com.glenviewsoftware.e6b.view.WBGraph;

public class WBCalcManager extends CalcManager
{
    private static class CalcRow
    {
        private CalculatorInputView weight;
        private CalculatorInputView arm;
        private CalculatorInputView moment;
        private View delButton;
        
        private CalcRow(CalculatorInputView w, CalculatorInputView a, CalculatorInputView m, View d)
        {
            weight = w;
            arm = a;
            moment = m;
            delButton = d;
        }
        
        private CalcRow(CalculatorInputView w, CalculatorInputView a, View d)
        {
            weight = w;
            arm = a;
            delButton = d;
        }
    }
    
    private static class FuelRow
    {
        private TextView label;
        private CalculatorInputView weight;
        private CalculatorInputView arm;
        private CalculatorInputView moment;
        
        private FuelRow(TextView l, CalculatorInputView w, CalculatorInputView a, CalculatorInputView m)
        {
            label = l;
            weight = w;
            arm = a;
            moment = m;
        }
        
        private FuelRow(TextView l, CalculatorInputView w, CalculatorInputView a)
        {
            label = l;
            weight = w;
            arm = a;
        }
        
        private WBFRow save(WBFRow oldState)
        {
            String name = oldState.getName();
            Value w = weight.saveStoreValue();
            Value a = arm.saveStoreValue();
            int m = oldState.getMomentUnit();
            if (moment != null) m = moment.getUnit();
            int ft = oldState.getFuelType();
            
            return new WBFRow(name,w,a,m,ft);
        }
    }

    private WBCalculation fCalculation;
    private CalcDelegate fDelegate;
    private InputViewCallback fCallback;
    
    private DescriptionView fLabel;
    private EditText fName;
    private AircraftPickerView fAircraft;
    
    private TextView fWeightLabel;
    private TextView fArmLabel;
    private TextView fMomentLabel;
    
    private TextView fAircraftLabel;
    private TextView fStationsLabel;
    
    private CalculatorInputView fAircraftWeight;
    private CalculatorInputView fAircraftArm;
    private CalculatorInputView fAircraftMoment;
    private ArrayList<FuelRow> fFuelTankData;
    private ArrayList<CalcRow> fStationsData;
    private View fInsert;
    
    private CalculatorInputView fTotalWeight;
    private CalculatorInputView fTotalArm;
    private CalculatorInputView fTotalMoment;
    
    private boolean fWideSetting;
    private WBGraph fGraph;
    
    private boolean fPicker;


    /**
     * Internal construction
     * @param calc
     */
    public WBCalcManager(WBCalculation calc)
    {
        fCalculation = calc;
    }

    @Override
    public void setDelegate(CalcDelegate del)
    {
        fDelegate = del;
    }

    @Override
    public void setInputCallback(InputViewCallback callback)
    {
        fCallback = callback;
    }

    @Override
    public void constructCalcViews(final RelativeLayout frame)
    {
        frame.removeAllViews();

        WBData data = fCalculation.getData();
        WBAircraft aircraft = fCalculation.getAircraft();
        boolean wide = isWide(frame);
        fWideSetting = wide;
        
        /*
         * Initialize stations
         */
        ArrayList<CalculatorInputView.InputFixedValues> stations = buildStations(data, aircraft);
        
        /*
         * Initialize arrays
         */
        
        fFuelTankData = new ArrayList<FuelRow>();
        fStationsData = new ArrayList<CalcRow>();

        /*
         * Add layouts (Populate with callbacks, values from data object)
         */
        
        // Create the label
        fLabel = new DescriptionView(frame.getContext());
        addView(fLabel,frame);
        fLabel.setDescription("Allows the calculation of the weight and balance of an aircraft, as well as calculating the Va speed for the given weight");

        // Create the name
        fName = new CalculatorEditText(frame.getContext());
        addView(fName,frame);
        fName.setText(fCalculation.getData().getName()); // TODO: callback
        fName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s)
            {
                String name = s.toString();
                updateName(name);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
        });
        
        // Construct the aircraft picker
        fAircraft = new AircraftPickerView(frame.getContext());
        addView(fAircraft,frame);
        fAircraft.setText(fCalculation.getData().getAircraft());
        fAircraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (fPicker) return;
                fPicker = true;
                
                new AircraftPicker(frame.getContext(),new AircraftPicker.Callback() {
                    @Override
                    public void select(String name)
                    {
                        updateAircraft(name,frame);
                        fPicker = false;
                    }

                    @Override
                    public void cancel()
                    {
                        fPicker = false;
                    }
                });
            }
        });
        
        // Construct the labels
        fWeightLabel = headerLabel(frame,"Weight",true);
        fArmLabel = headerLabel(frame,"Arm",true);
        if (wide) fMomentLabel = headerLabel(frame,"Moment",true);
        
        fAircraftLabel = headerLabel(frame,"Aircraft",false);
        fStationsLabel = headerLabel(frame,"Stations ",false);
        
        // Add aircraft, fuel, stations
        fAircraftWeight = calcInputView(frame,true);
        fAircraftWeight.setMeasurement(Units.weight);
        fAircraftWeight.loadStoreValue(data.getAircraftWeight());
        
        fAircraftArm = calcInputView(frame,true);
        fAircraftArm.setMeasurement(Units.length);
        fAircraftArm.loadStoreValue(data.getAircraftArm());

        if (wide) {
            fAircraftMoment = calcInputView(frame,false);
            fAircraftMoment.setMeasurement(Units.moment);
            fAircraftMoment.setUnit(data.getAircraftMomentUnit());
        }
        
        // fuel
        fuelRowsWithData(frame);
        
        // stations
        for (WBRow row: data.getList()) {
            CalcRow crow = createCalcRow(frame, wide, stations, row);
            fStationsData.add(crow);
        }
        
        // Insert
        fInsert = insDelButton(frame,false);
        fInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                insertStationRow(frame);
            }
        });
        
        // Add aircraft, fuel, stations
        fTotalWeight = calcInputResult(frame,"Total Weight");
        fTotalWeight.setMeasurement(Units.weight);
        fTotalWeight.setUnit(data.getWeightUnit());
        
        fTotalArm = calcInputResult(frame,"Total Arm");
        fTotalArm.setMeasurement(Units.length);
        fTotalArm.setUnit(data.getArmUnit());
        
        fTotalMoment = calcInputResult(frame,"Total Moment");
        fTotalMoment.setMeasurement(Units.moment);
        fTotalMoment.setUnit(data.getAircraftMomentUnit());
        
        fGraph = graph(frame);
    }
    
    /**
     * Update the name of this editor
     * @param name
     */
    private void updateName(String name)
    {
        fCalculation.getData().setName(name);
        fDelegate.updateCalcName(name);
    }

    /**
     * Insert stations
     * @param frame
     * @param wide
     * @param stations
     * @param row
     * @return
     */
    private CalcRow createCalcRow(final RelativeLayout frame, boolean wide, ArrayList<CalculatorInputView.InputFixedValues> stations, WBRow row)
    {
        CalculatorInputView w = calcInputView(frame,true);
        w.setMeasurement(Units.weight);
        w.loadStoreValue(row.getWeight());
        
        CalculatorInputView a = calcInputView(frame,true);
        a.setMeasurement(Units.length);
        a.loadStoreValue(row.getArm());
        a.setFixedValues(stations);
        
        CalculatorInputView m = null;
        if (wide) {
            m = calcInputView(frame,false);
            m.setMeasurement(Units.moment);
            m.setUnit(row.getMomentUnit());
        }
        final View del = insDelButton(frame,true);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteStationRow(del, frame);
            }
        });
        
        CalcRow crow = new CalcRow(w,a,m,del);
        return crow;
    }

    private ArrayList<CalculatorInputView.InputFixedValues> buildStations(WBData data, WBAircraft aircraft)
    {
        ArrayList<CalculatorInputView.InputFixedValues> stations;
        stations = new ArrayList<CalculatorInputView.InputFixedValues>();

        if (aircraft != null) {
            for (WBAircraftStation s: aircraft.getStation()) {
                Value val = new Value(s.getArm(),data.getArmUnit());
                CalculatorInputView.InputFixedValues v = new CalculatorInputView.InputFixedValues(val,s.getName());
                stations.add(v);
            }
        }
        
        return stations;
    }
    
    /**
     * Delete the station row
     * @param index
     * @param frame
     */
    private void deleteStationRow(View delView, RelativeLayout frame)
    {
        int index = 0;
        for (CalcRow r: fStationsData) {
            if (r.delButton == delView) break;
            ++index;
        }
        
        CalcRow row = fStationsData.get(index);
        boolean wide = isWide(frame);
        
        /* Delete this row */
        frame.removeView(row.arm);
        frame.removeView(row.weight);
        if (wide) {
            frame.removeView(row.moment);
        }
        frame.removeView(row.delButton);
        fStationsData.remove(index);
        
        /* Delete the row in the data */
        WBData data = fCalculation.getData();
        data.getList().remove(index);
        
        /* Layout and recalc */
        layoutCalcViews(frame);
        updateCalculation();
    }
    
    /**
     * Insert the station row
     * @param frame
     */
    private void insertStationRow(RelativeLayout frame)
    {
        boolean wide = isWide(frame);
        WBData data = fCalculation.getData();
        WBAircraft aircraft = fCalculation.getAircraft();
        ArrayList<CalculatorInputView.InputFixedValues> stations = buildStations(data, aircraft);

        WBRow row = new WBRow();
        data.getList().add(row);

        CalcRow crow = createCalcRow(frame, wide, stations, row);
        fStationsData.add(crow);

        /* Layout and recalc */
        updateCalculation();
        layoutCalcViews(frame);
    }
    
    private void fuelRowsWithData(RelativeLayout frame)
    {
        WBData data = fCalculation.getData();
        boolean wide = isWide(frame);
        
        /* Remove the old rows */
        for (FuelRow r: fFuelTankData) {
            frame.removeView(r.arm);
            frame.removeView(r.weight);
            frame.removeView(r.label);
            if (wide) {
                frame.removeView(r.moment);
            }
        }

        /* Reset and add the new rows */
        fFuelTankData = new ArrayList<FuelRow>();
        for (WBFRow fuel: data.getFuel()) {
            TextView v = headerLabel(frame,fuel.getName(),false);
            
            CalculatorInputView w = calcInputView(frame,true);
            if (fuel.getFuelType() == WBFuelTank.FUELTYPE_UNKNOWN) {
                w.setMeasurement(Units.weight);
            } else {
                w.setMeasurement(Units.volume);
            }
            w.loadStoreValue(fuel.getVolume());
            
            CalculatorInputView a = calcInputView(frame,true);
            a.setMeasurement(Units.length);
            a.loadStoreValue(fuel.getArm());
            
            CalculatorInputView m = null;
            if (wide) {
                m = calcInputView(frame,false);
                m.setMeasurement(Units.moment);
                m.setUnit(fuel.getMomentUnit());
            }
            FuelRow row = new FuelRow(v,w,a,m);
            fFuelTankData.add(row);
        }
    }


    
    /**
     * Update the aircraft
     * @param name
     */
    private void updateAircraft(String name, RelativeLayout frame)
    {
        fCalculation.setAircraftName(name);
        WBData data = fCalculation.getData();

        boolean wide = isWide(frame);

        // Pull weight, arm for aircraft 
        fAircraftWeight.loadStoreValue(data.getAircraftWeight());
        fAircraftArm.loadStoreValue(data.getAircraftArm());
        if (wide) {
            fAircraftMoment.setUnit(data.getAircraftMomentUnit());
        }
        
        // Redo fuel
        fuelRowsWithData(frame);
        
        // Update calculations
        updateCalculation();
        
        // Update the stations
        WBAircraft aircraft = fCalculation.getAircraft();
        ArrayList<CalculatorInputView.InputFixedValues> stations = buildStations(data, aircraft);
        for (CalcRow r: fStationsData) {
            r.arm.setFixedValues(stations);
        }
        
        // Update the aircraft label
        fAircraft.setText(name);
        
        layoutCalcViews(frame);
    }
    
    private WBGraph graph(RelativeLayout frame)
    {
        WBGraph g = new WBGraph(frame.getContext());
        addView(g,frame);
        return g;
    }
    
    private ImageView insDelButton(RelativeLayout frame, boolean delFlag)
    {
        ImageView v = new ImageView(frame.getContext());
        v.setImageResource(delFlag ? R.drawable.delete : R.drawable.insert);
        v.setPadding(8, 8, 8, 8);
        addView(v,frame);
        return v;
    }
    
    private CalculatorInputView calcInputView(RelativeLayout frame, boolean editable)
    {
        CalculatorInputView c = new CalculatorInputView(frame.getContext());
        c.setCompact(true);
        c.setCallback(fCallback);
        c.setEditable(editable);
        addView(c,frame);
        return c;
    }
    
    private CalculatorInputView calcInputResult(RelativeLayout frame, String label)
    {
        CalculatorInputView c = new CalculatorInputView(frame.getContext());
        c.setLabel(label);
        c.setCallback(fCallback);
        c.setEditable(false);
        addView(c,frame);
        return c;
    }
    
    /**
     * Helper to build header label
     * @param frame
     * @param label
     * @return
     */
    private TextView headerLabel(RelativeLayout frame, String label, boolean large)
    {
//        float scale = frame.getResources().getDisplayMetrics().density;
        TextView tv = new TextView(frame.getContext());
        tv.setText(label);
        tv.setTextSize(large ? 15 : 11);
        tv.setTextColor(0xFFFFFFFF);
        if (large) {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        addView(tv,frame);
        return tv;
    }
    

    private int layoutLabel(RelativeLayout layout)
    {
        float scale = layout.getResources().getDisplayMetrics().density;
        final int w = (int)(layout.getWidth() - 40 * scale);  /* Padding = 20 */
        int h = fLabel.getHeight(w-20);
        setLocation(fLabel,10,10,w-20,h);
        return 20 + h;
    }
    
    @Override
    public void layoutCalcViews(RelativeLayout frame)
    {
        boolean wide = isWide(frame);
        if (wide != fWideSetting) {
            /* Something got resized. Force reload */
            constructCalcViews(frame);
        }
        float scale = frame.getResources().getDisplayMetrics().density;
        
        final int w = (int)(frame.getWidth() - 40 * scale);  /* Padding = 20 */
        final int totHeight = (int)((w * 2)/3);  /* Padding = 20 */
        int ypos = layoutLabel(frame);
        
        final int h = (int)(44 * scale);
        final int toph = (int)(55 * scale);
        final int lh = (int)(scale * 16);     // label height

        if (wide) {
            // Side by side
            int xl = 10;
            int xw = (w - 30)/2;
            int xr = xl + 10 + xw;
            
            setLocation(fName,xl,ypos,xw,toph);
            setLocation(fAircraft,xr,ypos,xw,toph);
            ypos += toph;
        } else {
            int xl = 10;
            int xw = w-20;
            
            setLocation(fName,xl,ypos,xw,toph);
            ypos += toph;
            setLocation(fAircraft,xl,ypos,xw,toph);
            ypos += toph;
        }
        
        /*
         * Start laying out the rows
         */
        int xl,xm,xr,xd,xdw,xw;
        
        xdw = (int)(40 * scale);
        if (wide) {
            xw = (w - 40 - xdw)/3;
            xl = 10;
            xm = xl + 10 + xw;
            xr = xm + 10 + xw;
            xd = xr + 10 + xw;
        } else {
            xw = (w - 30 - xdw)/2;
            xl = 10;
            xm = xl + 10 + xw;
            xd = xm + 10 + xw;
            xr = 0;
        }
        
        setLocation(fWeightLabel,xl,ypos,xw,h);
        setLocation(fArmLabel,xm,ypos,xw,h);
        if (wide) {
            setLocation(fMomentLabel,xr,ypos,xw,h);
        }
        ypos += h;
        
        /* Aircraft */
        setLocation(fAircraftLabel,xl,ypos,xw,lh);
        ypos += lh;
        
        setLocation(fAircraftWeight,xl,ypos,xw,h);
        setLocation(fAircraftArm,xm,ypos,xw,h);
        if (wide) {
            setLocation(fAircraftMoment,xr,ypos,xw,h);
        }
        ypos += h;
        
        /* Fuel tanks */
        for (FuelRow r: fFuelTankData) {
            setLocation(r.label,xl,ypos,xw,lh);
            ypos += lh;
            
            setLocation(r.weight,xl,ypos,xw,h);
            setLocation(r.arm,xm,ypos,xw,h);
            if (wide) {
                setLocation(r.moment,xr,ypos,xw,h);
            }
            ypos += h;
        }
        
        /* Statiosn tank */
        setLocation(fStationsLabel,xl,ypos,xw,lh);
        ypos += lh;
        for (CalcRow r: fStationsData) {
            setLocation(r.weight,xl,ypos,xw,h);
            setLocation(r.arm,xm,ypos,xw,h);
            if (wide) {
                setLocation(r.moment,xr,ypos,xw,h);
            }
            setLocation(r.delButton,xd,ypos,xdw,h);
            ypos += h;
        }
        
        /* Insert */
        setLocation(fInsert,xd,ypos,xdw,h);
        ypos += h;
        
        /* Results */
        final int th = (int)(60 * scale);

        if (wide) {
            setLocation(fTotalWeight,xl,ypos,xw,th);
            setLocation(fTotalArm,xm,ypos,xw,th);
            setLocation(fTotalMoment,xr,ypos,xw,th);
            ypos += th;
        } else {
            setLocation(fTotalWeight,xl,ypos,w,th);
            ypos += th;
            setLocation(fTotalArm,xl,ypos,w,th);
            ypos += th;
            setLocation(fTotalMoment,xl,ypos,w,th);
            ypos += th;
        }
        
        /*
         * Graph
         */
        
        setLocation(fGraph,xl,ypos,w,totHeight);
        
        /*
         * Recalc
         */
        updateCalculation();
    }

    @Override
    public void updateCalculation()
    {
        double aweight = 0;
        double amoment = 0;
        double w,a,m;
        
        /*
         * Store the value into the data object
         */
        
        WBData data = fCalculation.getData();
        data.setAircraftWeight(fAircraftWeight.saveStoreValue());
        data.setAircraftArm(fAircraftArm.saveStoreValue());
        if (fAircraftMoment != null) {
            data.setAircraftMomentUnit(fAircraftMoment.getUnit());
        }
        
        w = fAircraftWeight.getValueAsUnit(Weight.WEIGHT_LBS);
        a = fAircraftArm.getValueAsUnit(Length.LENGTH_INCHES);
        m = w * a;
        aweight += w;
        amoment += m;
        if (fAircraftMoment != null) {
            fAircraftMoment.setValue(m, Moment.MOMENT_POUNDINCH);
        }

        ArrayList<WBGraph.Pair> array = new ArrayList<WBGraph.Pair>();
        
        int i,len = data.getFuel().size();
        int flen = fFuelTankData.size();
        for (i = 0; i < len; ++i) {
            WBFRow fdata = data.getFuel().get(i);
            if (i > flen) {
                fdata = new WBFRow();
            } else {
                FuelRow fin = fFuelTankData.get(i);
                fdata = fin.save(fdata);
                
                if (fdata.getFuelType() == WBFuelTank.FUELTYPE_UNKNOWN) {
                    w = fin.weight.getValueAsUnit(Weight.WEIGHT_LBS);
                } else {
                    w = fin.weight.getValueAsUnit(Volume.VOLUME_GALLONS) * weightForFuel(fdata.getFuelType());
                }
                a = fin.arm.getValueAsUnit(Length.LENGTH_INCHES);
                m = w * a;
                
                aweight += w;
                amoment += m;
                if (fin.moment != null) {
                    fin.moment.setValue(m,Moment.MOMENT_POUNDINCH);
                }
                
                /*
                 * Record weight/arm pair for graph
                 */
                
                w = Units.weight.toStandardUnit(w, Weight.WEIGHT_LBS);
                w = Units.weight.fromStandardUnit(w, fCalculation.getWeightUnit());
                a = fin.arm.getValueAsUnit(fCalculation.getArmUnit());
                
                array.add(new WBGraph.Pair(w, a));
            }
            data.getFuel().set(i, fdata);
        }
        
        data.getList().clear();
        for (CalcRow row: fStationsData) {
            int munit = Moment.MOMENT_POUNDINCH;
            if (row.moment != null) munit = row.moment.getUnit();
           
            WBRow wdata = new WBRow(row.weight.saveStoreValue(),row.arm.saveStoreValue(),munit);
            data.getList().add(wdata);
            
            w = row.weight.getValueAsUnit(Weight.WEIGHT_LBS);
            a = row.arm.getValueAsUnit(Length.LENGTH_INCHES);
            m = w * a;
            aweight += w;
            amoment += m;
            
            if (row.moment != null) {
                row.moment.setValue(m, Moment.MOMENT_POUNDINCH);
            }
        }
        
        double aarm;
        if (aweight == 0) {
            aarm = 0;
        } else {
            aarm = amoment / aweight;
        }
        
        fTotalWeight.setValue(aweight, Weight.WEIGHT_LBS);
        fTotalArm.setValue(aarm,Length.LENGTH_INCHES);
        fTotalMoment.setValue(amoment,Moment.MOMENT_POUNDINCH);
        
        /*
         * Set graph data
         */
        
        fGraph.setAircraft(fCalculation.getAircraft());
        fGraph.setTotalArm(fTotalArm.getValueAsUnit(fCalculation.getArmUnit()));
        fGraph.setTotalWeight(fTotalWeight.getValueAsUnit(fCalculation.getWeightUnit()));
        fGraph.setWeights(array);
        fGraph.post(new Runnable() {
            @Override
            public void run()
            {
                fGraph.reloadGraph();
            }
        });
    }

    private static double weightForFuel(int type)
    {
        switch (type) {
            case WBFuelTank.FUELTYPE_AVGAS:    return 6;
            case WBFuelTank.FUELTYPE_KEROSENE: return 7;
            case WBFuelTank.FUELTYPE_JETA:     return 6.6;
            default:                           return 1;   /* ??? */
        }
    }
}


