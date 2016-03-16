package mlcreations.mathqwhiz;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Mark on 15/03/2016.
 */
public class TimeSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.time_preferences);
    }
}
