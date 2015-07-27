package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eclipsegroup.dorel.financetime.models.Index;

import java.util.Collections;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{

    private LayoutInflater inflater;
    List<Index> data = Collections.emptyList();
    private Context context;

    public RecyclerAdapter(Context context, List<Index> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.index_card, parent, false);
        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        /* We set data into it for each element */

        Index current = data.get(position); /* TODO: get element */

        holder.favorite = 0;            /* TODO: set element */
        holder.firstName.setText(current.firstName);
        holder.secondName.setText(current.secondName);
        holder.centralName.setText(current.centralName);
        holder.max.setText("Max " + current.max.toString());
        holder.min.setText("Min  " + current.min.toString());
        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GraphicActivity.class);
                context.startActivity(intent);
            }
        });

        if(holder.favorite == 1){
            holder.star.setImageResource(R.drawable.ic_star_grey600_36dp);
        }
        else
            holder.star.setImageResource(R.drawable.ic_star_outline_grey600_36dp);

        holder.star.setOnClickListener(new StarListner(holder));

    }

    public class StarListner implements View.OnClickListener{

        RecyclerViewHolder holder;

        public StarListner(RecyclerViewHolder holder){
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            ImageButton button = (ImageButton)v;
            if (holder.favorite == 1){
                button.setImageResource(R.drawable.ic_star_outline_grey600_36dp);
                holder.favorite = 0;
            }
            else{
                button.setImageResource(R.drawable.ic_star_grey600_36dp);
                holder.favorite = 1;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout cardLayout;
        ImageButton star;
        TextView firstName;
        TextView secondName;
        TextView centralName;
        TextView max;
        TextView min;
        Integer favorite;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            firstName = (TextView) itemView.findViewById(R.id.first_text);
            secondName = (TextView) itemView.findViewById(R.id.second_text);
            centralName = (TextView) itemView.findViewById(R.id.central_text);
            max = (TextView) itemView.findViewById(R.id.max_text);
            min = (TextView) itemView.findViewById(R.id.min_text);
            cardLayout = (RelativeLayout) itemView.findViewById(R.id.relative_card);
            star = (ImageButton) itemView.findViewById(R.id.indices_star);
        }
    }
}
