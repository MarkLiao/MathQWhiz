package mlcreations.mathqwhiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
public class SurvivalActivity extends AppCompatActivity {
    public static final String SURVIVAL_MODE = "survival_pref_mode";
    public static final String SURVIVAL_NUMBERS = "survival_pref_numbers";
    public static final String SURVIVAL_OPERATIONS = "survival_pref_operations";
    private boolean survivalPreferencesChanged = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survival);

        Toolbar toolbar = (Toolbar) findViewById(R.id.survival_toolbar);
        setSupportActionBar(toolbar);


        PreferenceManager.setDefaultValues(SurvivalActivity.this, R.xml.survival_preferences, true);
        PreferenceManager.getDefaultSharedPreferences(SurvivalActivity.this).registerOnSharedPreferenceChangeListener(survivalPrefListener);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SurvivalFragment survivalFragment = (SurvivalFragment)getFragmentManager().findFragmentById(R.id.survivalFragment);
        survivalFragment.updateDifficulty(PreferenceManager.getDefaultSharedPreferences(SurvivalActivity.this));
        survivalFragment.updateNumbers(PreferenceManager.getDefaultSharedPreferences(SurvivalActivity.this));
        survivalFragment.updateOperations(PreferenceManager.getDefaultSharedPreferences(SurvivalActivity.this));
        survivalFragment.reset();
        survivalPreferencesChanged = false;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu){
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point();
        display.getRealSize(screenSize);
        if(screenSize.x < screenSize.y){
            getMenuInflater().inflate(R.menu.menu_survival,menu);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.survival_action_settings:
                Intent preferenceIntent = new Intent(this, SurvivalSettingsActivity.class);
                startActivity(preferenceIntent);
                return true;
            case R.id.survival_reset:
                SurvivalFragment sFrag = (SurvivalFragment)getFragmentManager().findFragmentById(R.id.survivalFragment);
                sFrag.reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public SharedPreferences.OnSharedPreferenceChangeListener survivalPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            survivalPreferencesChanged = true;

            SurvivalFragment tFrag = (SurvivalFragment)getFragmentManager().findFragmentById(R.id.survivalFragment);
            if(survivalPreferencesChanged){

                if(key.equals(SURVIVAL_MODE)){
                    tFrag.updateDifficulty(sharedPreferences);
                    tFrag.reset();
                }
                else if (key.equals(SURVIVAL_NUMBERS)){
                    Set<String> Numbers = sharedPreferences.getStringSet(SURVIVAL_NUMBERS,null);
                    if(Numbers != null && Numbers.size() > 0){
                        tFrag.updateNumbers(sharedPreferences);
                        tFrag.reset();
                    }
                    else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        for(int i = 0; i < 11; i++){
                            Numbers.add(String.valueOf(i));
                        }
                        editor.putStringSet(SURVIVAL_NUMBERS,Numbers);
                        editor.commit();
                        Toast.makeText(SurvivalActivity.this, "Setting numbers 0-10 as default numbers. One number must be selected", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (key.equals(SURVIVAL_OPERATIONS)){
                    Set<String> Operations = sharedPreferences.getStringSet(SURVIVAL_OPERATIONS,null);
                    if(Operations != null && Operations.size()>0){
                        tFrag.updateOperations(sharedPreferences);
                        tFrag.reset();
                    }
                    else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Operations.add("+");
                        editor.putStringSet(SURVIVAL_OPERATIONS, Operations);
                        editor.commit();
                        Toast.makeText(SurvivalActivity.this,"Setting default operation to '+' one operation must be selected!", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(SurvivalActivity.this,"Restarting quiz with new settings",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(SurvivalActivity.this).unregisterOnSharedPreferenceChangeListener(survivalPrefListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(SurvivalActivity.this).registerOnSharedPreferenceChangeListener(survivalPrefListener);
    }
}
