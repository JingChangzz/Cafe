package com.zutubi.android.junitreport;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;

import com.baidu.cafe.local.LocalLib;
import com.baidu.cafe.utils.ReflectHelper;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

/**
 * @author luxiaoyu01@baidu.com
 * @date 2013-6-25
 * @version
 * @todo
 */
public class CafeListener extends JUnitReportListener {
    private int           mPackageRcv    = 0;
    private int           mPackageSnd    = 0;
    private static String mName          = null;
    private LocalLib      mLocalLib      = null;
    private Context       mTargetContext = null;

    public CafeListener(Context context, Context targetContext, String reportFile,
            String reportDir, boolean filterTraces, boolean multiFile,
            Instrumentation instrumentation) {
        super(context, targetContext, reportFile, reportDir, filterTraces, multiFile);
        // activity == null, so we can not use those fuction in Locallib which use it.
        this.mLocalLib = new LocalLib(instrumentation, null);
        this.mTargetContext = targetContext;
    }

    @Override
    public void startTest(Test test) {
        mName = ((TestCase) test).getName();
        LocalLib.mTestCaseName = mName;
        mPackageRcv = LocalLib.getPackageRcv(mTargetContext.getPackageName());
        mPackageSnd = LocalLib.getPackageSnd(mTargetContext.getPackageName());
        super.startTest(test);
    }

    @Override
    public void addError(Test test, Throwable error) {
        mLocalLib.screenShotNamedSuffix(mName);
        super.addError(test, error);
    }

    @Override
    public void addFailure(Test test, AssertionFailedError error) {
        mLocalLib.screenShotNamedSuffix(mName);
        super.addFailure(test, error);
    }

    @Override
    public void endTest(Test test) {
        long mTestStartTime = 0;
        try {
            mTestStartTime = (Long) ReflectHelper.getObjectProperty(this, 1, "mTestStartTime");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        String packageName = mTargetContext.getPackageName();
        String string = String.format(
                "Testcase: %s  Time: %sms  PackageRcv: %sbytes  PackageSnd: %sbytes",
                ((TestCase) test).getClass(), System.currentTimeMillis() - mTestStartTime,
                LocalLib.getPackageRcv(packageName) - mPackageRcv,
                LocalLib.getPackageSnd(packageName) - mPackageSnd);
        Log.i("NetworkStatus", string);
        super.endTest(test);
    }

}