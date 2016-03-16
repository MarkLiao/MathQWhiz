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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.Set;
import java.util.Vector;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by Mark on 14/03/2016.
 */
public class SurvivalFragment extends Fragment{


    private TextView survivalTimeLeftTextView;
    private TextView survivalQuestionTextView;
    private TextView survivalQuestionNumberTextView;
    private LinearLayout survivalButtonCase;


    private Set<String> survivalNumberSet;
    private Set<String> survivalOperationSet;

    private static final long INITIAL5ROUNDS = 20000;
    private static final long FIVEPLUSROUNDS = 18000;
    private static final long TENPLUSROUNDS = 15000;
    private static final long FIFTEENPLUSROUNDS = 12000;
    private static final long TWENTYPLUSROUNDS = 9000;
    private static final long TWENTYFIVEPLUSROUNDS =6000;
    private static final long THIRTYPLUSROUNDS = 3000;

    private static final long INTERVAL = 1000;

    private SurvivalGameTimer roundTimer;
    private boolean hasTimerStart = false;

    private int survivalCurrentQuestionNumber;
    private int survivalButtonsToDisplay;

    private int totalCorrect;
    private int correctAnswer;

    private Vector<String> survivalQuestionParameterList;
    private String currentQuestion;
    private SecureRandom SRandom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.survival_quiz_fragment,container, false);


        survivalCurrentQuestionNumber = 1;
        totalCorrect = 0;
        SRandom = new SecureRandom();
        survivalQuestionParameterList = new Vector<>();
        survivalTimeLeftTextView = (TextView)view.findViewById(R.id.survivalLeftTextView);
        survivalQuestionTextView = (TextView)view.findViewById(R.id.survivalQuestionTextView);
        survivalQuestionNumberTextView = (TextView)view.findViewById(R.id.survivalQuestionNumberTextView);

        survivalButtonCase = (LinearLayout)view.findViewById(R.id.survivalLinearLayout2);
        for(int i = 0; i < survivalButtonCase.getChildCount(); i++){
            FancyButton button = (FancyButton) survivalButtonCase.getChildAt(i);
            button.setOnClickListener(survivalClickListener);
        }

        survivalQuestionNumberTextView.setText("Question " + survivalCurrentQuestionNumber);


        return view;
    }

    public void updateDifficulty(SharedPreferences sharedPreferences){
        String difficulty = sharedPreferences.getString(SurvivalActivity.SURVIVAL_MODE,null);
        if (difficulty.equals("Easy")){
            survivalButtonsToDisplay = 2;
        }
        else if (difficulty.equals("Normal")){
            survivalButtonsToDisplay = 3;
        }
        else if (difficulty.equals("Hard")){
            survivalButtonsToDisplay = 5;
        }

        for(int i = 0; i < survivalButtonCase.getChildCount(); i++){
            FancyButton button = (FancyButton)survivalButtonCase.getChildAt(i);
            button.setVisibility(View.INVISIBLE);
        }

        for(int i = 0; i < survivalButtonsToDisplay ; i++){
            FancyButton button = (FancyButton)survivalButtonCase.getChildAt(i);
            button.setVisibility(View.VISIBLE);
        }

        survivalQuestionNumberTextView.setText("Question " + survivalCurrentQuestionNumber);

    }
    public void updateNumbers(SharedPreferences sharedPreferences){
        survivalNumberSet = sharedPreferences.getStringSet(SurvivalActivity.SURVIVAL_NUMBERS,null);
    }
    public void updateOperations(SharedPreferences sharedPreferences){
        survivalOperationSet = sharedPreferences.getStringSet(SurvivalActivity.SURVIVAL_OPERATIONS,null);
    }

    public void reset(){
        totalCorrect = 0;
        currentQuestion="";
        survivalQuestionParameterList.clear();
        if(hasTimerStart){
            roundTimer.cancel();
            hasTimerStart = false;
        }
        generateQuestion();
        loadQuestion();

        /*survivalTimeLeftTextView.setText("" + (INITIAL5ROUNDS/1000));
        roundTimer = new SurvivalGameTimer(INITIAL5ROUNDS,INTERVAL);
        roundTimer.start();*/

    }

    public void generateQuestion(){
        survivalQuestionParameterList.clear();
        currentQuestion="";

        int numNumbers = survivalNumberSet.size();
        int numOps = survivalOperationSet.size();
        String[] tempNumArray = survivalNumberSet.toArray(new String[survivalNumberSet.size()]);
        String[] tempOpArray = survivalOperationSet.toArray(new String[survivalOperationSet.size()]);

        int index1 = SRandom.nextInt(numNumbers);
        int index2 = SRandom.nextInt(numNumbers);
        int randOp = SRandom.nextInt(numOps);

        survivalQuestionParameterList.add(tempNumArray[index1]);
        survivalQuestionParameterList.add(tempOpArray[randOp]);
        survivalQuestionParameterList.add(tempNumArray[index2]);

        currentQuestion = tempNumArray[index1] + " " + tempOpArray[randOp] + " " + tempNumArray[index2];

    }

    public void loadQuestion(){

        if(hasTimerStart){
            roundTimer.cancel();
            hasTimerStart = false;
        }

        correctAnswer = calcAnswer();
        survivalQuestionNumberTextView.setText("Question " + survivalCurrentQuestionNumber);
        survivalQuestionTextView.setText(currentQuestion);

        int upperRange = correctAnswer + 20;
        int lowerRange = correctAnswer - 20;

        for(int i = 0; i < survivalButtonsToDisplay; i++){
            FancyButton button = (FancyButton)survivalButtonCase.getChildAt(i);
            int randAnswer = lowerRange + (int) (Math.random() * ((upperRange - lowerRange) + 1));
            while (randAnswer == correctAnswer){
                randAnswer = lowerRange + (int) (Math.random() * ((upperRange - lowerRange) + 1));
            }
            button.setTextColor(Color.BLACK);
            button.setEnabled(true);
            button.setText("" + randAnswer);
        }

        int randomRow = SRandom.nextInt(survivalButtonsToDisplay);
        FancyButton button = (FancyButton)survivalButtonCase.getChildAt(randomRow);
        button.setText("" + correctAnswer);
        if(survivalCurrentQuestionNumber <= 5) {
            survivalTimeLeftTextView.setText("" + (INITIAL5ROUNDS / 1000));
            roundTimer = new SurvivalGameTimer(INITIAL5ROUNDS, INTERVAL);
        }
        else if (survivalCurrentQuestionNumber > 5 && survivalCurrentQuestionNumber <= 10){
            survivalTimeLeftTextView.setText("" + (FIVEPLUSROUNDS / 1000));
            roundTimer = new SurvivalGameTimer(FIVEPLUSROUNDS, INTERVAL);
        }
        else if (survivalCurrentQuestionNumber > 10 && survivalCurrentQuestionNumber <= 15){
            survivalTimeLeftTextView.setText("" + (TENPLUSROUNDS / 1000));
            roundTimer = new SurvivalGameTimer(TENPLUSROUNDS, INTERVAL);
        }
        else if (survivalCurrentQuestionNumber > 15 && survivalCurrentQuestionNumber <= 20){
            survivalTimeLeftTextView.setText("" + (FIFTEENPLUSROUNDS / 1000));
            roundTimer = new SurvivalGameTimer(FIFTEENPLUSROUNDS, INTERVAL);
        }
        else if (survivalCurrentQuestionNumber > 20 && survivalCurrentQuestionNumber <= 25){
            survivalTimeLeftTextView.setText("" + (TWENTYPLUSROUNDS / 1000));
            roundTimer = new SurvivalGameTimer(TWENTYPLUSROUNDS, INTERVAL);
        }
        else if (survivalCurrentQuestionNumber > 25 && survivalCurrentQuestionNumber <= 30){
            survivalTimeLeftTextView.setText("" + (TWENTYFIVEPLUSROUNDS / 1000));
            roundTimer = new SurvivalGameTimer(TWENTYFIVEPLUSROUNDS, INTERVAL);
        }
        else if (survivalCurrentQuestionNumber > 30 /*&& survivalCurrentQuestionNumber <= 25*/){
            survivalTimeLeftTextView.setText("" + (THIRTYPLUSROUNDS / 1000));
            roundTimer = new SurvivalGameTimer(THIRTYPLUSROUNDS, INTERVAL);
        }
        roundTimer.start();
        hasTimerStart = true;
    }

    public int calcAnswer(){
        if(survivalQuestionParameterList.get(1).equals("+")){
            return Integer.parseInt(survivalQuestionParameterList.get(0)) +  Integer.parseInt(survivalQuestionParameterList.get(2));
        }
        else if (survivalQuestionParameterList.get(1).equals("-")){
            return Integer.parseInt(survivalQuestionParameterList.get(0)) -  Integer.parseInt(survivalQuestionParameterList.get(2));
        }
        else{
            return Integer.parseInt(survivalQuestionParameterList.get(0)) *  Integer.parseInt(survivalQuestionParameterList.get(2));
        }
    }

    public void disableButtons(){
        for(int i = 0; i < survivalButtonCase.getChildCount();i++){
            FancyButton button = (FancyButton) survivalButtonCase.getChildAt(i);
            button.setEnabled(false);
        }
    }

    public OnClickListener survivalClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            FancyButton button = (FancyButton)v;
            if(button.getText().toString().equals("" + correctAnswer)){
                totalCorrect++;
                survivalCurrentQuestionNumber++;
                button.setTextColor(Color.GREEN);
                disableButtons();
                generateQuestion();
                loadQuestion();
            }
            else{//show gameOver
                disableButtons();
                roundTimer.cancel();
                hasTimerStart = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Oops! Game Over!");
                builder.setMessage("Question: " + currentQuestion + "\nYour Answer: " + button.getText().toString()
                        + "\nCorrect Answer: " + correctAnswer
                        + "\nTotal Correct: " + totalCorrect);
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
        }
    };




    public class SurvivalGameTimer extends CountDownTimer {
        public SurvivalGameTimer(long startTime, long interval){
            super(startTime,interval);
        }
        @Override
        public void onFinish() {
            disableButtons();
            hasTimerStart = false;
            survivalTimeLeftTextView.setText("Time Left: 0");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Times Up! Game Over!");
            builder.setMessage("Total Correct: " + totalCorrect);
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
            survivalTimeLeftTextView.setText("Time Left: " + millisUntilFinished/INTERVAL);
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
