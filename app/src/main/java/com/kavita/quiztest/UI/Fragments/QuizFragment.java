package com.kavita.quiztest.UI.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kavita.quiztest.Helpers.JsonSingleton;
import com.kavita.quiztest.Helpers.Url;
import com.kavita.quiztest.Models.ListQues;
import com.kavita.quiztest.R;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuizFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    String gid, test_id, correct_ans, test_marks;
    Long test_time;
    private TextView question_count, quizQuestion, counter, marks;
    private int currentQuizQuestion;
    private int quizCount;
    private List<ListQues> parsedObject;
    Button previous_btn, next_btn, submit;
    private RadioGroup radioGroup;
    private ListQues firstQues;
    private RadioButton optionOne, optionTwo, optionThree, optionFour;
    private JSONArray dataArray;
    public View view;
    private float score;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_quiz, container, false);

        question_count = view.findViewById(R.id.text_view_question_count);
        quizQuestion = view.findViewById(R.id.text_view_question);
        radioGroup = view.findViewById(R.id.radio_group);
        optionOne = view.findViewById(R.id.radio_button1);
        optionTwo = view.findViewById(R.id.radio_button2);
        optionThree = view.findViewById(R.id.radio_button3);
        optionFour = view.findViewById(R.id.radio_button4);
        previous_btn = view.findViewById(R.id.button_confirm_previous);
        next_btn = view.findViewById(R.id.button_confirm_next);
        submit = view.findViewById(R.id.button_confirm_submit);
        marks = view.findViewById(R.id.textviewMarks);
        gid = getArguments().getString("gid");
        test_time = getArguments().getLong("test_time");
        test_id = getArguments().getString("test_id");
        test_marks = getArguments().getString("test_marks");
       /* Log.d(TAG, "onCreateView: test_time"+test_time);
        Log.d(TAG, "onCreateView: options" + gid);
        Log.d(TAG, "onCreateView: QUESTIOn" + test_id);*/
        doinBackground();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

      /*  next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuizQuestion++;
                previous_btn.setEnabled(true);
                if (next_visibilty()) {
                   Log.d(TAG, "current Question: " + currentQuizQuestion);
                    try {
                        getScore();
                        displayquizdata(currentQuizQuestion);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    next_btn.setEnabled(false);
            }
        });
*/

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuizQuestion++;
                getScore();

                if (currentQuizQuestion < dataArray.length()) {
                    try {
                        displayquizdata(currentQuizQuestion);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String totalques = String.valueOf(dataArray.length());
                    Bundle bundle = new Bundle();
                    bundle.putString("score", String.valueOf(score));
                    bundle.putString("question", totalques);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new ResultFragment();
                    myFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();
                }

            }
        });
        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuizQuestion--;
                if (!next_btn.isActivated()) {
                    next_btn.setEnabled(true);
                }
                if (prev_visibilty()) {
                    //   Log.d(TAG, "onClick: " + currentQuizQuestion);
                    try {
                        displayquizdata(currentQuizQuestion);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    previous_btn.setEnabled(false);
            }
        });

        return view;
    }


    private void showPopup() {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
        alert.setMessage("Are you sure you want to Submit QuizTest?")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Bundle bundle = new Bundle();
                        Fragment myFragment = new ResultFragment();
                        bundle.putString("score", String.valueOf(score));
                        bundle.putString("question", String.valueOf(dataArray.length()));
                        myFragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();


                    }
                }).setNegativeButton("No", null);

        android.app.AlertDialog alert1 = alert.create();
        alert1.show();
    }


    private void doinBackground() {
        final ProgressDialog progress;
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading....");
        progress.setCancelable(false);
        progress.show();

        StringRequest stRequest = new StringRequest(Request.Method.POST, Url.URL_QUES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject jsonObject1 = obj.getJSONObject("quiz");
                    Log.i(TAG, "onResponse: RESponse" + response);

                    dataArray = jsonObject1.getJSONArray("payload");
                    if (jsonObject1.getString("status").equals("True")) {
                        currentQuizQuestion = 0;
                        displayquizdata(0);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ERROR", error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("gid", gid);
                params.put("test_id", test_id);

                return params;
            }
        };

        JsonSingleton.getInstance(getContext()).addToQueue(stRequest);

    }


    public void displayquizdata(int pointer) throws JSONException {

        //  Log.d(TAG, "displayquizdata: "+pointer);
       /* for(int i=0;i<dataArray.length();i++){
            // if both event_values and event_codes are of equal length
            dataArray<int>.add(dataArray.getString(i));
        }*/
        String totalques = String.valueOf(dataArray.length());

        String questioncount = Integer.toString(currentQuizQuestion + 1).concat("/").concat(totalques);
        question_count.setText(questioncount);
        quizQuestion.setText(dataArray.getJSONObject(pointer).getString("ques"));
        optionOne.setText(dataArray.getJSONObject(pointer).getString("option_1"));
        optionTwo.setText(dataArray.getJSONObject(pointer).getString("option_2"));
        optionThree.setText(dataArray.getJSONObject(pointer).getString("option_3"));
        optionFour.setText(dataArray.getJSONObject(pointer).getString("option_4"));
        correct_ans = dataArray.getJSONObject(pointer).getString("correct_option");
        marks.setText(test_marks);

        optionOne.setChecked(false);
        optionTwo.setChecked(false);
        optionThree.setChecked(false);
        optionFour.setChecked(false);

    }


    @Contract(pure = true)
    private boolean prev_visibilty() {
        if (currentQuizQuestion == -1) {
            return false;
        } else
            return true;
    }

    private boolean next_visibilty() {
        if ((currentQuizQuestion + 1) == question_count.getText().charAt(2)) {
            return false;
        } else
            return true;
    }

    private void getScore() {
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        RadioButton radioButton = view.findViewById(radioGroup.getCheckedRadioButtonId());
        String ans = Integer.toString(radioGroup.indexOfChild(radioButton) + 1);
        Log.d(TAG, "getScore: correct answer is" + correct_ans + "\tchosen answer is: " + ans);
        radioGroup.clearCheck();

        if (correct_ans.equals(ans)) {
            score += 1;
            Log.d(TAG, "getScore: " + score);
        } else {
            score -= 0;
            Log.d(TAG, "getScore: " + score);
        }
    }

}
