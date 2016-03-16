package mlcreations.mathqwhiz;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Set;
import java.util.Vector;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Mark on 14/03/2016.
 */
public class TimeFragment extends Fragment {

    private TextView timeLeftTextView;
    private TextView timeQuestionTextView;
    private TextView timeQuestionNumberTextView;
    private LinearLayout timeButtonCase;


    private Set<String> timeNumberSet;
    private Set<String> timeOperationSet;

    private static final long INTERVAL = 1000;
    private static final long SIXTY = 60000;
    private static final long HUNDREDTWENTY= 120000;

    private GameTimer roundTimer;
    private boolean hasTimerStart = false;

    private int timeCurrentQuestionNumber;
    private int timeButtonsToDisplay;

    private int totalGuesses;
    private int totalCorrect;
    private int correctAnswer;

    private Vector<String> timeQuestionParameterList;
    private String currentQuestion;
    private SecureRandom tRandom;
    private String currentTimeChoice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.time_quiz_fragment,container,false);

        timeCurrentQuestionNumber = 1;
        totalGuesses = 0;
        totalCorrect = 0;
        tRandom = new SecureRandom();
        timeQuestionParameterList = new Vector<>();
        timeLeftTextView = (TextView)view.findViewById(R.id.timeLeftTextView);
        timeQuestionTextView = (TextView)view.findViewById(R.id.timeQuestionTextView);
        timeQuestionNumberTextView = (TextView)view.findViewById(R.id.timeQuestionNumberTextView);

        timeButtonCase = (LinearLayout)view.findViewById(R.id.timeLinearLayout2);
        for(int i = 0; i < timeButtonCase.getChildCount(); i++){
            FancyButton button = (FancyButton) timeButtonCase.getChildAt(i);
            button.setOnClickListener(timeClickListener);
        }

        timeQuestionNumberTextView.setText("Question " + timeCurrentQuestionNumber);
        return view;
    }

    public void updateTime(SharedPreferences sharedPreferences){
        String timeChoice = sharedPreferences.getString(TimeTrialActivity.TIME_TIME, null);
        if(hasTimerStart){
            roundTimer.cancel();
            hasTimerStart=false;
        }
        if(timeChoice.equals("60")){
            roundTimer = new GameTimer(SIXTY,INTERVAL);
            roundTimer.start();
            hasTimerStart = true;
        }
        else if(timeChoice.equals("120")){
            roundTimer = new GameTimer(HUNDREDTWENTY,INTERVAL);
            roundTimer.start();
            hasTimerStart = true;
        }
        currentTimeChoice = timeChoice;
        timeLeftTextView.setText("Time Left: " + timeChoice);
    }

    public void updateDifficulty(SharedPreferences sharedPreferences){
        String difficulty = sharedPreferences.getString(TimeTrialActivity.TIME_MODE,null);
        if (difficulty.equals("Easy")){
            timeButtonsToDisplay = 2;
        }
        else if (difficulty.equals("Normal")){
            timeButtonsToDisplay = 3;
        }
        else if (difficulty.equals("Hard")){
            timeButtonsToDisplay = 5;
        }

        for(int i = 0; i < timeButtonCase.getChildCount(); i++){
            FancyButton button = (FancyButton)timeButtonCase.getChildAt(i);
            button.setVisibility(View.INVISIBLE);
        }

        for(int i = 0; i < timeButtonsToDisplay ; i++){
            FancyButton button = (FancyButton)timeButtonCase.getChildAt(i);
            button.setVisibility(View.VISIBLE);
        }

        timeQuestionNumberTextView.setText("Question " + timeCurrentQuestionNumber);
    }

    public void updateNumbers(SharedPreferences sharedPreferences){
        timeNumberSet = sharedPreferences.getStringSet(TimeTrialActivity.TIME_NUMBERS,null);
    }

    public void updateOperations(SharedPreferences sharedPreferences){
        timeOperationSet = sharedPreferences.getStringSet(TimeTrialActivity.TIME_OPERATIONS,null);
    }

    public void reset(){
        totalCorrect = 0;
        totalGuesses = 0;
        currentQuestion="";
        timeQuestionParameterList.clear();
        if(hasTimerStart){
            roundTimer.cancel();
            hasTimerStart = false;
        }
        generateQuestion();
        loadQuestion();

        if(currentTimeChoice.equals("60")){
            roundTimer = new GameTimer(SIXTY,INTERVAL);
            roundTimer.start();
            hasTimerStart = true;
        }
        else{
            roundTimer = new GameTimer(HUNDREDTWENTY,INTERVAL);
            roundTimer.start();
            hasTimerStart = true;
        }
    }

    public void generateQuestion(){

        timeQuestionParameterList.clear();
        currentQuestion="";

        int numNumbers = timeNumberSet.size();
        int numOps = timeOperationSet.size();
        String[] tempNumArray = timeNumberSet.toArray(new String[timeNumberSet.size()]);
        String[] tempOpArray = timeOperationSet.toArray(new String[timeOperationSet.size()]);

        int index1 = tRandom.nextInt(numNumbers);
        int index2 = tRandom.nextInt(numNumbers);
        int randOp = tRandom.nextInt(numOps);

        timeQuestionParameterList.add(tempNumArray[index1]);
        timeQuestionParameterList.add(tempOpArray[randOp]);
        timeQuestionParameterList.add(tempNumArray[index2]);

        currentQuestion = tempNumArray[index1] + " " + tempOpArray[randOp] + " " + tempNumArray[index2];

    }

    public void loadQuestion(){
        correctAnswer = calcAnswer();
        timeQuestionNumberTextView.setText("Question " + timeCurrentQuestionNumber);
        timeQuestionTextView.setText(currentQuestion);

        int upperRange = correctAnswer + 20;
        int lowerRange = correctAnswer - 20;

        for(int i = 0; i < timeButtonsToDisplay; i++){
            FancyButton button = (FancyButton)timeButtonCase.getChildAt(i);
            int randAnswer = lowerRange + (int) (Math.random() * ((upperRange - lowerRange) + 1));
            while (randAnswer == correctAnswer){
                randAnswer = lowerRange + (int) (Math.random() * ((upperRange - lowerRange) + 1));
            }
            button.setTextColor(Color.BLACK);
            button.setEnabled(true);
            button.setText("" + randAnswer);
        }

        int randomRow = tRandom.nextInt(timeButtonsToDisplay);
        FancyButton button = (FancyButton)timeButtonCase.getChildAt(randomRow);
        button.setText("" + correctAnswer);

    }

    private int calcAnswer(){
        if(timeQuestionParameterList.get(1).equals("+")){
            return Integer.parseInt(timeQuestionParameterList.get(0)) +  Integer.parseInt(timeQuestionParameterList.get(2));
        }
        else if (timeQuestionParameterList.get(1).equals("-")){
            return Integer.parseInt(timeQuestionParameterList.get(0)) -  Integer.parseInt(timeQuestionParameterList.get(2));
        }
        else{
            return Integer.parseInt(timeQuestionParameterList.get(0)) *  Integer.parseInt(timeQuestionParameterList.get(2));
        }
    }

    public OnClickListener timeClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            FancyButton button = (FancyButton)v;
            if(button.getText().toString().equals(""+ correctAnswer)){
                totalCorrect++;
                totalGuesses++;
                timeCurrentQuestionNumber++;
                button.setTextColor(Color.GREEN);
                disableButtons();
                generateQuestion();
                loadQuestion();
            }
            else{
                totalGuesses++;
                button.setTextColor(Color.RED);
                Toast.makeText(getActivity(),"Try Again!",Toast.LENGTH_SHORT).show();
                button.setEnabled(false);
            }
        }
    };

    public void disableButtons(){
        for(int i = 0; i < timeButtonCase.getChildCount();i++){
            FancyButton button = (FancyButton)timeButtonCase.getChildAt(i);
            button.setEnabled(false);
        }
    }

    public class GameTimer extends CountDownTimer{
        public GameTimer(long startTime, long interval){
            super(startTime,interval);
        }
        @Override
        public void onFinish() {
            disableButtons();
            hasTimerStart = false;
            int accuracy = (int) (((double)totalCorrect/(double)totalGuesses) *100);
            timeLeftTextView.setText("Time Left: 0");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Times Up! Game Over!");
            builder.setMessage("Total Correct: " + totalCorrect +
                    "\nTotal Guesses: " + totalGuesses + "\nAccuracy: " + accuracy);
            builder.setPositiveButton("Play Again", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reset();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("End", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timeLeftTextView.setText("Time Left: " + millisUntilFinished/INTERVAL);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(hasTimerStart){
            roundTimer.cancel();
        }
    }
}
