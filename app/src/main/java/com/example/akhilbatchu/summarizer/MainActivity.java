package com.example.akhilbatchu.summarizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rengwuxian.materialedittext.MaterialEditText;

import am.appwise.components.ni.NoInternetDialog;
import at.markushi.ui.CircleButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

import android.support.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    CircleButton circularbutton;
    LoadingButton fbt;
    EditText lemail,lpassword;
    private static final String TAG = "simplifiedcoding";
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 234;
    private SharedPreferences.Editor loginPrefsEditor;
    Context t;
    private Boolean saveLogin;
    private SharedPreferences loginPreferences;
    MaterialEditText forgotemail;
    CheckBox check;
    int save;
    SignInButton googleButton;
    NoInternetDialog noInternetDialog;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fbt = (LoadingButton) findViewById(R.id.ButtonLogin);
        circularbutton = (CircleButton)findViewById(R.id.tosignup);
        fbt.setTypeface(Typeface.SERIF);
        noInternetDialog = new NoInternetDialog.Builder(this).build();
        lemail = (EditText)findViewById(R.id.loginEmail);
        lpassword = (EditText)findViewById(R.id.loginPassword);
        fbt.setCornerRadius(100f);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleButton = (SignInButton)findViewById(R.id.sign_in_button);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        check   =(CheckBox)findViewById(R.id.saveLoginCheckBox);
        t= this;
//        for(int i=0;i<googleButton.getChildCount();i++)
//        {        View v = googleButton.getChildAt(i);
//
//            if (v instanceof TextView)
//            {
//                TextView tv = (TextView) v;
//                tv.setTextSize(14);
//                tv.setTypeface(null, Typeface.NORMAL);
//                tv.setText("My Text");
//                tv.setTextColor(Color.parseColor("#FFFFFF"));
//                tv.setBackgroundDrawable(getResources().getDrawable(
//                        R.drawable.googlecircle));
//                tv.setSingleLine(true);
//                tv.setPadding(15, 15, 15, 15);
//
//                return;
//            }
//        }
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        Shader shader = new LinearGradient(0f,0f,1000f,100f, 0xAAE53935, 0xAAFF5722, Shader.TileMode.CLAMP);
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        fbt.setBackgroundShader(shader);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check.isChecked())
                {
                    save=0;
                    loginPrefsEditor.putString("username", "");
                    loginPrefsEditor.putString("password","");
                    loginPrefsEditor.putBoolean("saveLogin", false);

                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                if(check.isChecked()) {
                    save = 1;
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", lemail.getText().toString());
                    loginPrefsEditor.putString("password", lpassword.getText().toString());
                    loginPrefsEditor.commit();
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        if (saveLogin) {
            lemail.setText(loginPreferences.getString("username", ""));
            lpassword.setText(loginPreferences.getString("password", ""));
            check.setChecked(true);
        }
        fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbt.startLoading();
                Login();
            }
        });
        circularbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if(cm.getActiveNetworkInfo()==null) {
                    Toast.makeText(getApplicationContext(), "Please Switch on your Mobile data ", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this,Signup.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this,Home.class);
                            startActivity(intent);
                           // Toast.makeText(MainActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    public void Login()
    {
        if(TextUtils.isEmpty(lemail.getText().toString()))
        {
            makeToast("Enter email");
            fbt.cancelLoading();
            fbt.loadingFailed();
            return;
        }
        if(TextUtils.isEmpty(lpassword.getText().toString()))
        {
            makeToast("Enter the password");
            fbt.cancelLoading();
            fbt.loadingFailed();
            return;
        }
        if(check.isChecked()) {
            save = 1;
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", lemail.getText().toString());
            loginPrefsEditor.putString("password", lpassword.getText().toString());
            loginPrefsEditor.commit();
        }
        else
        {
            save=0;
            loginPrefsEditor.putString("username", "");
            loginPrefsEditor.putString("password","");
            loginPrefsEditor.putBoolean("saveLogin", false);

            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
        final android.app.AlertDialog waitingDialog = new SpotsDialog(t);


        mAuth.signInWithEmailAndPassword(lemail.getText().toString(), lpassword.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        String email = lemail.getText().toString();
                        if (!task.isSuccessful()) {
                            // there was an error
                            fbt.cancelLoading();
                            fbt.loadingFailed();
                            SweetAlertDialog a =new SweetAlertDialog(t,SweetAlertDialog.ERROR_TYPE);
                                a.setCancelable(false);
                                    a.setTitleText("Oops")
                                    .setContentText("Authentication Denied")
                                    .show();
                        } else {
                            fbt.cancelLoading();
                            fbt.loadingSuccessful();

                            if(!check.isChecked()) {
                                lemail.setText("");
                                lpassword.setText("");
                            }
                            Intent intent = new Intent(MainActivity.this,Home.class);
                            intent.putExtra("email",email);
                            startActivity(intent);

                        }
                    }
                });

    }

    public void makeToast(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent =new Intent(MainActivity.this,Home.class);
            startActivity(intent);
            finish();
        }
    }

    public void ForgotPassword(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("CHANGE PASSWORD");
        alert.setMessage("Please fill all the information");
        LayoutInflater inflater = LayoutInflater.from(this);
        alert.setCancelable(false);
        View layout_pwd = inflater.inflate(R.layout.alertforgotpasswordlayout,null);
        forgotemail = (MaterialEditText)layout_pwd.findViewById(R.id.currentforgotemail);
        alert.setView(layout_pwd);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(forgotemail.getText().toString())) {
                    makeToast("Enter the email");

                } else {
                    String e = forgotemail.getText().toString();
                    final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                    waitingDialog.show();
                    mAuth.sendPasswordResetEmail(e)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    waitingDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        SweetAlertDialog a = new SweetAlertDialog(t, SweetAlertDialog.ERROR_TYPE);
                                        a.setCancelable(false);
                                        a.setTitleText("Oops...")
                                                .setContentText("Something went wrong!!!")
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
        alert.show();
    }

    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
}
