/*  EditorDataSource.java
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
import java.io.Serializable;

/**
 * Editor data source; this is the interface which mediates between the data within
 * an object being edited, and how to edit the data
 */
public interface EditorDataSource extends Serializable
{
    public static final int ROWTYPE_TEXT = 1;
    public static final int ROWTYPE_NUMERIC = 2;
    public static final int ROWTYPE_MENU = 3;
    public static final int ROWTYPE_CHILD = 4;
    
    /* Title to show for this object */
    String editorTitle(EditorActivity editor, EditorData groupData);
    
    /* Sections and section headers */
    int numberOfSections(EditorActivity editor, EditorData groupData);
    String headerOfSection(EditorActivity editor, int section, EditorData groupData);
    int numberOfRowsInSection(EditorActivity editor, int section, EditorData groupData);
    boolean sectionIsArray(EditorActivity editor, int section, EditorData groupData);
    
    /* Data display */
    String rowLabel(EditorActivity editor, int row, int section, EditorData groupData);
    String rowData(EditorActivity editor, int row, int section, EditorData groupData);
    int rowType(EditorActivity editor, int row, int section, EditorData groupData);
    
    /* Child editing */
    EditorData childAt(EditorActivity editor, int row, int section, EditorData groupData);
    EditorDataSource editorAt(EditorActivity editor, int row, int section, EditorData groupData);
    
    /* Update data contents */
    void updateRow(EditorActivity editor, int row, int section, EditorData groupData, String value) throws IOException;
    
    /* Insert/delete data */
    void deleteRow(EditorActivity editor, int row, int section, EditorData groupData) throws IOException;
    void insertRow(EditorActivity editor, int section, EditorData groupData) throws IOException;
    
    /* Editor menu; this expects to run and update data when menu selected */
    String[] menuList(EditorActivity editor, int row, int section, EditorData groupData);
    void setSelectedMenu(EditorActivity editor, int row, int section, EditorData groupData, int which) throws IOException;

    /* Save the state to persistent storage */
    void save(EditorActivity editor, EditorData fData) throws IOException;

    void onPause(EditorActivity editor, EditorData fData);
    void onResume(EditorActivity editor, EditorData fData);
}


