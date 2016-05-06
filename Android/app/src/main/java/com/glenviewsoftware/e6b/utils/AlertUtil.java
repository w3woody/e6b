/*  AlertUtil.java
 *
 *  Created on Nov 20, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertUtil
{
    public interface Callback
    {
        void done();
    }
    
    public static void message(Context activity, String title, String msg)
    {
        message(activity,title,msg,null);
    }
    
    public static void message(Context activity, String title, String msg, final Callback callback)
    {
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setTitle(title);
        b.setMessage(msg);
        b.setCancelable(true);
        b.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if (callback != null) callback.done();
            }
        });
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (callback != null) callback.done();
            }
        });
        b.show();
    }
}


