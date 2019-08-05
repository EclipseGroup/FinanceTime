package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eclipsegroup.dorel.financetime.database.Database;
import com.eclipsegroup.dorel.financetime.database.DatabaseHelper;
import com.eclipsegroup.dorel.financetime.models.Index;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class PageFragment extends Fragment{

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
    private HandleAsynkTask handler;
    private Exception error;
    private ArrayList<Index> data = new ArrayList<Index>();

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

        handler = new HandleAsynkTask();

        dbHelper = new DatabaseHelper(getActivity());
        db = new Database(dbHelper);
        symbols = db.getListType(pageType, fragmentType);

        if (symbols.size()!= 0 && data.isEmpty()){
            Search search = new Search();
            search.execute();
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

        progressBar.setVisibility(View.INVISIBLE);

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

    public static PageFragment getInstance(int position, int fragmentType, Context context) {

        PageFragment pageFragment = new PageFragment();
        pageFragment.pageType = position;
        pageFragment.fragmentType = fragmentType;
        pageFragment.context = context;

        return pageFragment;
    }

    private void sendMessage(String string){
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("myKey", string);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void signalSearchDone(){
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_DONE", "");
        msg.setData(bundle);
        handler.sendMessage(msg);

    }

    class Search extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... queries) {

            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.getJsonFromQuery();

            try {
                if (json != null){
                    json = json.getJSONObject("query").getJSONObject("results");
                }
                else return null;

            } catch (JSONException e) {
                return null;
            }

            JSONArray array;
            try {
                array = json.getJSONArray("quote");
            } catch (JSONException e) {
                return null;
            }

            String symbol, name, open, price, min, max;

            if (array != null){
                for (int i = 0; i < array.length(); i++){
                    try {
                        json = array.getJSONObject(i);
                        symbol = json.getString("symbol");
                        name = json.getString("Name");
                        open = json.getString("Open");
                        price = json.getString("LastTradePriceOnly");
                        min = json.getString("DaysLow");
                        max = json.getString("DaysHigh");

                        data.add(new Index(symbol, name, open, price, min, max));
                    } catch (JSONException e) {
                        return null;
                    }
                }
                signalSearchDone();
            }

            return "";
        }
        @Override
        protected void onPostExecute(String result){

            if (result == null)
                sendMessage("BAD");
        }
    }

    class JSONParser {

        InputStream in = null;

        public JSONParser(){
        }

        public  JSONObject getJsonFromQuery() {

            String YQL = "select * from yahoo.finance.quotes where symbol in (";
            for(int i = 0; i < symbols.size(); i++){
                YQL += String.format("\"%s\",",symbols.get(i));
            }
            YQL = YQL.substring(0,YQL.length()-1);
            YQL += ")";
            String request = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json&env=store://datatables.org/alltableswithkeys&callback=", Uri.encode(YQL));

            URL url;
            try {
                url = new URL(request);

                URLConnection urlConnection = url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                String string = responseStrBuilder.toString();

                return new JSONObject(string);

            }catch (Exception e) {
                error = e;
                return null;
            }

        }
    }

    class HandleAsynkTask extends Handler {
        @Override
        public void handleMessage(Message msg){

            String str = msg.getData().getString("myKey");
            if (str != null){
                if(str.compareTo("BAD") == 0){
                    progressBar.setVisibility(View.INVISIBLE);
                }
                Toast.makeText(getActivity(), str , Toast.LENGTH_SHORT).show();
            }

            if(msg.getData().getString("SEARCH_DONE") != null){
                progressBar.setVisibility(View.INVISIBLE);
                swipe.setRefreshing(false);

                if (data.size() != 0){
                    recyclerAdapter = new RecyclerAdapter(getActivity(), data);
                    recyclerView.setAdapter(recyclerAdapter);
                }
                else
                    Toast.makeText(getActivity(), "No result found", Toast.LENGTH_LONG).show();
            }
        }

    }

    class OnRefresh implements SwipeRefreshLayout.OnRefreshListener {

        public OnRefresh(){
        }

        @Override
        public void onRefresh() {
                if(data.size() == symbols.size() && symbols.size() != 0){
                    data.clear();
                    Search search = new Search();
                    search.execute();
                }
            else
                swipe.setRefreshing(false);
        }
    }
}

