package com.eclipsegroup.dorel.financetime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eclipsegroup.dorel.financetime.models.Index;

import java.util.ArrayList;
import java.util.List;

public class PageFragment extends Fragment {

    private static final String TAG = PageFragment.class.getSimpleName();

    private static final int NEWS = 0;
    private static final int INDICES = 1;
    private static final int STOCKS = 2;
    private static final int FOREX = 3;
    private static final int COMMODITIES = 4;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private TextView textView;
    private Integer fragmentType; /* Type of tab pressed */


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View layout;

        /* Choose the layout to apply */

        if (fragmentType == null) /* TODO: BETTER CONTROL */
            fragmentType = 1;

        if(fragmentType == INDICES){
            layout = inflater.inflate(R.layout.fragment_indices, container, false);
            recyclerView = (RecyclerView) layout.findViewById(R.id.indices_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); /* you want a linear display */
            recyclerAdapter = new RecyclerAdapter(getActivity(), getData());
            recyclerView.setAdapter(recyclerAdapter);

        }
        else{
            layout = inflater.inflate(R.layout.fragment_indices, container, false);
            recyclerView = (RecyclerView) layout.findViewById(R.id.indices_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); /* you want a linear display */
            recyclerAdapter = new RecyclerAdapter(getActivity(), getData());
            recyclerView.setAdapter(recyclerAdapter);
        }

        return layout;
    }

    public static List<Index> getData(){

        Index current;
        List<Index> data = new ArrayList<Index>();
        /* Prepare list */

        String[] titles ={"jack", "isss", "under", "the"};
        for(Integer i=0; i < titles.length; i++){
            current = Index.setNewIndex(titles[i], "Google InC", "NASUSA",
                    2.0, 32.0, 232.0, 0.55, 0.33);
            data.add(current);
        }

        return data;
    }

    public static PageFragment getInstance(int position){

        /* TODO: Control what you return, here you choose the page */

        PageFragment pageFragment = new PageFragment();
        pageFragment.fragmentType = position;

        Log.d(TAG, "new one" + Integer.toString(position));

        return pageFragment;
    }
}

