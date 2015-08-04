package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.List;


public class PageFragment extends Fragment implements FinanceServiceCallback {

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
    private Integer pageType;
    private Integer fragmentType;
    private Context context;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;
    private Database db;
    private ArrayList<String> symbols = new ArrayList<String>();
    private SwipeRefreshLayout swipe;

    Index current;
    ArrayList<Index> data = new ArrayList<Index>();
    private YahooFinanceService service;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("pageType", pageType);
        savedInstanceState.putInt("fragmentType", fragmentType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            pageType = savedInstanceState.getInt("pageType");
            fragmentType = savedInstanceState.getInt("fragmentType");
        }

        dbHelper = new DatabaseHelper(getActivity());
        db = new Database(dbHelper);
        symbols = db.getListType(pageType, fragmentType);
        service = new YahooFinanceService(this, getActivity());

        if (symbols.size()!= 0 && data.isEmpty())
            for (Integer i = 0; i < symbols.size(); i++) {
                service.refreshQuote(symbols.get(i));
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout;

        layout = inflater.inflate(R.layout.fragment_page, container, false);

        swipe = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) layout.findViewById(R.id.indices_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); /* you want a linear display */
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primaryColor),
                PorterDuff.Mode.SRC_IN);


        swipe.setOnRefreshListener(new OnRefresh());

        if (symbols.size() != 0){
            if (symbols.size() > data.size())
                progressBar.setVisibility(View.VISIBLE);

            if(data.size() == symbols.size()){
                recyclerAdapter = new RecyclerAdapter(getActivity(), data, pageType, fragmentType);
                recyclerView.setAdapter(recyclerAdapter);
                progressBar.setVisibility(View.INVISIBLE);
                recyclerAdapter.setOnItemRemoved(symbols);
            }
        }

        return layout;
    }

    public List<Index> getData() {

        return data;
    }

    public static PageFragment getInstance(int position, int fragmentType, Context context) {

        PageFragment pageFragment = new PageFragment();
        pageFragment.pageType = position;
        pageFragment.fragmentType = fragmentType;
        pageFragment.context = context;

        return pageFragment;
    }

    @Override
    public void serviceSuccess(Quote quote) {

        current = Index.setNewIndex(quote.getSymbol(), quote.getName(), quote.getOpen(),
                quote.getLastTrade(), quote.getDaysLow(), quote.getDaysHigh());
        data.add(current);
        // Toast.makeText(getActivity(), "CIAO " + Integer.toString(conta)+ quote.getSymbol() + quote.getName() , Toast.LENGTH_SHORT).show();
        if (data.size() == symbols.size()) {
            recyclerAdapter = new RecyclerAdapter(getActivity(), data, pageType, fragmentType);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.setOnItemRemoved(symbols);

            progressBar.setVisibility(View.INVISIBLE);
            swipe.setRefreshing(false);
        }
    }

    @Override
    public void serviceFailure(Exception exception) {

        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    class OnRefresh implements SwipeRefreshLayout.OnRefreshListener {

        public OnRefresh(){

        }

        @Override
        public void onRefresh() {
                if(data.size() == symbols.size() && symbols.size() != 0){
                    data.clear();
                    for (Integer i = 0; i < symbols.size(); i++) {
                        service.refreshQuote(symbols.get(i));
                    }
                }
            else
                swipe.setRefreshing(false);
        }
    }
}

