/*  EditorActivity.java
 *
 *  Created on Dec 28, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.editor;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.glenviewsoftware.e6b.R;
import com.glenviewsoftware.e6b.utils.AlertUtil;

/**
 * Editor activity 
 */
public class EditorActivity extends Activity
{
    public static final String RELOAD = "EditorReload";
    public static final String RETITLE = "EditorReloadTitle";
    
    private EditorDataSource fDataSource;
    private EditorData fData;
    private ListView fListView;
    private TextView fTextView;
    private EditorAdapter fListData;
    
    /**
     * Insert
     */
    private class Insert extends EditorAdapter.Insert
    {
        private int fSection;

        public Insert(int sec)
        {
            fSection = sec;
        }

        @Override
        public void doRowClick()
        {
            doInsert();
        }

        @Override
        public void doInsDel()
        {
            doInsert();
        }

        private void doInsert()
        {
            // row data
            int secLen = fDataSource.numberOfRowsInSection(EditorActivity.this, fSection, fData);
            boolean secIsArray = fDataSource.sectionIsArray(EditorActivity.this, fSection, fData);
            
            // insert data
            try {
                fDataSource.insertRow(EditorActivity.this, fSection, fData);
            }
            catch (IOException e) {
                showIOError();
                return;
            }

            // Insert row
            int type = fDataSource.rowType(EditorActivity.this, secLen, fSection, fData);
            Data rowData = new Data(fSection,secLen,type,secIsArray);
            
            fListData.insertData(rowData, this);
        }
    }
    
    /**
     * Used to store a data row
     */
    private class Data extends EditorAdapter.Row
    {
        private int fSecID;
        private int fRowID;
        private int fRowType;
        private boolean fIsList;

        public Data(int secID, int rowID, int rowType, boolean isList)
        {
            fSecID = secID;
            fRowID = rowID;
            fRowType = rowType;
            fIsList = isList;
        }
        
        @Override
        public boolean isSeparator()
        {
            return false;
        }

        @Override
        public boolean canInsert()
        {
            return false;
        }

        @Override
        public boolean canDelete()
        {
            return fIsList;
        }

        @Override
        public boolean canDrillDown()
        {
            return fIsList || (fRowType == EditorDataSource.ROWTYPE_CHILD);
        }

        @Override
        public boolean canEdit()
        {
            return (fRowType == EditorDataSource.ROWTYPE_TEXT) || (fRowType == EditorDataSource.ROWTYPE_NUMERIC);
        }

        @Override
        public boolean isNumeric()
        {
            return (fRowType == EditorDataSource.ROWTYPE_NUMERIC);
        }

        @Override
        public CharSequence getTitle()
        {
            return fDataSource.rowLabel(EditorActivity.this, fRowID, fSecID, fData);
        }

        @Override
        public CharSequence getValue()
        {
            return fDataSource.rowData(EditorActivity.this, fRowID, fSecID, fData);
        }

        @Override
        public void doRowClick()
        {
            /*
             * Open the child at this point
             */
            
            EditorData childData = fDataSource.childAt(EditorActivity.this, fRowID, fSecID, fData);
            EditorDataSource source = fDataSource.editorAt(EditorActivity.this, fRowID, fSecID, fData);
            
            Intent intent = new Intent(EditorActivity.this,EditorActivity.class);
            intent.putExtra("data", childData);
            intent.putExtra("source", source);
            startActivity(intent);
            // TODO: How do we handle return value?
        }

        @Override
        public void doInsDel()
        {
            // Delete this data row
            AlertDialog.Builder b = new AlertDialog.Builder(EditorActivity.this);
            b.setTitle("Are you sure?");
            b.setMessage("Are you sure you wish to delete this item?");
            b.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    try {
                        fDataSource.deleteRow(EditorActivity.this, fRowID, fSecID, fData);
                    }
                    catch (IOException e) {
                        showIOError();
                        return;
                    }
                    
//                    fListData.removeData(this); -- todo: fix this?
                    reloadData();
                }
            });
            b.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                }
            });
            b.show();
        }

        @Override
        public void updateValue(String string)
        {
            // TODO: Handle rename of aircraft
            try {
                fDataSource.updateRow(EditorActivity.this, fRowID, fSecID, fData, string);
                reloadData();
            }
            catch (IOException e) {
                showIOError();
                return;
            }
        }

        @Override
        public boolean canEditMenu()
        {
            return (fRowType == EditorDataSource.ROWTYPE_MENU);
        }

        @Override
        public String[] menuList()
        {
            return fDataSource.menuList(EditorActivity.this, fRowID, fSecID, fData);
        }

        @Override
        public void setSelectedMenu(int which)
        {
            try {
                fDataSource.setSelectedMenu(EditorActivity.this, fRowID, fSecID, fData, which);
                reloadData();
            }
            catch (IOException e) {
                showIOError();
                return;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editorlayout);
        
        /**
         * Get the data
         */
        if (savedInstanceState != null) {
            fData = (EditorData)savedInstanceState.getSerializable("data");
        }
        
        Intent intent = getIntent();
        if (fData == null) {
            fData = (EditorData)intent.getSerializableExtra("data");
        }
        fDataSource = (EditorDataSource)intent.getSerializableExtra("source");
        
        /*
         * Now refresh the contents
         */
        
        fTextView = (TextView)findViewById(R.id.title);
        fTextView.setText(fDataSource.editorTitle(this, fData));

        fListView = (ListView)findViewById(R.id.list);
        fListData = new EditorAdapter(this);
        fListView.setAdapter(fListData);
        
        reloadData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable("data", fData);    /* Save current state */
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        try {
            fDataSource.save(this,fData);
        }
        catch (IOException e) {
            Log.d("E6B","Save fail?",e);
        }
        fDataSource.onPause(this,fData);
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        fDataSource.onResume(this,fData);
        
        reloadData();
        reloadTitle();
    }
    
    /**
     * Internal routine reloads the list view
     */
    public void reloadData()
    {
        ArrayList<EditorAdapter.Row> list = new ArrayList<EditorAdapter.Row>();
        
        int sec,secLen = fDataSource.numberOfSections(this, fData);
        for (sec = 0; sec < secLen; ++sec) {
            String header = fDataSource.headerOfSection(this, sec, fData);
            EditorAdapter.Separator sep = new EditorAdapter.Separator(header);
            list.add(sep);
            
            boolean secIsArray = fDataSource.sectionIsArray(this, sec, fData);
            int row,rowLen = fDataSource.numberOfRowsInSection(this, sec, fData);
            for (row = 0; row < rowLen; ++row) {
                int type = fDataSource.rowType(this, row, sec, fData);
                Data rowData = new Data(sec,row,type,secIsArray);
                list.add(rowData);
            }
            if (secIsArray) {
                list.add(new Insert(sec));
            }
        }
        
        fListData.setData(list);
    }
    
    /**
     * Internal routine to reload the title
     */
    public void reloadTitle()
    {
        fTextView.setText(fDataSource.editorTitle(this, fData));
    }
    
    private void showIOError()
    {
        AlertUtil.message(this, "Error", "Unable to complete the operation due to an I/O error");
    }
}


