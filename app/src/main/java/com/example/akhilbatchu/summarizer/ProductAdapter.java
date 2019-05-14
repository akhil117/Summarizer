package com.example.akhilbatchu.summarizer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    Context mCtx;
    private List<outputs> outputs;
    String uid;
    Context t;
    public ProductAdapter(Context mCtx,List<outputs> outputs,Context t, String uid)
    {
        this.uid = uid;
        this.mCtx = mCtx;
        this.t = t;
        this.outputs = outputs;
    }

    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.program_card, null);
        return new ProductViewHolder(view);
    }

    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        outputs  o = outputs.get(position);
        holder.dotspro.setTag(position);

            if(position%3==0) {
                holder.icon.setBackgroundResource(R.drawable.his1);
            }
        if(position%3==1) {
            holder.icon.setBackgroundResource(R.drawable.his3);
        }

        if(position%3==2) {
            holder.icon.setBackgroundResource(R.drawable.his2);

        }
        try {
            holder.topic.setText("    "+o.getTopic().toUpperCase());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        }catch (Exception e)
        {

        }

        holder.dotspro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pstion  = (int)v.getTag();
                PopupMenu popupMenu = new PopupMenu(t,holder.dotspro);
                popupMenu.inflate(R.menu.exam_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.ShareHistory)
                        {
                            Intent share = new Intent(android.content.Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                            // Add data to the intent, the receiving app will decide
                            // what to do with it.
                            share.putExtra(Intent.EXTRA_SUBJECT, outputs.get(pstion).getTopic());
                            share.putExtra(Intent.EXTRA_TEXT, outputs.get(pstion).getOutfile());

                            mCtx.startActivity(Intent.createChooser(share, "Share link!"));
                        }
                        if(item.getItemId()==R.id.HistoryDownload)
                        {
                            mCtx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(outputs.get(pstion).getOutfile())));

                        }
                        if(item.getItemId()==R.id.HistoryDelete)
                        {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference messageRef = database.getReference();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("history").child(uid).child(outputs.get(pstion).getTopic()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Log.d("Delete", "Notification has been deleted");
                                        }
                                        }
                                    });
                        }
                        return false;
                    }
                });

            }
        });
        Date date = new Date();
        holder.date.setText(o.getDate());
    }
    @Override
    public int getItemCount() {
        return outputs.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        TextView topic,date,icon,dotspro;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            dotspro = (TextView)itemView.findViewById(R.id.dotspro);
            icon  = (TextView)itemView.findViewById(R.id.ivMovie);
            date = (TextView)itemView.findViewById(R.id.BatchTitle);
            topic = (TextView)itemView.findViewById(R.id.namehistory);
        }
    }
}
