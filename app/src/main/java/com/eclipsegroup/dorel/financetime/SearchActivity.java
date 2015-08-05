package com.eclipsegroup.dorel.financetime;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.eclipsegroup.dorel.financetime.models.Index;


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
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String query;
    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<Index> data = new ArrayList<>();
    private HandleAsynkTask handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handler = new HandleAsynkTask();

        query = getIntent().getExtras().getString("SEARCH_STRING");

        setToolbar();
        setSearchView();

        progressBar = (ProgressBar) findViewById(R.id.progress_search);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primaryColor),
                PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.indices_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Search search = new Search();
        search.execute(query);
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

    class Search extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... queries) {

            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.getJsonFromQuery(queries[0]);

            try {
                if (json != null){
                    json = json.getJSONObject("ResultSet");
                }
                else return null;

            } catch (JSONException e) {
                return null;
            }

            JSONArray array;
            try {
                array = json.getJSONArray("Result");
            } catch (JSONException e) {
                return null;
            }

            String symbol, name, central_name;

            if (array != null){
                for (int i = 0; i < array.length(); i++){
                    try {
                        json = array.getJSONObject(i);
                        symbol = json.getString("symbol");
                        name = json.getString("name");
                        central_name = json.getString("exchDisp");
                        data.add(new Index(symbol, name, central_name));
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

        public  JSONObject getJsonFromQuery(String query) {

            try {
                query = URLEncoder.encode(query, "UTF-8").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                return null;
            }

            String request = "http://autoc.finance.yahoo.com/autoc?query=" + query +
                    "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";

            URL url;
            try {
                url = new URL(request);
            }
            catch (MalformedURLException e) {
                return null;
            }

            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            catch (IOException e) {
                return null;
            }

            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
                urlConnection.disconnect();

            } catch (IOException e) {
                urlConnection.disconnect();
                return null;
            }

            BufferedReader streamReader;
            try {
                streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return null;
            }
            StringBuilder responseStrBuilder = new StringBuilder();


            String inputStr;
            try {
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
            } catch (IOException e) {
                return null;
            }

            try {
                String string = responseStrBuilder.toString();
                Integer end = string.length() - 1;
                string = string.substring(39, end);
                return new JSONObject(string);
            } catch (JSONException e) {
                return null;
            }

        }
    }

    class HandleAsynkTask extends Handler{
        @Override
        public void handleMessage(Message msg){

            String str = msg.getData().getString("myKey");
            if (str != null){
                if(str.compareTo("BAD") == 0){
                    progressBar.setVisibility(View.INVISIBLE);
                }
                Toast.makeText(SearchActivity.this, str , Toast.LENGTH_SHORT).show();
            }

            if(msg.getData().getString("SEARCH_DONE") != null){
                progressBar.setVisibility(View.INVISIBLE);

                if (data.size() != 0){
                    recyclerAdapter = new RecyclerAdapter(SearchActivity.this, data);
                    recyclerView.setAdapter(recyclerAdapter);
                }
                else
                    Toast.makeText(SearchActivity.this, "No result found", Toast.LENGTH_LONG).show();
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
                Intent openMainActivity = new Intent(SearchActivity.this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
            }
        });
    }

    private void setSearchView(){

        searchView = (SearchView) findViewById(R.id.search_space_activity);
        searchView.setQuery(query, true);
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
