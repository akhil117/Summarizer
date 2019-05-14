package com.example.akhilbatchu.summarizer;

import android.Manifest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.AuthenticationRequiredException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marozzi.roundbutton.RoundButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import am.appwise.components.ni.NoInternetDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , GoogleApiClient.ConnectionCallbacks  {

    RoundButton bt;
    RoundButton selectFile,downloads;
    FirebaseStorage storage;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
   static String tn,ts;

    MaterialEditText topname,topsent;
    TextView navView;
    int upload=0;
    ArrayList<output> o ;

    Context t;
    int flag;
    FirebaseDatabase database;

    NoInternetDialog noInternetDialog;
    String uid;
    String email;
    Uri Pdfuri;
    android.app.AlertDialog waitingDialog;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        bt = (RoundButton) findViewById(R.id.upload);
        downloads=(RoundButton)findViewById(R.id.Download);
        t=this;
        selectFile = (RoundButton) findViewById(R.id.selectFile);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
         waitingDialog = new SpotsDialog(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid().toString();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Home.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                {
                    selectPdf();
                }
                else
                {
                    ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     if(Pdfuri != null)
                     {
                         uploadPdfUri(Pdfuri);
                     }
                     else
                     {
                         makeToast("Select a file");
                     }
            }
        });


        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mostafa = ref.child("output");
                mostafa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        output o = dataSnapshot.getValue(output.class);
                        Log.i("topic",o.getOutfile());
                        Log.i("topic",o.getTopic());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        outputs os = new outputs();
                        os.setDate(dateFormat.format(date));
                        os.setOutfile(o.getOutfile());
                        os.setTopic(o.getTopic());
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("history").child(uid).child(o.getTopic()).setValue(os);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(o.getOutfile())));
                        waitingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUser = (TextView)headerView.findViewById(R.id.textView);
        navUser.setText(email);

    }
    public void makeToast(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
    public void InputForm(final Uri pdfuri)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("CHANGE PASSWORD");

        alert.setMessage("Please fill all the information");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.inputclass,null);
        topname = (MaterialEditText)layout_pwd.findViewById(R.id.TopicName);
        topsent = (MaterialEditText)layout_pwd.findViewById(R.id.TopSentences);
        alert.setView(layout_pwd);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag=0;
                progressDialog.dismiss();
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(topname.getText().toString()))
                {
                    makeToast("Please enter the Topic field");
                    progressDialog.dismiss();
                    return ;
                }
                if(TextUtils.isEmpty(topsent.getText().toString()))
                {
                    makeToast("Please enter the Top Sentences");
                    progressDialog.dismiss();
                    return;
                }

                tn = topname.getText().toString();
                ts = topsent.getText().toString();
                flag = 1;
                upload = 9;
                final String[] urls = new String[1];
                final String fileName = "input";

                final StorageReference storageReference = storage.getReference();
                storageReference.child(fileName).putFile(pdfuri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String url = storageReference.getDownloadUrl()+"";

                                storageReference.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String u = uri +"";
                                        Log.i("uris",u);
                                        DatabaseReference reference = database.getReference();

                                        Log.i("reference",reference+"");
                                        reference.child("input").child("url").setValue(u);
                                        reference.child("input").child("top_sent").setValue(ts);
                                        reference.child("input").child("topic").setValue(tn);
                                        reference.child("input").child("url").setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    progressDialog.dismiss();

                                                    waitingDialog.show();
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                                    DatabaseReference mostafa = ref.child("output");

                                                    mostafa.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            output o = dataSnapshot.getValue(output.class);
                                                            Date date = new Date();
                                                            long timeMilli = date.getTime();
                                                            Log.i("TimeMilli",timeMilli+"");
                                                            Log.i("topic",o.getOutfile()+timeMilli);
                                                            Log.i("topic",o.getTopic()+timeMilli);

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    SweetAlertDialog b = new SweetAlertDialog(t, SweetAlertDialog.ERROR_TYPE);
                                                    b.setCancelable(false);
                                                    b.setTitleText("Failure")
                                                            .setContentText("Error in Uploading the file")
                                                            .setConfirmText("ok")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                    sDialog.dismissWithAnimation();
                                                                }
                                                            }).show();                                        }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("File not successfully uploaded");
                        e.printStackTrace();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int currentProgress = (int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());;
                        progressDialog.setProgress(currentProgress);
                        progressDialog.setCancelable(false);
                    }
                });
            }
        });
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.getDatabase().getReference().child("output").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                waitingDialog.dismiss();
                if(upload==9)
                {
                    upload=0;
                    new SweetAlertDialog(t, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Processing Completed")
                            .setContentText("Please press the download file!!!")
                            .show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("TAGss","Datachanged");
                waitingDialog.dismiss();



            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                waitingDialog.dismiss();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                waitingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                waitingDialog.dismiss();

            }
        });
    }

    public void uploadPdfUri(Uri pdfuri)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
        InputForm(pdfuri);
    }

    public void selectPdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            Pdfuri = data.getData();
        }
        else
        {
                Toast.makeText(getApplicationContext(),"Please Select the file",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
                selectPdf();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please Provide Permission",Toast.LENGTH_LONG).show();
        }

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            try {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                finish();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(id==R.id.History)
        {
            Intent intent = new Intent(Home.this,History.class);
            intent.putExtra("uid",uid);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
