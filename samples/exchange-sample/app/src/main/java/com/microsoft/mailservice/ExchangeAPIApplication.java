/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.mailservice;

import android.app.Activity;
import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.microsoft.office365.odata.EntityContainerClient;
import com.microsoft.office365.odata.impl.DefaultDependencyResolver;

// TODO: Auto-generated Javadoc

/**
 * The Class ExchangeAPIApplication.
 */
public class ExchangeAPIApplication extends Application {

    private AppPreferences mPreferences;
    private Thread.UncaughtExceptionHandler mDefaultUEH;

    private DefaultDependencyResolver mResolver;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("Client", "UncaughtException", ex);
            mDefaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    public void onCreate() {

        Log.d("Asset Management", "onCreate");
        super.onCreate();

        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        mPreferences = new AppPreferences(PreferenceManager.getDefaultSharedPreferences(this));
        mResolver = new DefaultDependencyResolver();
    }


    public EntityContainerClient getContainer() {

        EntityContainerClient container = new EntityContainerClient(Constants.ENDPOINT_ID, mResolver);
        return container;
    }

    public DefaultDependencyResolver getDependencyResolver() {
        return mResolver;
    }


    public AppPreferences getAppPreferences() {
        return mPreferences;
    }

    private boolean isNullOrEmpty(String value) {

        return value == null || value.length() == 0;
    }

    public boolean hasConfiguration() {

        if (isNullOrEmpty(mPreferences.getClientId()))
            return false;

        if (isNullOrEmpty(mPreferences.getRedirectUrl()))
            return false;

        return true;
    }

    /**
     * Handle error.
     *
     * @param throwable the throwable
     */
    public void handleError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("Asset", throwable.toString());
    }

    /**
     * Clear preferences.
     */
    public void clearPreferences(Activity activity) {
        CookieSyncManager syncManager = CookieSyncManager.createInstance(this);
        if (syncManager != null) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
            Authentication.resetToken(activity);
        }
    }

}
