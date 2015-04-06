/*
 * Copyright (C) 2014 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */

package org.fdroid.fdroid.installer;

import java.io.File;
import java.util.List;

import org.fdroid.fdroid.Preferences;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * Abstract Installer class. Also provides static methods to automatically
 * instantiate a working Installer based on F-Droids granted permissions.
 */
abstract public class Installer {
    protected Context mContext;
    protected PackageManager mPm;
    protected InstallerCallback mCallback;

    public static final String TAG = "fdroid.Installer";

    /**
     * This is thrown when an Installer is not compatible with the Android OS it
     * is running on. This could be due to a broken superuser in case of
     * RootInstaller or due to an incompatible Android version in case of
     * SystemPermissionInstaller
     */
    public static class AndroidNotCompatibleException extends Exception {

        private static final long serialVersionUID = -8343133906463328027L;

        public AndroidNotCompatibleException() {
        }

        public AndroidNotCompatibleException(String message) {
            super(message);
        }

        public AndroidNotCompatibleException(Throwable cause) {
            super(cause);
        }

        public AndroidNotCompatibleException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Callback from Installer. NOTE: This callback can be in a different thread
     * than the UI thread
     */
    public interface InstallerCallback {

        public static final int OPERATION_INSTALL = 1;
        public static final int OPERATION_DELETE = 2;

        public static final int ERROR_CODE_CANCELED = 1;
        public static final int ERROR_CODE_OTHER = 2;

        public void onSuccess(int operation);

        public void onError(int operation, int errorCode);
    }

    public Installer(Context context, PackageManager pm, InstallerCallback callback)
            throws AndroidNotCompatibleException {
        this.mContext = context;
        this.mPm = pm;
        this.mCallback = callback;
    }

    /**
     * Creates a new Installer for installing/deleting processes starting from
     * an Activity
     *
     * @param activity
     * @param pm
     * @param callback
     * @return
     * @throws AndroidNotCompatibleException
     */
    public static Installer getActivityInstaller(Activity activity, PackageManager pm,
            InstallerCallback callback) {

        // if root installer has been activated in preferences -> RootInstaller
        boolean isRootInstallerEnabled = Preferences.get().isRootInstallerEnabled();
        if (isRootInstallerEnabled) {
            Log.d(TAG, "root installer preference enabled -> RootInstaller");

            try {
                return new RootInstaller(activity, pm, callback);
            } catch (AndroidNotCompatibleException e) {
                Log.e(TAG, "Android not compatible with RootInstaller!", e);
            }
        }

        // system permissions and pref enabled -> SystemInstaller
        boolean isSystemInstallerEnabled = Preferences.get().isSystemInstallerEnabled();
        if (isSystemInstallerEnabled) {
            if (hasSystemPermissions(activity, pm)) {
                Log.d(TAG, "system permissions -> SystemInstaller");

                try {
                    return new SystemInstaller(activity, pm, callback);
                } catch (AndroidNotCompatibleException e) {
                    Log.e(TAG, "Android not compatible with SystemInstaller!", e);
                }
            } else {
                Log.e(TAG, "SystemInstaller is enabled in prefs, but system-perms are not granted!");
            }
        }

        // Fallback -> DefaultInstaller
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Default installer on Android >= 4.0
            try {
                Log.d(TAG, "try default installer for Android >= 4");

                return new DefaultInstallerSdk14(activity, pm, callback);
            } catch (AndroidNotCompatibleException e) {
                Log.e(TAG, "Android not compatible with DefaultInstallerSdk14!", e);
            }
        } else {
            // Default installer on Android < 4.0
            try {
                Log.d(TAG, "try default installer for Android < 4");

                return new DefaultInstaller(activity, pm, callback);
            } catch (AndroidNotCompatibleException e) {
                Log.e(TAG, "Android not compatible with DefaultInstaller!", e);
            }
        }

        // this should not happen!
        return null;
    }

    public static Installer getUnattendedInstaller(Context context, PackageManager pm,
            InstallerCallback callback) throws AndroidNotCompatibleException {

        // if root installer has been activated in preferences -> RootInstaller
        boolean useRootInstaller = Preferences.get().isRootInstallerEnabled();
        if (useRootInstaller) {
            try {
                return new RootInstaller(context, pm, callback);
            } catch (AndroidNotCompatibleException e) {
                Log.e(TAG, "Android not compatible with RootInstaller!", e);
            }
        }

        if (hasSystemPermissions(context, pm)) {
            // we have system permissions!
            return new SystemInstaller(context, pm, callback);
        } else {
            // nope!
            throw new AndroidNotCompatibleException();
        }
    }

    public static boolean hasSystemPermissions(Context context, PackageManager pm) {
        int checkInstallPermission =
                pm.checkPermission(permission.INSTALL_PACKAGES, context.getPackageName());
        int checkDeletePermission =
                pm.checkPermission(permission.DELETE_PACKAGES, context.getPackageName());
        boolean permissionsGranted =
                (checkInstallPermission == PackageManager.PERMISSION_GRANTED
                && checkDeletePermission == PackageManager.PERMISSION_GRANTED);

        return permissionsGranted;
    }

    public void installPackage(File apkFile) throws AndroidNotCompatibleException {
        // check if file exists...
        if (!apkFile.exists()) {
            Log.e(TAG, "Couldn't find file " + apkFile + " to install.");
            return;
        }

        installPackageInternal(apkFile);
    }

    public void installPackage(List<File> apkFiles) throws AndroidNotCompatibleException {
        // check if files exist...
        for (File apkFile : apkFiles) {
            if (!apkFile.exists()) {
                Log.e(TAG, "Couldn't find file " + apkFile + " to install.");
                return;
            }
        }

        installPackageInternal(apkFiles);
    }

    public void deletePackage(String packageName) throws AndroidNotCompatibleException {
        // check if package exists before proceeding...
        try {
            mPm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Couldn't find package " + packageName + " to delete.");
            return;
        }

        deletePackageInternal(packageName);
    }

    protected abstract void installPackageInternal(File apkFile)
            throws AndroidNotCompatibleException;

    protected abstract void installPackageInternal(List<File> apkFiles)
            throws AndroidNotCompatibleException;

    protected abstract void deletePackageInternal(String packageName)
            throws AndroidNotCompatibleException;

    public abstract boolean handleOnActivityResult(int requestCode, int resultCode, Intent data);

    public abstract boolean supportsUnattendedOperations();
}
