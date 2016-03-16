package mlcreations.mathqwhiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Mark on 14/03/2016.
 */
public class TimeTrialActivity extends AppCompatActivity {
    public static final String TIME_TIME = "time_pref_time";
    public static final String TIME_MODE = "time_pref_mode";
    public static final String TIME_NUMBERS = "time_pref_numbers";
    public static final String TIME_OPERATIONS = "time_pref_operations";
    private boolean timePreferencesChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timetrial_toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(TimeTrialActivity.this, R.xml.time_preferences, true);
        PreferenceManager.getDefaultSharedPreferences(TimeTrialActivity.this).registerOnSharedPreferenceChangeListener(timePrefListener);
    }

    @Override
    protected void onStart(){
        super.onStart();
        TimeFragment timeFragment = (TimeFragment)getFragmentManager().findFragmentById(R.id.timeFragment);
        timeFragment.updateTime(PreferenceManager.getDefaultSharedPreferences(TimeTrialActivity.this));
        timeFragment.updateDifficulty(PreferenceManager.getDefaultSharedPreferences(TimeTrialActivity.this));
        timeFragment.updateNumbers(PreferenceManager.getDefaultSharedPreferences(TimeTrialActivity.this));
        timeFragment.updateOperations(PreferenceManager.getDefaultSharedPreferences(TimeTrialActivity.this));
        timeFragment.reset();
        timePreferencesChanged = false;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu){
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point();
        display.getRealSize(screenSize);
        if(screenSize.x < screenSize.y){
            getMenuInflater().inflate(R.menu.menu_time,menu);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.timetrial_action_settings:
                Intent preferenceIntent = new Intent(this, TimeSettingsActivity.class);
                startActivity(preferenceIntent);
                return true;
            case R.id.timetrial_reset:
                TimeFragment tFrag = (TimeFragment)getFragmentManager().findFragmentById(R.id.timeFragment);
                tFrag.reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public OnSharedPreferenceChangeListener timePrefListener = new OnSharedPreferenceChangeListener(){
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            timePreferencesChanged = true;

            TimeFragment tFrag = (TimeFragment)getFragmentManager().findFragmentById(R.id.timeFragment);
            if(timePreferencesChanged){
                if(key.equals(TIME_TIME)){
                    tFrag.updateTime(sharedPreferences);
                    tFrag.reset();
                }
                else if(key.equals(TIME_MODE)){
                    tFrag.updateDifficulty(sharedPreferences);
                    tFrag.reset();
                }
                else if (key.equals(TIME_NUMBERS)){
                    Set<String> Numbers = sharedPreferences.getStringSet(TIME_NUMBERS,null);
                    if(Numbers != null && Numbers.size() > 0){
                        tFrag.updateNumbers(sharedPreferences);
                        tFrag.reset();
                    }
                    else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        for(int i = 0; i < 11; i++){
                            Numbers.add(String.valueOf(i));
                        }
                        editor.putStringSet(TIME_NUMBERS,Numbers);
                        editor.commit();
                        Toast.makeText(TimeTrialActivity.this, "Setting numbers 0-10 as default numbers. One number must be selected", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (key.equals(TIME_OPERATIONS)){
                    Set<String> Operations = sharedPreferences.getStringSet(TIME_OPERATIONS,null);
                    if(Operations != null && Operations.size()>0){
                        tFrag.updateOperations(sharedPreferences);
                        tFrag.reset();
                    }
                    else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Operations.add("+");
                        editor.putStringSet(TIME_OPERATIONS, Operations);
                        editor.commit();
                        Toast.makeText(TimeTrialActivity.this,"Setting default operation to '+' one operation must be selected!", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(TimeTrialActivity.this,"Restarting quiz with new settings",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(TimeTrialActivity.this).unregisterOnSharedPreferenceChangeListener(timePrefListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(TimeTrialActivity.this).registerOnSharedPreferenceChangeListener(timePrefListener);
    }
}
