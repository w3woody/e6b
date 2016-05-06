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

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.glenviewsoftware.e6b.adapters.CalculatorAdapter;
import com.glenviewsoftware.e6b.adapters.WBListAdapter;
import com.glenviewsoftware.e6b.calc.CalcDelegate;
import com.glenviewsoftware.e6b.calc.CalcManager;
import com.glenviewsoftware.e6b.calc.CalcStorage;
import com.glenviewsoftware.e6b.calc.Calculation;
import com.glenviewsoftware.e6b.calc.wb.AircraftDatabase;
import com.glenviewsoftware.e6b.calc.wb.WBCalculation;
import com.glenviewsoftware.e6b.utils.AlertUtil;
import com.glenviewsoftware.e6b.view.CalculatorInputView;
import com.glenviewsoftware.e6b.view.ContentLayout;
import com.glenviewsoftware.e6b.view.SelectorView;

public class MainActivity extends Activity
{
    private SelectorView fSelectorView;
    private ViewSwitcher fSwitcher;
    private View fHelpButton;
    private View fAddButton;
    
    private View fWBFrame;
    
    private CalculatorAdapter fCalcListAdapter;
    private ListView fE6BListView;
    private ContentLayout fContents;
    
    private ListView fWBListView;
    private WBListAdapter fWBContents;
    
    private static boolean gE6BGroup;
    private static int gE6BSelected;

    /*
     * Drawer open/close logic
     */
    private boolean fDrawerLogic;
    private boolean fDrawerOpen;
    private boolean fInAnimation;
    private View fE6BDrawer;
    private View fE6BView;
    private Calculation fCurCalculation;
    private CalcManager fCurCalcManager;
    private TextView fTitle;
    private View fEditorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        /*
         * Display the startup disclamer
         */
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        boolean hasDisplayed = prefs.getBoolean("hasWarned", false);
        if (!hasDisplayed) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("hasWarned", true);
            edit.commit();
            
            AlertUtil.message(this, "Warning", "While we have done everything we can to " +
            		"assure the calculations performed by Glenview Software's E6B are " +
            		"correct, we do not guarantee the values. As PIC it is up to you " +
            		"to verify the values are correct.\n\nWeight and Balance limits for " +
            		"the aircraft in Glenview Software's E6B were obtained from various " +
            		"Internet sources; please double-check against your aircraft's own " +
            		"weight and balance specifications before using the results.");
        }
        
        /* Get width to determine orientation flag */
        int or = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dpWidth = dm.widthPixels / dm.xdpi;
        if (dpWidth > 3.5) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                or = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
            } else {
                or = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
            }
        }
        setRequestedOrientation(or);
        
        /* Set up the list views */
        fSelectorView = (SelectorView)findViewById(R.id.selector);
        fSelectorView.setCallback(new SelectorView.Callback() {
            @Override
            public int numberItems()
            {
                return 2;
            }

            @Override
            public String itemLabel(int index)
            {
                switch (index) {
                    case 0:
                    default:    return "E6B";
                    case 1:     return "W&B";
                }
            }

            @Override
            public void doSelect(int index)
            {
                doSelectList(index);
            }
        });
        
        fSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        fSwitcher.setDisplayedChild(0);
        fWBFrame = findViewById(R.id.wb);
        
        /* Aircraft editor */
        fEditorButton = findViewById(R.id.editor);
        fEditorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                doEditAircraft();
            }
        });
        
        /* Add/help */
        fAddButton = findViewById(R.id.add);
        fAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                doAddWBCalculation();
            }
        });
        
        /* W&B list */
        fWBListView = (ListView)findViewById(R.id.wblist);
        fWBContents = new WBListAdapter(this);
        fWBContents.setCallback(new WBListAdapter.Callback() {
            @Override
            public void doDelete(int index)
            {
                deleteCalculation(index);
            }
            
            @Override
            public void doClick(int index)
            {
                /* Change selection and open calculation */
                openCalculation(true,index);
            }
        });
        fWBListView.setAdapter(fWBContents);
        
        fWBListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int item, long arg3)
            {
                /* Change selection and open calculation */
                openCalculation(true,item);
            }
        });
        
        fWBContents.setData(WBCalculation.getWBValues());
        
        /* Help */
        fHelpButton = findViewById(R.id.help);
        fHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });
        fE6BListView = (ListView)findViewById(R.id.list);
        
        fContents = (ContentLayout)findViewById(R.id.contents);
        if (fContents != null) {
            fContents.setOnDidLayout(new ContentLayout.OnDidLayout() {
                @Override
                public void didLayout(ContentLayout l)
                {
                    if (fCurCalcManager != null) {
                        fCurCalcManager.layoutCalcViews(l);
                        l.requestLayout();
                    }
                }
            });
        }
        
        fE6BView = findViewById(R.id.e6b);
        fE6BDrawer = findViewById(R.id.popupBar);
        fDrawerLogic = (fE6BDrawer != null);
        fDrawerOpen = false;
        
        /*
         * Drawer logic
         */
        if (fDrawerLogic) {
            fE6BDrawer.setVisibility(View.GONE);
            fE6BView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    openCloseDrawer();
                }
            });
        }
        
        /*
         * Set up calculations
         */
        
        fTitle = (TextView)findViewById(R.id.title);
        
        fCalcListAdapter = new CalculatorAdapter(this);
        fE6BListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        fE6BListView.setAdapter(fCalcListAdapter);
        
        fE6BListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int item, long itemId)
            {
                /* Change selection and open calculation */
                openCalculation(false,item);
            }
        });
        
        fCalcListAdapter.setData(E6BApplication.e6bCalculations());
        
        /*
         * Select the first item in the list
         */
        
        startupSelection();
    }
    
    /**
     * Add an aircraft
     */

    protected void doAddWBCalculation()
    {
        int index = WBCalculation.createNewWBFile();
        fWBContents.setData(WBCalculation.getWBValues());
        openCalculation(true,index);
    }

    /**
     * Start the aircraft editor, if we can
     */
    protected void doEditAircraft()
    {
        if (!AircraftDatabase.shared().canEditAircraft()) {
            AlertUtil.message(this, "Unable to edit", "Unable to edit aircraft; unable to access external storage to store aircraft data");
            return;
        }
        
        Intent intent = new Intent(this,AircraftActivity.class);
        startActivity(intent);
    }

    /**
     * Return true if the weight/balance list is frontmost, false if E6B is frontmost
     * @return
     */
    private boolean isWBList()
    {
        if (fWBFrame == fSwitcher.getCurrentView()) return true;
        return false;
    }
    
    /**
     * Determine which is selected and switch
     * @param index
     */
    private void doSelectList(int index)
    {
        if (index == 0) {
            if (isWBList()) {
                fSwitcher.showNext();
            }
        } else {
            if (!isWBList()) {
                fSwitcher.showNext();
            }
        }
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        
        /* Reload W&B table contents */
        fWBContents.setData(WBCalculation.getWBValues());
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        
        /* Make sure E6B values are saved */
        CalcStorage.shared().saveValues();
        WBCalculation.saveWBFiles();
    }

    /**
     * internal routine opens/closes drawer
     */
    private void openCloseDrawer()
    {
        if (fInAnimation) return;
        
        fInAnimation = true;
        if (fDrawerOpen) {
            final TranslateAnimation t = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF,-1,
                    Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF,0);
            t.setDuration(250);
            t.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }
                
                @Override
                public void onAnimationRepeat(Animation animation)
                {
                }
                
                @Override
                public void onAnimationEnd(Animation animation)
                {
                    fE6BDrawer.setVisibility(View.GONE);
                    fDrawerOpen = false;
                    fInAnimation = false;
                }
            });
            fE6BDrawer.startAnimation(t);
            
        } else {
            final TranslateAnimation t = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,-1,
                    Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF,0);
            t.setDuration(250);
            t.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }
                
                @Override
                public void onAnimationRepeat(Animation animation)
                {
                }
                
                @Override
                public void onAnimationEnd(Animation animation)
                {
                    fDrawerOpen = true;
                    fInAnimation = false;
                }
            });
            fE6BDrawer.setVisibility(View.VISIBLE);
            fE6BDrawer.startAnimation(t);
        }
    }
    
    /**
     * Force close the drawer.
     */
    private void closeDrawer()
    {
        if (fInAnimation) {
            fDrawerOpen = false;
            fE6BDrawer.getAnimation().cancel();
            fE6BDrawer.setVisibility(View.GONE);
        } else  if (fDrawerOpen) {
            openCloseDrawer();
        }
    }
    
    /**
     * Start up selection; figure out the selected item and display it if needed
     */
    private void startupSelection()
    {
        if (!gE6BGroup && (gE6BSelected == 0)) {
            int i,len = fCalcListAdapter.getCount();
            for (i = 0; i < len; ++i) {
                CalculatorAdapter.Row r = fCalcListAdapter.getItem(i);
                if (!r.isSeparator()) {
                    /*
                     * Select this row
                     */
                    
                    gE6BSelected = i;
                    break;
                }
            }
        }
        
        if (gE6BGroup) {
            fWBListView.setSelection(gE6BSelected);
            fSelectorView.setSelection(1);
            doSelectList(1);
        } else {
            fE6BListView.setSelection(gE6BSelected);
            doSelectList(0);
        }
        if (fContents != null) {
            // Handle reloading and resize only after the resize has happened.
            fContents.post(new Runnable() {
                @Override
                public void run()
                {
                    openCalculation(gE6BGroup,gE6BSelected);
                }
            });
        }
    }
    
    /**
     * Open the specified calculation. Launch an intent if on a phone, or setup the
     * calculation if not.
     * @param wb
     * @param index
     */
    private void openCalculation(boolean wb, int index)
    {
        fE6BListView.setItemChecked(index, true);
        gE6BSelected = index;
        gE6BGroup = wb;

        if (fContents != null) {
            /*
             * Current calculation update in same view
             */
            if (wb) {
                fCurCalculation = fWBContents.getItem(index);
            } else {
                CalculatorAdapter.Row r = fCalcListAdapter.getItem(index);
                fCurCalculation = r.getCalculation();
            }
            fCurCalcManager = fCurCalculation.getCalculationManager();
            
            /*
             * Set up the calculation manager
             */
            fCurCalcManager.setDelegate(new CalcDelegate() {
                @Override
                public void updateCalcName(String name)
                {
                    /* Handle when a calc is renamed--when a W&B is renamed */
                    fTitle.setText(name);
                    fWBContents.reloadNamed();
                }

                @Override
                public void updateLayout()
                {
                    fCurCalcManager.layoutCalcViews(fContents);
                }
            });
            fCurCalcManager.setInputCallback(new CalculatorInputView.InputViewCallback() {
                @Override
                public void viewDidClear(CalculatorInputView v)
                {
                    fCurCalcManager.updateCalculation();
                }

                @Override
                public void viewDidUpdate(CalculatorInputView v)
                {
                    fCurCalcManager.updateCalculation();
                }
            });
            
            fCurCalcManager.constructCalcViews(fContents);
            if (fContents.getWidth() > 0) {
                fCurCalcManager.layoutCalcViews(fContents);
                fContents.requestLayout();
            }
            
            fTitle.setText(fCurCalculation.getCalculationName());
        } else {
            /*
             * Calculation is in subview
             */
            
            Intent intent = new Intent(this,CalculatorActivity.class);
            intent.putExtra("type", wb);
            intent.putExtra("calc", index);
            startActivity(intent);
        }
        
        if (fDrawerLogic) {
            closeDrawer();
        }
    }
    
    /**
     * Delete W&B calculation
     * @param wbIndex
     */
    private void deleteCalculation(final int wbIndex)
    {
        final List<WBCalculation> l = WBCalculation.getWBValues();
        WBCalculation c = l.get(wbIndex);
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        
        b.setTitle("Delete?");
        b.setMessage("Are you sure you wish to delete " + c.getCalculationName() + "? This operation cannot be undone.");
        b.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                /*
                 * Delete
                 */
                l.remove(wbIndex);
                if (l.size() == 0) {
                    l.add(new WBCalculation());
                }
                WBCalculation.saveWBFiles();
                
                fWBContents.setData(l);
                if (fContents != null) {
                    openCalculation(true,0);
                }
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        b.setCancelable(true);
        b.show();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // close menu if open and drawer logic depends on it.
            if (fDrawerLogic) {
                if (fDrawerOpen || fInAnimation) {
                    closeDrawer();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
