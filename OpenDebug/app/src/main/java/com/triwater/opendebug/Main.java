package com.triwater.opendebug;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by HM on 2017/6/2.
 */

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if(loadPackageParam.packageName.equals("android")){

            hookAllMethods(findClass("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader),
                    "getPackageInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            int debugParam = 32768;
                            Object result = param.getResult();
                            if(result != null) {
                                ApplicationInfo info = ((PackageInfo)result).applicationInfo;
                                int flags = info.flags;
                                if((flags & debugParam) == 0) {
                                    flags |= debugParam;
                                }

                                if((flags & 2) == 0) {
                                    flags |= 2;
                                }

                                info.flags = flags;
                                param.setResult(result);
                            }
                        }
                    });
        }
    }
}
