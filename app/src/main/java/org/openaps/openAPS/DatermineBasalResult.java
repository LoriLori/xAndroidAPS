package org.openaps.openAPS;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public class DatermineBasalResult {

    public String reason;
    public double tempBasalRate;
    public double eventualBG;
    public double snoozeBG;
    public int duration;
    public String error;

    public DatermineBasalResult(V8Object result) {
        if(result.contains("error")) {
            error = result.getString("error");
            return;
        }
        reason = result.getString("reason");
        eventualBG = result.getDouble("eventualBG");
        snoozeBG = result.getDouble("snoozeBG");
        if(result.contains("rate")) {
            tempBasalRate = result.getDouble("rate");
        } else {
            tempBasalRate = -1;
        }
        if(result.contains("duration")) {
            duration = result.getInteger("duration");
        } else {
            duration = -1;
        }

        result.release();
    }
}
