package com.example.akhilbatchu.summarizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    ArrayList<outputs> list;
    LinearLayoutManager m;
    DatabaseReference databaseReference;
    ProgressBar pgbar;
    Context t;
    TextView tv;
    RecyclerView recyclerView;
    String uid;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar)findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.collapsing_toolbar_recycler_view);
        tv = (TextView)findViewById(R.id.textHistory);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        pgbar = (ProgressBar)findViewById(R.id.Pgbar);
        pgbar.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
        mAuth = FirebaseAuth.getInstance();
        t = this;
        uid = mAuth.getUid().toString();
        m = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(m);
        databaseReference = FirebaseDatabase.getInstance().getReference("history").child(uid);
        if(actionBar!=null)
        {
            // Display home menu item.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("History");
        getData();
    }


    public void getData()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pgbar.setVisibility(View.INVISIBLE);
                for (DataSnapshot d : snapshot.getChildren()) {

                    outputs o  = d.getValue(outputs.class);
                    list.add(o);
                }
                if(list.size()==0)
                {
                         tv.setVisibility(View.VISIBLE);
                }
                else
                {
                    tv.setVisibility(View.GONE);
                }
                ProductAdapter adapter = new ProductAdapter(getApplicationContext(), list,t,uid);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
