package com.iniesta.quiztest.UI.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private FirebaseAuth mAuth;
    private CardView register;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextView textViewregister;
    private EditText t1;
     private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
//    public static final String INTENT_PHONENUMBER = "phonenumber";
//    public static final String INTENT_COUNTRY_CODE = "code";
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
        mAuth = FirebaseAuth.getInstance();
      //  t1=findViewById(R.id.verify);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Regular.ttf");
        textViewregister.setTypeface(custom_font);
        
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




        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                String a=t1.getText().toString();
                //int code=Integer.parseInt(a)''

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, a);
                signInWithPhoneAuthCredential(credential);
                // ...
            }
        };






    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
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

        if(Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches())
        {
            return true;
        }

        else if (Pattern.compile("(0/91)?[7-9][0-9]{9}").matcher(email).matches())
        {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
       email,        // Phone number to verify
        60,                 // Timeout duration
        TimeUnit.SECONDS,   // Unit of timeout
        this,               // Activity (for callback binding)
        mCallbacks);
            return true;// OnVerificationStateChangedCallbacks
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
