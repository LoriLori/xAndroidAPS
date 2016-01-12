package org.openaps.openAPS;


import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class DetermineBasalAdapterJS {
    private static Logger log = LoggerFactory.getLogger(DetermineBasalAdapterJS.class);


    private final ScriptReader mScriptReader;
    V8 mV8rt ;
    private V8Object mProfile;
    private V8Object mGlucoseStatus;
    private V8Object mIobData;
    private V8Object mCurrentTemp;

    private final String PARAM_currentTemp = "currentTemp";
    private final String PARAM_iobData = "iobData";
    private final String PARAM_glucoseStatus = "glucose_status";
    private final String PARAM_profile = "profile";

    public DetermineBasalAdapterJS(ScriptReader scriptReader) throws IOException {
        mV8rt = V8.createV8Runtime();
        mScriptReader  = scriptReader;

        initProfile();
        initGlucoseStatus();
        initIobData();
        initCurrentTemp();

        initLogCallback();

        initProcessExitCallback();

        initModuleParent();

        loadScript();
    }

    public DatermineBasalResult invoke() {

        mV8rt.executeVoidScript(
                "console.error(\"determine_basal(\"+\n" +
                        "JSON.stringify("+PARAM_glucoseStatus+")+ \", \" +\n" +
                        "JSON.stringify("+PARAM_currentTemp+")+ \", \" + \n" +
                        "JSON.stringify("+PARAM_iobData+")+ \", \" +\n" +
                        "JSON.stringify("+PARAM_profile+")+ \") \");");
        mV8rt.executeVoidScript(
                "var rT = determine_basal(" +
                        PARAM_glucoseStatus + ", " +
                        PARAM_currentTemp+", " +
                        PARAM_iobData +", " +
                        PARAM_profile + ", " +
                        "undefined, "+
                        "setTempBasal"+
                        ");");


        String ret = "";
        log.debug(mV8rt.executeStringScript("JSON.stringify(rT);"));

        V8Object v8ObjectReuslt = mV8rt.getObject("rT");
//        {
//            V8Object result = v8ObjectReuslt;
//            log.debug(Arrays.toString(result.getKeys()));
//        }

        DatermineBasalResult result = new DatermineBasalResult(v8ObjectReuslt);


        return result;
    }

    private void loadScript() throws IOException {
        mV8rt.executeVoidScript(
                readFile("oref0/lib/determine-basal/determine-basal.js"),
                "oref0/bin/oref0-determine-basal.js", 
                0);
        mV8rt.executeVoidScript("var determine_basal = module.exports;");
        
        mV8rt.executeVoidScript(
        		"var setTempBasal = function (rate, duration, profile, rT, offline) {" +
                    "rT.duration = duration;\n" +
                "    rT.rate = rate;" +
                    "return rT;" +
                "};",
        		"setTempBasal.js",
        		0
        		);
    }

    private void initModuleParent() {
        mV8rt.executeVoidScript("var module = {\"parent\":Boolean(1)};");
    }

    private void initProcessExitCallback() {
        JavaVoidCallback callbackProccessExit = new JavaVoidCallback() {
            @Override
            public void invoke(V8Object arg0, V8Array parameters) {
                if (parameters.length() > 0) {
                    Object arg1 = parameters.get(0);
                    log.error("ProccessExit " +arg1);
//					mV8rt.executeVoidScript("return \"\";");
                }
            }
        };
        mV8rt.registerJavaMethod(callbackProccessExit, "proccessExit");
        mV8rt.executeVoidScript("var process = {\"exit\": function () { proccessExit(); } };");
    }

    private void initLogCallback() {
        JavaVoidCallback callbackLog = new JavaVoidCallback() {
            @Override
            public void invoke(V8Object arg0, V8Array parameters) {
                if (parameters.length() > 0) {
                    Object arg1 = parameters.get(0);
                    log.debug("JSLOG " +	arg1);


                }
            }
        };
        mV8rt.registerJavaMethod(callbackLog, "log");
        mV8rt.executeVoidScript("var console = {\"log\":log, \"error\":log};");
    }

    private void initCurrentTemp() {
        mCurrentTemp = new V8Object(mV8rt);
        setCurrentTemp(30.0, 0.1);
        mCurrentTemp.add("temp", "absolute");

        mV8rt.add(PARAM_currentTemp, mCurrentTemp);
    }

    public void setCurrentTemp(double tempBasalDurationInMinutes, double tempBasalRateAbsolute) {
        mCurrentTemp.add("duration", tempBasalDurationInMinutes);
        mCurrentTemp.add("rate", tempBasalRateAbsolute);
    }

    private void initIobData() {
        mIobData = new V8Object(mV8rt);
        setIobData(0.0, 0.0, 0.0);

        mV8rt.add(PARAM_iobData, mIobData);
    }

    public void setIobData(double netIob, double netActivity, double bolusIob) {
        mIobData.add("iob", netIob);
        mIobData.add("activity", netActivity);
        mIobData.add("bolusiob", bolusIob);
    }

    private void initGlucoseStatus() {
        mGlucoseStatus = new V8Object(mV8rt);

        setGlucoseStatus(0.0, 0.0, 0.0);

        mV8rt.add(PARAM_glucoseStatus, mGlucoseStatus);
    }

    public void setGlucoseStatus(double glocoseValue, double glucoseDelta, double glucoseAvgDelta15m) {
        mGlucoseStatus.add("delta", glucoseDelta);
        mGlucoseStatus.add("glucose", glocoseValue);
        mGlucoseStatus.add("avgdelta", glucoseAvgDelta15m);

    }

    private void initProfile() {
        mProfile = new V8Object(mV8rt);

        mProfile.add("max_iob", 2.0);
        mProfile.add("carbs_hr", 28.0);
        mProfile.add("dia", 4.0);
        mProfile.add("type", "current");
        setProfile_CurrentBasal(1.6);
        mProfile.add("max_daily_basal", 1.1);
        setProfile_MaxBasal(2.0);
        mProfile.add("max_bg", 125);
        mProfile.add("min_bg", 106);
        mProfile.add("carbratio", 10);
        setProfile_Sens(27);
        mV8rt.add(PARAM_profile, mProfile);
    }

    public void setProfile_Sens(int sensitivityInMGDL) {
        mProfile.add("sens", sensitivityInMGDL);
    }

    public void setProfile_CurrentBasal(double currentBasal) {
        mProfile.add("current_basal", currentBasal);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String readFile(String filename) throws IOException {
        byte[] bytes = mScriptReader.readFile(filename);
        String string = new String(bytes, "UTF-8");
        if(string.startsWith("#!/usr/bin/env node")) {
            string = string.substring(20);
        }
        return string;
    }

    public void setProfile_MaxBasal(double max_basal) {
        mProfile.add("max_basal", max_basal);
    }

    public void release() {
        try {
            mProfile.release();
            mCurrentTemp.release();
            mIobData.release();
            mGlucoseStatus.release();
            mV8rt.release();
        } catch (Exception e) {
            log.error("release()",e);
        }
    }
}
