/*  AbstractAdapter.java
 *
 *  Created on Nov 15, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.database.DataSetObserver;
import android.widget.ListAdapter;

public abstract class AbstractAdapter<T> implements ListAdapter
{
    private List<T> fList;
    private HashSet<DataSetObserver> fObservers = new HashSet<DataSetObserver>();

    @Override
    public int getCount()
    {
        if (fList == null) return 0;
        return fList.size();
    }

    @Override
    public T getItem(int position)
    {
        return fList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isEmpty()
    {
        return getCount() == 0;
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
        return true;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    public void setData(List<T> data)
    {
        fList = new ArrayList<T>(data);
        fireInvalidated();
    }
    
    protected void fireInvalidated()
    {
        for (DataSetObserver d: fObservers) {
            d.onInvalidated();
        }
    }
    
    protected void fireChanged()
    {
        for (DataSetObserver d: fObservers) {
            d.onChanged();
        }
    }
}


