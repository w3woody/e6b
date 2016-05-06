/*  WBPointDataEditor.java
 *
 *  Created on Dec 30, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.editor.aircraft;

import java.io.IOException;

import com.glenviewsoftware.e6b.editor.EditorActivity;
import com.glenviewsoftware.e6b.editor.EditorData;
import com.glenviewsoftware.e6b.editor.EditorDataSource;

public class WBPointDataEditor implements EditorDataSource
{
    @Override
    public String editorTitle(EditorActivity editor, EditorData groupData)
    {
        return "Point Data";
    }

    @Override
    public int numberOfSections(EditorActivity editor, EditorData groupData)
    {
        return 1;
    }

    @Override
    public String headerOfSection(EditorActivity editor, int section, EditorData groupData)
    {
        return "Weight/Balance";
    }

    @Override
    public int numberOfRowsInSection(EditorActivity editor, int section, EditorData groupData)
    {
        return 2;
    }

    @Override
    public boolean sectionIsArray(EditorActivity editor, int section, EditorData groupData)
    {
        return false;
    }

    @Override
    public String rowLabel(EditorActivity editor, int row, int section, EditorData groupData)
    {
        switch (row) {
            case 0: return "Weight";
            case 1: return "Arm";
        }
        return null;
    }

    @Override
    public String rowData(EditorActivity editor, int row, int section, EditorData groupData)
    {
        switch (row) {
            case 0: return String.format("%.2f", ((WBPointData)groupData).getWeight());
            case 1: return String.format("%.2f", ((WBPointData)groupData).getArm());
        }
        return null;
    }

    @Override
    public int rowType(EditorActivity editor, int row, int section, EditorData groupData)
    {
        return ROWTYPE_NUMERIC;
    }

    @Override
    public EditorData childAt(EditorActivity editor, int row, int section, EditorData groupData)
    {
        return null;
    }

    @Override
    public EditorDataSource editorAt(EditorActivity editor, int row, int section, EditorData groupData)
    {
        return null;
    }

    @Override
    public void updateRow(EditorActivity editor, int row, int section, EditorData groupData, String value) throws IOException
    {
        switch (row) {
            case 0: 
                ((WBPointData)groupData).setWeight(Double.parseDouble(value));
                break;
            case 1: 
                ((WBPointData)groupData).setArm(Double.parseDouble(value));
                break;
        }
    }

    @Override
    public void deleteRow(EditorActivity editor, int row, int section, EditorData groupData) throws IOException
    {
    }

    @Override
    public void insertRow(EditorActivity editor, int section, EditorData groupData) throws IOException
    {
    }

    @Override
    public String[] menuList(EditorActivity editor, int row, int section, EditorData groupData)
    {
        return null;
    }

    @Override
    public void setSelectedMenu(EditorActivity editor, int row, int section, EditorData groupData, int which) throws IOException
    {
    }

    @Override
    public void save(EditorActivity editor, EditorData groupData) throws IOException
    {
        ((WBPointData)groupData).save();
    }

    @Override
    public void onPause(EditorActivity editor, EditorData fData)
    {
    }

    @Override
    public void onResume(EditorActivity editor, EditorData fData)
    {
    }
}


