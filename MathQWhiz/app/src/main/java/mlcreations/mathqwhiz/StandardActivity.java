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
public class StandardActivity extends AppCompatActivity {
    public static final String STANDARD_MODE = "standard_pref_mode";
    public static final String STANDARD_NUMBERS = "standard_pref_numbers";
    public static final String STANDARD_OPERATIONS = "standard_pref_operations";
    private boolean preferencesChanged = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.standard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(StandardActivity.this, R.xml.standard_preferences,true);
        PreferenceManager.getDefaultSharedPreferences(StandardActivity.this).registerOnSharedPreferenceChangeListener(standardPrefListener);
    }

    @Override
    protected void onStart(){
        super.onStart();
        StandardFragment standardFragment = (StandardFragment)getFragmentManager().findFragmentById(R.id.standardFragment);
        standardFragment.updateDifficulty(PreferenceManager.getDefaultSharedPreferences(StandardActivity.this));
        standardFragment.updateNumbers(PreferenceManager.getDefaultSharedPreferences(StandardActivity.this));
        standardFragment.updateOperations(PreferenceManager.getDefaultSharedPreferences(StandardActivity.this));
        standardFragment.reset();
        preferencesChanged = false;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu){
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point();
        display.getRealSize(screenSize);
        if(screenSize.x < screenSize.y){
            getMenuInflater().inflate(R.menu.menu_standard,menu);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.standard_action_settings:
                Intent preferenceIntent = new Intent(this, StandardSettingsActivity.class);
                startActivity(preferenceIntent);
                return true;
            case R.id.standard_reset:
                StandardFragment sFrag = (StandardFragment)getFragmentManager().findFragmentById(R.id.standardFragment);
                sFrag.reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public OnSharedPreferenceChangeListener standardPrefListener = new OnSharedPreferenceChangeListener(){
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            preferencesChanged = true;

            StandardFragment sFrag = (StandardFragment)getFragmentManager().findFragmentById(R.id.standardFragment);
            if(preferencesChanged){
                if(key.equals(STANDARD_MODE)){
                    sFrag.updateDifficulty(sharedPreferences);
                    sFrag.reset();
                }
                else if (key.equals(STANDARD_NUMBERS)){
                    Set<String> Numbers = sharedPreferences.getStringSet(STANDARD_NUMBERS,null);
                    if(Numbers != null && Numbers.size() > 0){
                        sFrag.updateNumbers(sharedPreferences);
                        sFrag.reset();
                    }
                    else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        for(int i = 0; i < 11; i++){
                            Numbers.add(String.valueOf(i));
                        }
                        editor.putStringSet(STANDARD_NUMBERS,Numbers);
                        editor.commit();
                        Toast.makeText(StandardActivity.this, "Setting numbers 0-10 as default numbers. One number must be selected", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (key.equals(STANDARD_OPERATIONS)){
                    Set<String> Operations = sharedPreferences.getStringSet(STANDARD_OPERATIONS,null);
                    if(Operations != null && Operations.size()>0){
                        sFrag.updateOperations(sharedPreferences);
                        sFrag.reset();
                    }
                    else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Operations.add("+");
                        editor.putStringSet(STANDARD_OPERATIONS, Operations);
                        editor.commit();
                        Toast.makeText(StandardActivity.this,"Setting default operation to '+' one operation must be selected!", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(StandardActivity.this,"Restarting quiz with new settings",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(StandardActivity.this).unregisterOnSharedPreferenceChangeListener(standardPrefListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(StandardActivity.this).registerOnSharedPreferenceChangeListener(standardPrefListener);
    }


}
