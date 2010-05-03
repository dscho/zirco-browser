/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.zirco.ui.components;

import org.zirco.controllers.Controller;
import org.zirco.utils.Constants;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ZircoWebView extends WebView {
	
	private int mProgress = 100;
	
	private boolean mIsLoading = false;
	
	public ZircoWebView(Context context) {
		super(context);
		
		initializeOptions();
	}
	
	public ZircoWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initializeOptions();
	}	
	
	public void initializeOptions() {
		WebSettings settings = getSettings();
		
		// User settings		
		settings.setJavaScriptEnabled(Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_JAVASCRIPT, true));
		settings.setLoadsImagesAutomatically(Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, true));
		settings.setSaveFormData(Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_FORM_DATA, true));
		settings.setSavePassword(Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_PASSWORDS, true));
		
		CookieManager.getInstance().setAcceptCookie(Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_COOKIES, true));
		
		settings.setSupportZoom(true);
		
		// Technical settings
		settings.setSupportMultipleWindows(true);						
    	setLongClickable(true);
	}
	
	public void setProgress(int progress) {
		mProgress = progress;
	}
	
	public int getProgress() {
		return mProgress;
	}
	
	public void notifyPageStarted() {
		mIsLoading = true;
	}
	
	public void notifyPageFinished() {
		mProgress = 100;
		mIsLoading = false;
	}
	
	public boolean IsLoading() {
		return mIsLoading;
	}

}
