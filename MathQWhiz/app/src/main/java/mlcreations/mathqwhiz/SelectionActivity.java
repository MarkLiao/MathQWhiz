package mlcreations.mathqwhiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * Created by Mark on 14/03/2016.
 */
public class SelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_selection_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.game_mode_toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.standard_button).setOnClickListener(standardButton);
        findViewById(R.id.TimeTrial_button).setOnClickListener(TimeButton);
        findViewById(R.id.Survival_button).setOnClickListener(SurvivalButton);

    }

    public OnClickListener standardButton = new OnClickListener(){
        @Override
        public void onClick(View v) {
            SelectionActivity.this.startActivity(new Intent(SelectionActivity.this,StandardActivity.class));
        }
    };

    public OnClickListener TimeButton = new OnClickListener(){
        @Override
        public void onClick(View v) {
            SelectionActivity.this.startActivity(new Intent(SelectionActivity.this,TimeTrialActivity.class));
        }
    };

    public OnClickListener SurvivalButton = new OnClickListener(){
        @Override
        public void onClick(View v) {
            SelectionActivity.this.startActivity(new Intent(SelectionActivity.this,SurvivalActivity.class));
        }
    };
}
