package mlcreations.mathqwhiz;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Mark on 15/03/2016.
 */
public class TimeSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.time_settings_toolbar);
        setSupportActionBar(toolbar);
    }
}
