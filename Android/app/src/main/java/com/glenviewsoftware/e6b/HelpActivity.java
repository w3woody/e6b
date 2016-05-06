/*  HelpActivity.java
 *
 *  Created on Dec 24, 2012 by William Edward Woody
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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

/**
 * Online help
 */
public class HelpActivity extends Activity
{
    private WebView fWebView;
    private ImageView fPrev;
    private ImageView fNext;
    private ImageView fReload;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        fWebView = (WebView)findViewById(R.id.webView);
        fPrev = (ImageView)findViewById(R.id.back);
        fNext = (ImageView)findViewById(R.id.next);
        fReload = (ImageView)findViewById(R.id.reload);
        
        fWebView.loadUrl("file:///android_asset/help/index.html");
        fWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                resetButtons();
            }
            
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        resetButtons();
        
        fPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fWebView.goBack();
            }
        });
        
        fNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fWebView.goForward();
            }
        });
        
        fReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fWebView.reload();
            }
        });
    }
    
    
    private void resetButtons()
    {
        fPrev.setImageResource(fWebView.canGoBack() ? R.drawable.left_arrow : R.drawable.left_arrow_fade);
        fNext.setImageResource(fWebView.canGoForward() ? R.drawable.right_arrow : R.drawable.right_arrow_fade);
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fWebView.canGoBack()) {
                fWebView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

}


