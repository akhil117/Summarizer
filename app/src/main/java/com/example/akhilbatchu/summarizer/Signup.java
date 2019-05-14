package com.example.akhilbatchu.summarizer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import am.appwise.components.ni.NoInternetDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button bt;
    NoInternetDialog noInternetDialog;
    Context t;
    private AlphaAnimation buttonClick ;
    EditText name,email,idedit,pass,confirmpass;
    public  final  static  String TAG ="Tag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        noInternetDialog = new NoInternetDialog.Builder(this).build();
        t = this;
        buttonClick = new AlphaAnimation(1F, 0.8F);
        name  = (EditText)findViewById(R.id.userName);
        TextView tv = (TextView)findViewById(R.id.haveanaccount);
        email = (EditText)findViewById(R.id.userEmail);
        tv.setPaintFlags(tv.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        noInternetDialog = new NoInternetDialog.Builder(this).build();
        pass = (EditText)findViewById(R.id.userPassword);
        confirmpass = (EditText)findViewById(R.id.userConfirmPassword);
        bt = (Button)findViewById(R.id.ButtonLogin);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if(TextUtils.isEmpty(name.getText().toString()))
                {
                    makeToast("Please enter the name");
                    return;
                }

                if( name.getText().toString().length()<8)
                {
                    makeToast("User Name should be of minimum 8 characters");
                    return;
                }
                if(TextUtils.isEmpty(pass.getText().toString()))
                {
                    makeToast("Enter password");
                    return;
                }
                if(TextUtils.isEmpty(confirmpass.getText().toString()) )
                {
                    makeToast("Enter confirm password");
                    return;
                }

                if(!(pass.getText().toString().equals(confirmpass.getText().toString())))
                {
                    makeToast("Password mismatched");
                    return;
                }
                if(pass.length()<6)
                {
                    makeToast("minimum 6 characters are required in password");
                    return;
                }
                final android.app.AlertDialog waitingDialog = new SpotsDialog(t);
                waitingDialog.show();
                waitingDialog.setCancelable(false);
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(Signup.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Signup.this, "Details are in bad format",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    new SweetAlertDialog(t, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Success")
                                            .setContentText("New User has been created Successfully")
                                            .setConfirmText("LoginPage")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    startActivity(new Intent(Signup.this,MainActivity.class));
                                                    finish();
                                                    sDialog.dismissWithAnimation();
                                                }
                                            }).setCancelButton("Stayhere", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    }).show();

                                }
                            }
                        });

            }

    });
}

    public void haveAnAccount(View view)
    {
        Intent intent = new Intent(Signup.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void makeToast(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
