/*  WBDataEditor.java
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

public class WBDataEditor implements EditorDataSource
{

    @Override
    public String editorTitle(EditorActivity editor, EditorData groupData)
    {
        return ((WBData)groupData).getName();
    }

    @Override
    public int numberOfSections(EditorActivity editor, EditorData groupData)
    {
        return 2;
    }

    @Override
    public String headerOfSection(EditorActivity editor, int section, EditorData groupData)
    {
        switch (section) {
            case 0:
                return "Envelope Name";
            case 1:
                return "Envelope Data";
        }
        return null;
    }

    @Override
    public int numberOfRowsInSection(EditorActivity editor, int section, EditorData groupData)
    {
        switch (section) {
            case 0:
                return 1;
            case 1:
                return ((WBData)groupData).getDataCount();
        }
        return 0;
    }

    @Override
    public boolean sectionIsArray(EditorActivity editor, int section, EditorData groupData)
    {
        return (section == 1);
    }

    @Override
    public String rowLabel(EditorActivity editor, int row, int section, EditorData groupData)
    {
        switch (section) {
            case 0: return "Name";
            case 1: return String.format("%.2f", ((WBData)groupData).getDataPoint(row).getWeight());
        }
        return null;
    }

    @Override
    public String rowData(EditorActivity editor, int row, int section, EditorData groupData)
    {
        switch (section) {
            case 0: return ((WBData)groupData).getName();
            case 1: return String.format("%.2f", ((WBData)groupData).getDataPoint(row).getArm());
        }
        return null;
    }

    @Override
    public int rowType(EditorActivity editor, int row, int section, EditorData groupData)
    {
        switch (section) {
            case 0: return ROWTYPE_TEXT;
            case 1: return ROWTYPE_CHILD;
        }
        return 0;
    }

    @Override
    public EditorData childAt(EditorActivity editor, int row, int section, EditorData groupData)
    {
        WBData d = ((WBData)groupData);
        return new WBPointData(d.getAircraftID(),d.getDataRow(),row);
    }

    @Override
    public EditorDataSource editorAt(EditorActivity editor, int row, int section, EditorData groupData)
    {
        return new WBPointDataEditor();
    }

    @Override
    public void updateRow(EditorActivity editor, int row, int section, EditorData groupData, String value) throws IOException
    {
        if (section == 0) {
            ((WBData)groupData).setName(value);
            editor.reloadTitle();
        }
    }

    @Override
    public void deleteRow(EditorActivity editor, int row, int section, EditorData groupData) throws IOException
    {
        if (section == 1) {
            ((WBData)groupData).deleteRow(row);
        }
    }

    @Override
    public void insertRow(EditorActivity editor, int section, EditorData groupData) throws IOException
    {
        if (section == 1) {
            ((WBData)groupData).insertRow();
        }
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
        ((WBData)groupData).save();
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


