package org.openaps.openAPS;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public class DatermineBasalResult {

    public final String reason;
    public final double tempBasalRate;
    public final double eventualBG;
    public final double snoozeBG;
    public final int duration;

    public DatermineBasalResult(V8Object result) {
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
