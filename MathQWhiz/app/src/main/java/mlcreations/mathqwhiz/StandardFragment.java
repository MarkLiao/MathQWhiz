package mlcreations.mathqwhiz;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Mark on 14/03/2016.
 */
public class StandardFragment extends Fragment {

    private TextView questionNumberTextView;
    private TextView questionTextView;
    private TextView userResponseTextView;
    private LinearLayout buttonCase;
    private Handler handler;
    private SecureRandom mRandom;

    private int currentQNumber;
    private static final int EASYMODE = 10;
    private static final int NORMALMODE = 25;
    private static final int HARDMODE = 35;

    private int buttonsToDisplay;
    private int questionsInQuiz;
    private int correctAnswers;
    private int currentCorrectAnswer;
    private int totalGuesses;
    private int opCounter;
    private ArrayList<String> questionList;
    private Vector<String> questionParameterList;

    //NumberSet
    private Set<String> numberSet;
    private Set<String> operationSet;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.quizfragment,container,false);

        currentQNumber = 1;
        opCounter = 1;
        handler = new Handler();
        mRandom = new SecureRandom();
        questionParameterList = new Vector<>();

        questionList = new ArrayList<>();
        questionNumberTextView = (TextView)view.findViewById(R.id.questionNumberTextView);
        questionTextView = (TextView)view.findViewById(R.id.questionTextView);
        userResponseTextView = (TextView)view.findViewById(R.id.userResponseTextView);
        buttonCase = (LinearLayout)view.findViewById(R.id.linearlayout2);

        for(int i = 0; i < buttonCase.getChildCount(); i++){
            FancyButton button = (FancyButton)buttonCase.getChildAt(i);
            button.setOnClickListener(guessButtonClick);
        }

        questionNumberTextView.setText("Question 1 of " + EASYMODE);
        return view;
    }

    public void updateDifficulty(SharedPreferences sharedPreferences){
        String difficulty = sharedPreferences.getString(StandardActivity.STANDARD_MODE,null);
        if (difficulty.equals("Easy")){
            buttonsToDisplay = 2;
            questionsInQuiz = EASYMODE;
        }
        else if (difficulty.equals("Normal")){
            buttonsToDisplay = 3;
            questionsInQuiz = NORMALMODE;
        }
        else if (difficulty.equals("Hard")){
            buttonsToDisplay = 5;
            questionsInQuiz = HARDMODE;
        }

        for(int i = 0; i < buttonCase.getChildCount(); i++){
            FancyButton button = (FancyButton)buttonCase.getChildAt(i);
            button.setVisibility(View.INVISIBLE);
        }

        for(int i = 0; i < buttonsToDisplay ; i++){
            FancyButton button = (FancyButton)buttonCase.getChildAt(i);
            button.setVisibility(View.VISIBLE);
        }

        questionNumberTextView.setText("Question " + currentQNumber + " of " + questionsInQuiz);

    }
    public void updateNumbers(SharedPreferences sharedPreferences){
        numberSet = sharedPreferences.getStringSet(StandardActivity.STANDARD_NUMBERS,null);
    }
    public void updateOperations(SharedPreferences sharedPreferences){
        operationSet = sharedPreferences.getStringSet(StandardActivity.STANDARD_OPERATIONS,null);
    }
    public void reset(){

        totalGuesses = 0;
        correctAnswers =0;
        questionList.clear();
        questionParameterList.clear();
        currentQNumber = 1;
        opCounter=1;

        generateQuestions();
        loadQuestion();

    }

    public void generateQuestions(){
        int counter = 0;
        int numberOfNumbers = numberSet.size();
        int numberOfOperations = operationSet.size();

        String[] tempNumberArray = numberSet.toArray(new String[numberSet.size()]);
        String[] tempOpArray = operationSet.toArray(new String[operationSet.size()]);

        while (counter < questionsInQuiz){
            int randNumIndex1 = mRandom.nextInt(numberOfNumbers);
            int randNumIndex2 = mRandom.nextInt(numberOfNumbers);
            int randOp = mRandom.nextInt(numberOfOperations);

            questionParameterList.add(tempNumberArray[randNumIndex1]);
            questionParameterList.add(tempOpArray[randOp]);
            questionParameterList.add(tempNumberArray[randNumIndex2]);

            String tempQuestion = (tempNumberArray[randNumIndex1] + " " + tempOpArray[randOp] + " " + tempNumberArray[randNumIndex2]);
            questionList.add(tempQuestion);
            counter++;
        }

    }

    public void loadQuestion(){
        String cQuestion = questionList.remove(0);
        currentCorrectAnswer = calcAnswer();
        questionNumberTextView.setText("Question " + currentQNumber + " of " + questionsInQuiz);
        questionTextView.setText(cQuestion);
        opCounter +=3;

        //create random answers for buttons

        int upperRange = currentCorrectAnswer + 20;
        int lowerRange = currentCorrectAnswer - 20;

        for(int i = 0; i < buttonsToDisplay; i++){
            FancyButton button = (FancyButton)buttonCase.getChildAt(i);
            int randAnswer = lowerRange + (int) (Math.random() * ((upperRange - lowerRange) + 1));
            while (randAnswer == currentCorrectAnswer){
                randAnswer = lowerRange + (int) (Math.random() * ((upperRange - lowerRange) + 1));
            }
            button.setTextColor(Color.BLACK);
            button.setEnabled(true);
            button.setText("" + randAnswer);
        }

        int randomRow = mRandom.nextInt(buttonsToDisplay);
        FancyButton button = (FancyButton)buttonCase.getChildAt(randomRow);
        button.setText("" + currentCorrectAnswer);
    }

    public int calcAnswer(){

        if(questionParameterList.get(opCounter).equals("+")){
            return Integer.parseInt(questionParameterList.get(opCounter - 1)) +  Integer.parseInt(questionParameterList.get(opCounter + 1));
        }
        else if (questionParameterList.get(opCounter).equals("-")){
            return Integer.parseInt(questionParameterList.get(opCounter - 1)) -  Integer.parseInt(questionParameterList.get(opCounter + 1));
        }
        else{
            return Integer.parseInt(questionParameterList.get(opCounter - 1)) *  Integer.parseInt(questionParameterList.get(opCounter + 1));
        }
    }

    public OnClickListener guessButtonClick = new OnClickListener(){
        @Override
        public void onClick(View v) {
            FancyButton button = (FancyButton)v;
            if(button.getText().toString().equals(""+ currentCorrectAnswer)){
                correctAnswers++;
                totalGuesses++;
                currentQNumber++;
                button.setTextColor(Color.GREEN);
                disableButtons();

                int accuracy = (int) (((double)correctAnswers/(double)totalGuesses) *100);
                if(correctAnswers == questionsInQuiz){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setTitle("Congratulations you finished!");
                    builder.setMessage("Your Accuracy Rate is " + accuracy + "%");
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
                else{
                    handler.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            loadQuestion();
                        }
                    },500);
                }
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
        for(int i = 0; i < buttonsToDisplay; i++){
            FancyButton button = (FancyButton)buttonCase.getChildAt(i);
            button.setEnabled(false);
        }
    }


}
