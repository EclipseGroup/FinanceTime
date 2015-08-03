package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eclipsegroup.dorel.financetime.database.Database;
import com.eclipsegroup.dorel.financetime.database.DatabaseHelper;
import com.eclipsegroup.dorel.financetime.models.Index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{

    private LayoutInflater inflater;
    List<Index> data = Collections.emptyList();
    private Context context;
    private DatabaseHelper dbHelper;
    private Database db;
    private Integer pageType;
    private Integer fragmentType;
    private ArrayList<String> symbols;


    public RecyclerAdapter(Context context, List<Index> data, Integer pageType, Integer fragmentType){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        dbHelper = new DatabaseHelper(context);
        db = new Database(dbHelper);
        this.fragmentType = fragmentType;
        if (fragmentType == 0)
            this.pageType = pageType - 1;
        else
            this.pageType = pageType;
    }


    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.index_card, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        Index current = data.get(position);

        if (db.isFavorite(current.firstName)){
            holder.favorite = 1;
            holder.star.setImageResource(R.drawable.ic_star_grey600_36dp);
        }

        else{
            holder.favorite = 0;
            holder.star.setImageResource(R.drawable.ic_star_outline_grey600_36dp);
        }

        holder.firstName.setText(current.firstName);
        holder.secondName.setText(current.secondName);
        holder.currentValue.setText(current.value);
        holder.max.setText("Max " + current.max);
        holder.min.setText("Min  " + current.min);
        holder.growth.setText(current.growth);

        holder.percent_growth.setText(current.percent_growth);
        if (current.color == 0){
            holder.growth.setTextColor(context.getResources().getColor(R.color.down_color));
            holder.percent_growth.setTextColor(context.getResources().getColor(R.color.down_color));
        }
        else{
            holder.growth.setTextColor(context.getResources().getColor(R.color.up_color));
            holder.percent_growth.setTextColor(context.getResources().getColor(R.color.up_color));
        }

        holder.pageType = pageType;

        /* Start the graph activity on click */
        holder.cardLayout.setOnClickListener(new CardListner(holder));
        holder.star.setOnClickListener(new StarListner(holder));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setOnItemRemoved(ArrayList<String> symbols){
        this.symbols = symbols;
    }

    void onItemRemoved(){
        if (fragmentType == 1 ){
            symbols.clear();
            ArrayList<String> list = db.getListType(pageType, fragmentType);
            if (list != null)
                symbols.addAll(list);
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout cardLayout;
        ImageButton star;
        TextView firstName;
        TextView secondName;
        TextView centralName;
        TextView currentValue;
        TextView max;
        TextView min;
        TextView growth;
        TextView percent_growth;

        Integer favorite;
        Integer pageType;


        public RecyclerViewHolder(View itemView) {
            super(itemView);

            firstName = (TextView) itemView.findViewById(R.id.first_text);
            secondName = (TextView) itemView.findViewById(R.id.second_text);
            centralName = (TextView) itemView.findViewById(R.id.central_text);
            max = (TextView) itemView.findViewById(R.id.max_text);
            min = (TextView) itemView.findViewById(R.id.min_text);
            currentValue = (TextView) itemView.findViewById(R.id.value_text);
            cardLayout = (RelativeLayout) itemView.findViewById(R.id.relative_card);
            star = (ImageButton) itemView.findViewById(R.id.indices_star);
            growth = (TextView) itemView.findViewById(R.id.growth_text);
            percent_growth = (TextView) itemView.findViewById(R.id.percent_growth_text);
        }
    }

    public class CardListner implements View.OnClickListener{

        RecyclerViewHolder holder;

        public CardListner(RecyclerViewHolder holder){
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, GraphicActivity.class);
            intent.putExtra("INDEX_SYMBOL", holder.firstName.getText().toString());
            context.startActivity(intent);
        }
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
                db.deleteFavorite(holder.firstName.getText().toString());

                if(fragmentType == 1){
                    data.remove(holder.getLayoutPosition());
                    notifyItemRemoved(holder.getLayoutPosition());
                    onItemRemoved();
                }

            }
            else{
                button.setImageResource(R.drawable.ic_star_grey600_36dp);
                holder.favorite = 1;
                db.insertFavorite(holder.firstName.getText().toString(),
                        holder.pageType, holder.secondName.getText().toString());
            }
        }
    }
}
