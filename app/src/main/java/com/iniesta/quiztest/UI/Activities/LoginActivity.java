package com.iniesta.quiztest.UI.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iniesta.quiztest.Helpers.Helper;
import com.iniesta.quiztest.Helpers.JsonSingleton;
import com.iniesta.quiztest.Helpers.SharedPref;
import com.iniesta.quiztest.Helpers.Url;
import com.iniesta.quiztest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private TextView textView2;
    private EditText Email, Password;
    private CardView login;
    CheckBox showPwd;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        SharedPref.init(getApplicationContext());
        Email = findViewById(R.id.mobile);
        Password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        textView2 = findViewById(R.id.textViewLogin);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Regular.ttf");
        textView2.setTypeface(custom_font);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

            }
        });

        showPwd = findViewById(R.id.showPwd);

        showPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = Email.getText().toString().trim();
                String mPassword = Password.getText().toString().trim();
                Email.getText().clear();
                Password.getText().clear();


                if (checkAuth(mEmail, mPassword)) {
                    if (Helper.isnetworkavailable(LoginActivity.this)) {
                        Login(mEmail, mPassword);

                    } else {
                        Toast.makeText(LoginActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                        //network not available
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Field does not match", Toast.LENGTH_SHORT).show();
                    //fields does not match
                }
            }
        });

    }


    private boolean checkAuth(String email, String pass) {
        if (!email.isEmpty() || !pass.isEmpty()) {
            return true;
        } else {
            Email.setError("Please Enter Email");
            Password.setError("Please Enter Password");
            return false;
        }
    }

    private void Login(final String email, final String password) {
        final ProgressDialog progress;
        progress = new ProgressDialog(this);
        progress.setTitle("Please Wait!!");
        progress.setMessage("Logging In");
        progress.setCancelable(false);
        progress.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //  login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try {
                    Log.d(TAG, "onResponse: Login" + response
                    );
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("login");
                    JSONArray jsonArray = jsonObject1.getJSONArray("payload");
                    if (jsonObject1.getString("status").equals("True")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            //store data
                            Log.i("go64",response);
                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", object.getString("email"));
                            editor.putString("name", object.getString("username"));
                            editor.apply();
                            //start intent
                            Log.i(TAG, "onResponse: Login" + "LoggedIN");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {

                        Log.d(TAG, "onResponse: Login" + jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: Login ", error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        JsonSingleton.getInstance(LoginActivity.this).addToQueue(stringRequest);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
