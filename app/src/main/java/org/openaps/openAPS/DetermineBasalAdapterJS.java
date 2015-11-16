package org.openaps.openAPS;


import android.provider.MediaStore;
import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DetermineBasalAdapterJS {

    private final ScriptReader mScriptReader;
    V8 mV8rt ;

    public DetermineBasalAdapterJS(ScriptReader scriptReader) {
        mV8rt = V8.createV8Runtime();
        mScriptReader  = scriptReader;
    }

    public String invoke() throws IOException {
        mV8rt.executeVoidScript("var module = {\"parent\":Boolean(1)};");

        JavaVoidCallback callbackLog = new JavaVoidCallback() {
            @Override
            public void invoke(V8Object arg0, V8Array parameters) {
                if (parameters.length() > 0) {
                    Object arg1 = parameters.get(0);
//					System.out.println("LOG " +	arg1);


                }
            }
        };

        JavaVoidCallback callbackProccessExit = new JavaVoidCallback() {
            @Override
            public void invoke(V8Object arg0, V8Array parameters) {
                if (parameters.length() > 0) {
                    Object arg1 = parameters.get(0);
                    System.out.println("ProccessExit " +arg1);
//					mV8rt.executeVoidScript("return \"\";");
                }
            }
        };
        mV8rt.registerJavaMethod(callbackProccessExit, "proccessExit");
        mV8rt.executeVoidScript("var process = {\"exit\": function () { proccessExit(); } };");

        mV8rt.registerJavaMethod(callbackLog, "log");
        mV8rt.executeVoidScript("var console = {\"log\":log, \"error\":log};");

        mV8rt.executeVoidScript(readFile("oref0/bin/oref0-determine-basal.js"), "oref0/bin/oref0-determine-basal.js", 1);

        mV8rt.executeVoidScript("var determinebasal = init();");

        V8Object profile = new V8Object(mV8rt);
        profile.add("max_iob", 2.0);
        profile.add("carbs_hr", 28.0);
        profile.add("dia", 4.0);
        profile.add("type", "current");
        profile.add("current_basal", 1.6);
        profile.add("max_daily_basal", 1.1);
        profile.add("max_basal", 2);
        profile.add("max_bg", 125);
        profile.add("min_bg", 106);
        profile.add("carbratio", 10);
        profile.add("sens", 10);
        mV8rt.add("profile", profile);


        V8Object glucose_status = new V8Object(mV8rt);
        glucose_status.add("delta", 10.0);
        glucose_status.add("glucose", 100.0);
        glucose_status.add("avgdelta", 10.0);
        mV8rt.add("glucose_status", glucose_status);


        V8Object iob_data = new V8Object(mV8rt);
        iob_data.add("iob", 0.0);
        iob_data.add("activity", 0.0);
        iob_data.add("bolusiob", 0.0);
        mV8rt.add("iob_data", iob_data);

        V8Object currenttemp = new V8Object(mV8rt);
        currenttemp.add("duration", 30.0);
        currenttemp.add("rate", 0.1);
        currenttemp.add("temp", "absolute");
        mV8rt.add("currenttemp", currenttemp);


        V8Array determine_basal_parameters = new V8Array(mV8rt);
        determine_basal_parameters.add("glucose_status", glucose_status);
        determine_basal_parameters.add("currenttemp", currenttemp);
        determine_basal_parameters.add("iob_data", iob_data);
        determine_basal_parameters.add("profile", profile);

        mV8rt.executeVoidScript("var rT = determinebasal.determine_basal(glucose_status, currenttemp, iob_data, profile);");
        String ret =
                mV8rt.executeStringScript("JSON.stringify(rT);");

        return ret;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            mV8rt.release();
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

}
