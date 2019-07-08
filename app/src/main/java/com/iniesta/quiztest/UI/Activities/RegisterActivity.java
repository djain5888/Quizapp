package com.iniesta.quiztest.UI.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.iniesta.quiztest.Helpers.Helper;
import com.iniesta.quiztest.Helpers.JsonSingleton;
import com.iniesta.quiztest.Helpers.Url;
import com.iniesta.quiztest.R;
import com.msg91.sendotpandroid.library.internal.Iso2Phone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    String verificationId;

    private CardView register1;

    private FirebaseAuth mAuth;

    PhoneAuthCredential credential;

    private TextView textViewregister;
    private EditText t1;

    private String mVerificationId;
    private EditText editTextusername, editTextemail, editTextpassword, editTextotp;
    private ProgressDialog pd;
    private TextView verify, verifyotp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        register1 = findViewById(R.id.register);
        register1.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        verify = findViewById(R.id.verify);
        verifyotp = findViewById(R.id.verifyotp);

        textViewregister = findViewById(R.id.textViewregister);
        editTextusername = findViewById(R.id.username);
        editTextpassword = findViewById(R.id.password);
        editTextotp = findViewById(R.id.otp);
        editTextemail = findViewById(R.id.mobile);
        pd = new ProgressDialog(this);
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    editTextotp.setText(code);
                    //verifyCode(code);
                    //Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(RegisterActivity.this, e.getMessage()+"\nFailed Try Again", Toast.LENGTH_SHORT).show();
            }
        };

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Regular.ttf");
        textViewregister.setTypeface(custom_font);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verify.setTextColor(getResources().getColor(R.color.red_600));
                String number = editTextemail.getText().toString();
                number="+91"+number;
                sendVerificationCode(number);
            }
        });

        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            //verifyotp.setTextColor(getResources().getColor(R.color.red_600));

//                if (code.isEmpty() || code.length() < 6) {
//
//                    editTextpassword.setError("Enter code...");
//                    //editTextpassword.requestFocus();
//                    return;
//                }
                //Toast.makeText(RegisterActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                verifyCode();

            }
        });

        register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isnetworkavailable(RegisterActivity.this)){
                    //network available

                        registerUser();


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



    private boolean isValidEmailId(String email) {

        if (Pattern.compile("[7-9][0-9]{9}").matcher(email).matches())
        {
            return true;
        }
        return false;

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
      //  final String confirmpswd = editTextconfirmpassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextusername.setError("Please enter Username ");
            editTextusername.requestFocus();
            progress.setCancelable(true);
            progress.cancel();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextemail.setError("Please enter phone number");
            editTextemail.requestFocus();
            progress.cancel();
            progress.setCancelable(true);
            return;
        }
        if (!isValidEmailId(email)) {
            editTextemail.setError("Please enter valid number");
            editTextemail.requestFocus();
            progress.cancel();
            progress.setCancelable(true);
            return;
        }

        if (TextUtils.isEmpty(password)){
            editTextpassword.setError("Please enter password");
            editTextpassword.requestFocus();
            progress.setCancelable(true);
            progress.cancel();
            return;
        }

//        if (TextUtils.isEmpty(confirmpswd)){
//            editTextpassword.setError("Please confirm password");
//            editTextpassword.requestFocus();
//            return;
//        }


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
                        Log.i("hello", "onResponse: registerUser()"+"Not Registerd");
                        progress.setCancelable(true);
                        Toast.makeText(RegisterActivity.this, "Unable to register Please Try again!!!!", Toast.LENGTH_LONG).show();
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
    private void sendVerificationCode(String number) {
        Toast.makeText(this, "Sending OTP", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }




//    private void verifyCode(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//    }

    private void verifyCode() {
        String code = editTextotp.getText().toString();
        credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            register1.setVisibility(View.VISIBLE);

                            //register1.setVisibility(View.VISIBLE);
                            Log.d("uyh", "signInWithCredential:success");

                        } else {
                            Log.w("lllllllllllllll", "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Now A Valid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
