package org.openaps;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.TextView;
import org.openaps.openAPS.DatermineBasalResult;
import org.openaps.openAPS.DetermineBasalAdapterJS;
import org.openaps.openAPS.ScriptReader;

import java.io.IOException;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final TextView result = (TextView) findViewById(R.id.result);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DetermineBasalAdapterJS dbJS = new DetermineBasalAdapterJS(new ScriptReader(getApplicationContext()));
                    dbJS.setGlucoseStatus(120, 10, 10);

                    DatermineBasalResult text = dbJS.invoke();
                    Snackbar.make(view, "Result   " + text.reason, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                    result.setText(text.reason);
                    dbJS.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
