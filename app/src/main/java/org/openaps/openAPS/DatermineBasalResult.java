package org.openaps.openAPS;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public class DatermineBasalResult {

    public final String reason;
    public final double tempBasalRate;
    public final double eventualBG;
    public final double snoozeBG;

    public DatermineBasalResult(V8Object result) {
        reason = result.getString("reason");
        tempBasalRate = result.getDouble("rate");
        eventualBG = result.getDouble("eventualBG");
        snoozeBG = result.getDouble("snoozeBG");
        result.release();
    }
}
