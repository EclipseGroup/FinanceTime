package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eclipsegroup.dorel.financetime.models.Index;

import java.util.Collections;
import java.util.List;

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

        Index current = data.get(position);
        holder.firstName.setText(current.firstName);
        holder.secondName.setText(current.secondName);
        holder.centralName.setText(current.centralName);
        holder.max.setText("Max " + current.max.toString());
        holder.min.setText("Min  " + current.min.toString());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView firstName;
        TextView secondName;
        TextView centralName;
        TextView max;
        TextView min;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            firstName = (TextView) itemView.findViewById(R.id.first_text);
            secondName = (TextView) itemView.findViewById(R.id.second_text);
            centralName = (TextView) itemView.findViewById(R.id.central_text);
            max = (TextView) itemView.findViewById(R.id.max_text);
            min = (TextView) itemView.findViewById(R.id.min_text);

        }
    }
}
