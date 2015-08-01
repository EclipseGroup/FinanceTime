package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eclipsegroup.dorel.financetime.database.Database;
import com.eclipsegroup.dorel.financetime.database.DatabaseHelper;
import com.eclipsegroup.dorel.financetime.models.Index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PageFragment extends Fragment implements FinanceServiceCallback{

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
    private Context context;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;
    private Database db;
    private ArrayList<String> symbols;

    Index current;
    ArrayList<Index> data = new ArrayList<Index>();
    private YahooFinanceService service;
    int conta = 0;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(context);
        db = new Database(dbHelper);
        symbols = db.getListType(pageType, fragmentType);
        service = new YahooFinanceService(this, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){


        if(symbols != null && data.isEmpty())
            for(Integer i=0; i < symbols.size(); i++){

               // Toast.makeText(context, "STOCKS" + Integer.toString(i), Toast.LENGTH_SHORT).show();
                service.refreshQuote(symbols.get(i));
            }

        View layout;

        /* Choose the layout to apply */

        if (pageType == null) /* TODO: BETTER CONTROL */
            pageType = 1;

        if(pageType == INDICES){
            layout = inflater.inflate(R.layout.fragment_indices, container, false);
            recyclerView = (RecyclerView) layout.findViewById(R.id.indices_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); /* you want a linear display */
            recyclerAdapter = new RecyclerAdapter(getActivity(), data, pageType, fragmentType);
            recyclerView.setAdapter(recyclerAdapter);

        }
        else{
            layout = inflater.inflate(R.layout.fragment_indices, container, false);
            recyclerView = (RecyclerView) layout.findViewById(R.id.indices_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); /* you want a linear display */
            recyclerAdapter = new RecyclerAdapter(getActivity(), data, pageType, fragmentType);
            recyclerView.setAdapter(recyclerAdapter);
        }

        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primaryColor),
                PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);

        return layout;
    }

    public List<Index> getData(){

        return data;
    }

    public static PageFragment getInstance(int position, int fragmentType, Context context){

        PageFragment pageFragment = new PageFragment();
        pageFragment.pageType = position;
        pageFragment.fragmentType = fragmentType;
        pageFragment.context = context;

        return pageFragment;
    }

    @Override
    public void serviceSuccess(Quote quote) {

        current = Index.setNewIndex( quote.getSymbol(),quote.getName(), quote.getOpen(),
                quote.getLastTrade(), quote.getDaysLow(), quote.getDaysHigh());
        data.add(current);
       // Toast.makeText(getActivity(), "CIAO " + Integer.toString(conta)+ quote.getSymbol() + quote.getName() , Toast.LENGTH_SHORT).show();
        conta++;
        if (conta == symbols.size()){
            recyclerAdapter = new RecyclerAdapter(getActivity(), data, pageType, fragmentType);
            recyclerView.setAdapter(recyclerAdapter);
            conta = 0;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void serviceFailure(Exception exception) {

        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}

