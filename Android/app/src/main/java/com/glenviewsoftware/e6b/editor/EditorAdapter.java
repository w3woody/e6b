/*  EditorAdapter.java
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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.glenviewsoftware.e6b.R;

public class EditorAdapter implements ListAdapter
{
    /**
     * A row object
     */
    public abstract static class Row
    {
        private static long gID;
        private long fID;
        
        public Row()
        {
            fID = ++gID;
        }
        
        public abstract boolean isSeparator();
        public abstract boolean canInsert();
        public abstract boolean canDelete();
        public abstract boolean canDrillDown();
        public abstract boolean canEdit();
        public abstract boolean isNumeric();
        public abstract boolean canEditMenu();
        public abstract String[] menuList();
        public abstract CharSequence getTitle();
        public abstract CharSequence getValue();
        public abstract void doRowClick();
        public abstract void doInsDel();
        public abstract void updateValue(String string);
        public abstract void setSelectedMenu(int which);
    }
    
    public abstract static class Insert extends Row
    {
        @Override
        public boolean isSeparator()
        {
            return false;
        }

        @Override
        public boolean canInsert()
        {
            return true;
        }

        @Override
        public boolean canDelete()
        {
            return false;
        }

        @Override
        public boolean canDrillDown()
        {
            return false;
        }

        @Override
        public boolean canEdit()
        {
            return false;
        }

        @Override
        public boolean isNumeric()
        {
            return false;
        }

        @Override
        public CharSequence getTitle()
        {
            return "Insert";
        }

        @Override
        public CharSequence getValue()
        {
            return "";
        }

        @Override
        public void updateValue(String string)
        {
        }

        @Override
        public boolean canEditMenu()
        {
            return false;
        }

        @Override
        public String[] menuList()
        {
            return null;
        }
        
        @Override
        public void setSelectedMenu(int which)
        {
        }
    }
    
    public static class Separator extends Row
    {
        private String fTitle;
        
        public Separator(String title)
        {
            fTitle = title;
        }

        @Override
        public boolean isSeparator()
        {
            return true;
        }

        @Override
        public boolean canInsert()
        {
            return false;
        }

        @Override
        public boolean canDelete()
        {
            return false;
        }

        @Override
        public boolean canDrillDown()
        {
            return false;
        }

        @Override
        public boolean canEdit()
        {
            return false;
        }

        @Override
        public boolean isNumeric()
        {
            return false;
        }

        @Override
        public CharSequence getTitle()
        {
            return fTitle;
        }

        @Override
        public CharSequence getValue()
        {
            return null;
        }

        @Override
        public void doRowClick()
        {
        }

        @Override
        public void doInsDel()
        {
        }

        @Override
        public void updateValue(String string)
        {
        }

        @Override
        public boolean canEditMenu()
        {
            return false;
        }

        @Override
        public String[] menuList()
        {
            return null;
        }

        @Override
        public void setSelectedMenu(int which)
        {
        }
    }
    
    private LinkedList<Row> fList;
    private HashSet<DataSetObserver> fObservers = new HashSet<DataSetObserver>();
    private Context fContext;
    
    /**
     * Construct a new context for editing
     * @param c
     */
    public EditorAdapter(Context c)
    {
        fContext = c;
        fList = new LinkedList<Row>();
    }
    
    /**
     * Update the data
     * @param data
     */
    public void setData(Collection<Row> data)
    {
        fList.clear();
        fList.addAll(data);
        for (DataSetObserver obs: fObservers) {
            obs.onInvalidated();
        }
    }
    
    public void insertData(Row newItem, Row beforeItem)
    {
        int location;
        if (beforeItem == null) {
            location = fList.size();
        } else {
            location = fList.indexOf(beforeItem);
        }
        fList.add(location, newItem);
        fireChanged();
    }
    
    public void removeData(Row item)
    {
        fList.remove(item);
        fireChanged();
    }
    
    protected void fireChanged()
    {
        for (DataSetObserver obs: fObservers) {
            obs.onChanged();
        }
    }
    
    @Override
    public int getCount()
    {
        return fList.size();
    }

    @Override
    public Row getItem(int position)
    {
        return fList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return fList.get(position).fID;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (fList.get(position).isSeparator()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Internal handler helps with references
     */
    private class Handler
    {
        View fRootView;
        TextView fTextView;
        ImageView fInsDel;
        ImageView fDisclose;
        TextView fFixedText;
        Row fRow;
        
        public Handler(View v)
        {
            fRootView = v;
            fTextView = (TextView)v.findViewById(R.id.textView);
            fFixedText = (TextView)v.findViewById(R.id.fixedView);
            fInsDel = (ImageView)v.findViewById(R.id.insDel);
            fDisclose = (ImageView)v.findViewById(R.id.open);
        }
        
        public void setContent(Row t)
        {
            fRow = t;
            
            fTextView.setText(t.getTitle());
            
            if (!t.isSeparator()) {
                /*
                 * Set the clicks
                 */
                
                fRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        handleClick();
                    }
                });
                fInsDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        fRow.doInsDel();
                    }
                });
                
                /*
                 * Insert/delete button
                 */
                if (t.canInsert()) {
                    fInsDel.setVisibility(View.VISIBLE);
                    fInsDel.setImageResource(R.drawable.insert);
                } else if (t.canDelete()) {
                    fInsDel.setVisibility(View.VISIBLE);
                    fInsDel.setImageResource(R.drawable.delete);
                } else {
                    fInsDel.setVisibility(View.GONE);
                }
                
                /*
                 * Drill down button
                 */
                if (t.canDrillDown()) {
                    fDisclose.setVisibility(View.VISIBLE);
                } else {
                    fDisclose.setVisibility(View.GONE);
                }
                
                /*
                 * Edit field
                 */
                fFixedText.setText(t.getValue());
                fFixedText.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
					    handleClick();
					}
				});
            }
        }
        
        private void handleClick()
        {
            // -- when done call fRow.updateValue(s.toString());
            if (fRow.canEdit()) {
                // Do edit
                editText(fRow);
                
            } else if (fRow.canDrillDown()) {
                // not editable. Forward as click
                fRow.doRowClick();
                
            } else if (fRow.canEditMenu()) {
                // menu selection
                editMenu(fRow);
            }
        }
    }

    private void editMenu(final Row row)
    {
        String[] values = row.menuList();
    
        AlertDialog.Builder builder = new AlertDialog.Builder(fContext);
        builder.setTitle("Select Value");
        builder.setItems(values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                row.setSelectedMenu(which);
            }
        });
        builder.show();
    }
    
    private void editText(final Row row)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(fContext);
        LayoutInflater i = LayoutInflater.from(fContext);
        final View v = i.inflate(R.layout.editor_text, null);
        builder.setTitle(row.getTitle());
        builder.setView(v);
        builder.setPositiveButton("Update",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                EditText et = (EditText)v.findViewById(R.id.editText);
                String value = et.getText().toString();
                
                row.updateValue(value);
            }
        });
        
        EditText et = (EditText)v.findViewById(R.id.editText);
        CharSequence val = row.getValue();
        et.setText(val);
        if (row.isNumeric()) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        et.setSelection(0, val.length());
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        
        builder.show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Handler h;
        Row s = getItem(position);
        
        if (convertView != null) {
            h = (Handler)convertView.getTag();
        } else {
            LayoutInflater i = LayoutInflater.from(fContext);
            if (s.isSeparator()) {
                convertView = i.inflate(R.layout.editor_separator, null);
            } else {
                convertView = i.inflate(R.layout.editor_line, null);
            }
            convertView.setTag(h = new Handler(convertView));
        }

        h.setContent(s);
        return convertView;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isEmpty()
    {
        return fList.size() == 0;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer)
    {
        fObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer)
    {
        fObservers.remove(observer);
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return !fList.get(position).isSeparator();
    }

}


