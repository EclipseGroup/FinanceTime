package com.eclipsegroup.dorel.financetime;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.eclipsegroup.dorel.financetime.models.Graph;
import com.eclipsegroup.dorel.financetime.models.Index;
import com.jjoe64.graphview.GraphView;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import static com.eclipsegroup.dorel.financetime.R.*;

public class GraphicActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Graph graph_data;
    private SearchView searchView;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private ArrayList<Index> data = new ArrayList<>();
    private HandleAsynkTask handler;
    private TextView startDate;


    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Exception error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_graphic);

        handler = new HandleAsynkTask();


        graph_data = new Graph(getIntent().getExtras().getString("INDEX_SYMBOL"));

        Search search = new Search();
        search.execute(graph_data.getName(), /*graph_data.getStartPeriod(),graph_data.getEndPeriod()*/"2014-07-01", "2015-07-10");

        toolbar = (Toolbar) findViewById(id.graphic_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(graph_data.getName().toUpperCase());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openMainActivity = new Intent(GraphicActivity.this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
            }
        });
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
            JSONObject json = jsonParser.getJsonFromQuery(queries[0],queries[1],queries[2]);

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

            String symbol, close, date;

            if (array != null){
                for (int i = 0; i < array.length(); i++){
                    try {
                        json = array.getJSONObject(i);
                        symbol = json.getString("Symbol");
                        close = json.getString("Close");
                        date = json.getString("Date");
                        data.add(new Index(close, symbol,date));
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

        public JSONParser() {
        }


        public JSONObject getJsonFromQuery(String symbol, String dateStart, String dateEnd) {


            String YQL = "select * from yahoo.finance.historicaldata where symbol = \"" + symbol + "\" and startDate = \"" + dateStart + "\" and endDate = \"" + dateEnd + "\"";
            String request = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json&env=store://datatables.org/alltableswithkeys&callback=", Uri.encode(YQL));
            try {
                URL url = new URL(request);

                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);

                InputStream inputStream = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                String string = result.toString();
                return new JSONObject(string);

            } catch (Exception e) {
                error = e;
                return null;
            }
        }
    }

    class HandleAsynkTask extends Handler {
        @Override
        public void handleMessage(Message msg){

            String str = msg.getData().getString("myKey");
            /*if (str != null){
                if(str.compareTo("BAD") == 0){
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }*/
            int i;
            Toast.makeText(GraphicActivity.this, str, Toast.LENGTH_SHORT).show();
          //Toast.makeText(GraphicActivity.this, data.get(i).secondName, Toast.LENGTH_SHORT).show();

            if(msg.getData().getString("SEARCH_DONE") != null){
                //progressBar.setVisibility(View.INVISIBLE);

                GraphView graph = (GraphView) findViewById(id.graphic_layout);

                DataPoint[] dp = new DataPoint[data.size()];
                String prova;
                for(i = 0; i < data.size(); i++) {
                    prova = data.get(i).secondName;
                    if (prova != null)
                        dp[i] = (new DataPoint(i, Double.parseDouble(prova)));
                }

                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dp);
                if(series2 != null)
                graph.addSeries(series2);
                series2.setTitle("bar");

               /* if (data.size() != 0){
                    recyclerAdapter = new RecyclerAdapter(GraphicActivity.this, data);
                    recyclerView.setAdapter(recyclerAdapter);
                }
                else
                    Toast.makeText(GraphicActivity.this, "No result found", Toast.LENGTH_LONG).show();*/
            }
        }

    }

    private void setToolbar(){
        toolbar = (Toolbar)findViewById(R.id.search_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMainActivity = new Intent(GraphicActivity.this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
            }
        });
    }

    private void setSearchView(){

        searchView = (SearchView) findViewById(R.id.search_space_activity);

        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressBar.setVisibility(View.VISIBLE);
                data.clear();
                Search search = new Search();
                search.execute(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
}