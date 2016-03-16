package mlcreations.mathqwhiz;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Mark on 16/03/2016.
 */
public class SurvivalSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.survival_preferences);
    }
}
