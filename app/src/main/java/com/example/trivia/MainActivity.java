package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<Question> questionList;
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this);

        currentQuestionIndex = prefs.getState();

        binding.txtHighest.setText(MessageFormat.format("Highest:{0}", String.valueOf(prefs.getHighestScore())));

        binding.txtScore.setText(MessageFormat.format(getString(R.string.curent_score), String.valueOf(score.getScore())));
        questionList = new Repository().getQuestions(questionArrayList ->{
            binding.txtQuestion.setText(questionArrayList.get(currentQuestionIndex)
                    .getAnswer());
                    updateCounter(questionArrayList);
                }
        );
        binding.btnNext.setOnClickListener(view -> {
            getNextQuestion();

        });
        binding.btnTrue.setOnClickListener(view -> {
            checkAnswer(true);
            uploadQuestion();
        });
        binding.btnFalse.setOnClickListener(view -> {
            checkAnswer(false);
            uploadQuestion();
        });

    }

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        uploadQuestion();
    }

    private void checkAnswer(boolean userChoseCorrect) {
        Boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if(userChoseCorrect == answer){
            snackMessageId = R.string.correct_answer;
            addPoint();
            fadeAnimation();
        } else {
            snackMessageId = R.string.incorrect_answer;
            deductPoint();
            shakeAnimation();
        }
        Toast.makeText(getApplicationContext(),snackMessageId,Toast.LENGTH_SHORT).show();
    }

    private void updateCounter(java.util.ArrayList<Question> questionArrayList) {
        binding.txtViewOutOf.setText(String.format(getString(R.string.txt_formatted),
                currentQuestionIndex, questionArrayList.size()));
    }
    private void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.txtQuestion.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.txtQuestion.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void uploadQuestion() {
        binding.txtQuestion.setText(questionList.get(currentQuestionIndex).getAnswer());
        updateCounter((ArrayList<Question>) questionList);
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation
                );
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.txtQuestion.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.txtQuestion.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void deductPoint(){

        if(scoreCounter > 0 ){
            scoreCounter -= 100;
            score.setScore(scoreCounter);
            binding.txtScore.setText(MessageFormat.format(getString(R.string.curent_score), String.valueOf(score.getScore())));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
        }
    }
    private void addPoint(){
        scoreCounter +=100;
        score.setScore(scoreCounter);
        binding.txtScore.setText(MessageFormat.format(getString(R.string.curent_score), String.valueOf(score.getScore())));
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}