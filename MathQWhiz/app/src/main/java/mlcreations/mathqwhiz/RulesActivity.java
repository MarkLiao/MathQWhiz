package mlcreations.mathqwhiz;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sembozdemir.viewpagerarrowindicator.library.ViewPagerArrowIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 16/03/2016.
 */
public class RulesActivity extends AppCompatActivity {

    private final static int totalPages =3;

    private ViewPager myPager;
    private PagerAdapter pAdapter;
    private ViewPagerArrowIndicator VPAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider);

        Toolbar toolbar = (Toolbar) findViewById(R.id.rules_toolbar);
        setSupportActionBar(toolbar);

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(Fragment.instantiate(this,StandardRules.class.getName()));
        fragments.add(Fragment.instantiate(this, TimeTrialRules.class.getName()));
        fragments.add(Fragment.instantiate(this, SurvivalRules.class.getName()));
        pAdapter = new MyPagerAdapter(getSupportFragmentManager(),fragments);
        myPager = (ViewPager)findViewById(R.id.viewpager);
        VPAI = (ViewPagerArrowIndicator)findViewById(R.id.viewPagerArrowIndicator);
        myPager.setAdapter(pAdapter);
        VPAI.bind(myPager);
    }
}
