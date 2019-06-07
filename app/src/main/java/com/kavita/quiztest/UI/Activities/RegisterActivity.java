package com.kavita.quiztest.UI.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kavita.quiztest.Helpers.Helper;
import com.kavita.quiztest.Helpers.JsonSingleton;
import com.kavita.quiztest.Helpers.Url;
import com.kavita.quiztest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();

    private CardView register;
    private TextView textViewregister;
    private EditText editTextusername,editTextemail,editTextpassword,editTextconfirmpassword;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        register = findViewById(R.id.register);
        textViewregister = findViewById(R.id.textViewregister);
        editTextusername = findViewById(R.id.username);
        editTextpassword = findViewById(R.id.Password);
        editTextconfirmpassword = findViewById(R.id.confirmpassword);
        editTextemail = findViewById(R.id.Email);
        pd = new ProgressDialog(this);
        
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isnetworkavailable(RegisterActivity.this)){
                    //network available
                    if (confirmPass()){
                        registerUser();

                    }else {
                        Toast.makeText(RegisterActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    //network not available
                    Log.i(TAG, "onClick: register"+"Network not available");
                }
            }
        });
        textViewregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    
    private boolean confirmPass(){
        if (editTextpassword.getText().toString().equals(editTextconfirmpassword.getText().toString())) {
            Log.i(TAG, "confirmPass: "+editTextpassword.getText().toString()+"\t"+editTextconfirmpassword.getText().toString()
            );
            return true;
        }else
            return false;
    }

    private boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }


    private void registerUser() {
        final ProgressDialog progress;
        progress = new ProgressDialog(this);
        progress.setTitle("Please Wait!!");
        progress.setCancelable(false);
        progress.show();
        final String email = editTextemail.getText().toString().trim();
        final String username = editTextusername.getText().toString().trim();
        final String password = editTextpassword.getText().toString().trim();
        final String confirmpswd = editTextconfirmpassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextusername.setError("Please enter Username ");
            editTextusername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextemail.setError("Please enter Email");
            editTextemail.requestFocus();
            return;
        }
        if (!isValidEmailId(email)) {
            editTextemail.setError("Please enter valid email");
            editTextemail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)){
            editTextpassword.setError("Please enter password");
            editTextpassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmpswd)){
            editTextpassword.setError("Please confirm password");
            editTextpassword.requestFocus();
            return;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();

                try {
                    Log.d(TAG, "onResponse: registerUser()"+response);
                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject("register");
                    if (object.getString("status").equals("True")){
                          //user is  registered
                        Log.i(TAG, "onResponse: registerUser()"+"Registered");
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }else{
                        Log.i(TAG, "onResponse: registerUser()"+"Not Registerd");
                    }


                   // Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse-JsonException:registerUser() ",e );
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: registerUser()",error );
                pd.cancel();
               // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                return params;

            }
        };
        JsonSingleton.getInstance(RegisterActivity.this).addToQueue(stringRequest);

    }
}
