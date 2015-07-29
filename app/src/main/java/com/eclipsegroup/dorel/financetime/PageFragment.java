package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eclipsegroup.dorel.financetime.models.Index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PageFragment extends Fragment {

    private static final String TAG = PageFragment.class.getSimpleName();

    private static final int NEWS = 0;
    private static final int INDICES = 1;
    private static final int STOCKS = 2;
    private static final int FOREX = 3;
    private static final int COMMODITIES = 4;
    private static final int MAIN_PAGE = 0;
    private static final int FAVORITES = 1;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private TextView textView;
    private Integer pageType; /* Type of tab pressed */
    private Integer fragmentType;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View layout;

        /* Choose the layout to apply */

        if (pageType == null) /* TODO: BETTER CONTROL */
            pageType = 1;

        if(pageType == INDICES){
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

    public List<Index> getData(){

        Index current;
        List<Index> data = new ArrayList<Index>();

        String [] indicesSymbols = {"NASDAQ", "Dow Jones", };
        String [] stocksSymbols = {"GOOG", "YHOO","TWTR","CFG", "BAC", "F", "FB", "AAPL",
                    "T","FNC.MI", "UCG.MI", "UCG.MI", "ENI.MI", "AMZN" };
        String [] forexSymbols = {"EURUSD=X", "GBPEUR=X", "USDJPY=X", "GBPUSD=X",
                    "USDCNY=X", "EURJPY=X", };

        if(pageType == STOCKS){
            for(Integer i=0; i < stocksSymbols.length; i++){

                /* (symbolName, secondName, indexName se c'è se no lascia cosi per ora,
                         currentValue, min, max, growth, percent_growth)  */

                current = Index.setNewIndex(stocksSymbols[i], "Google InC", "NASUSA",
                        "2.0", "32.0", "232.0");

                data.add(current);
            }

        }

        else{
            String[] titles ={"jack", "isss", "under", "the"};
            for(Integer i=0; i < titles.length; i++){
                current = Index.setNewIndex(titles[i], "Google InC", "NASUSA",
                        "2.0", "32.0", "232.0");
                data.add(current);
            }

        }

        return data;
    }

    public static PageFragment getInstance(int position, int fragmentType){

        /* TODO: Control what you return, here you choose the page */

        PageFragment pageFragment = new PageFragment();
        pageFragment.pageType = position;
        pageFragment.fragmentType = fragmentType;

        return pageFragment;
    }
}

