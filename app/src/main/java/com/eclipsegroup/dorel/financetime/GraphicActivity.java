package com.eclipsegroup.dorel.financetime;

import android.content.Intent;
import android.graphics.PorterDuff;
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
import com.eclipsegroup.dorel.financetime.models.GraphElement;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static com.eclipsegroup.dorel.financetime.R.id;
import static com.eclipsegroup.dorel.financetime.R.layout;

public class GraphicActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;
    private Graph graph_data;
    private SearchView searchView;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private ArrayList<GraphElement> data = new ArrayList<>();
    private HandleAsynkTask handler;
    private TextView startDateView;
    private TextView endDateView;
    private String strStartDate;
    private String strEndDate;
    private SimpleDateFormat formatter;
    private GraphView open_graph;
    private GraphView volume_graph;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Exception error;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_graphic);

        handler = new HandleAsynkTask();
        symbol = getIntent().getExtras().getString("INDEX_SYMBOL");

        open_graph = (GraphView) findViewById(id.open_graphic_layout);
        volume_graph = (GraphView) findViewById(id.volume_graphic_layout);
        startDateView = (TextView) findViewById(R.id.start_date);
        endDateView = (TextView) findViewById(R.id.end_date);

        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();

        Date todayDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -20);
        Date firstDate = calendar.getTime();

        strStartDate = formatter.format(firstDate);
        strEndDate = formatter.format(todayDate);

        endDateView.setText(strEndDate);
        startDateView.setText(strStartDate);

        endDateView.setOnClickListener(new DatePick("endDate"));
        startDateView.setOnClickListener(new DatePick("startDate"));

        progressBar = (ProgressBar) findViewById(R.id.progress_graphic);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primaryColor),
                PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);

        Downloader downloader = new Downloader();
        downloader.execute(symbol, strStartDate, strEndDate);

        toolbar = (Toolbar) findViewById(id.graphic_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(symbol.toUpperCase());

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

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(i, i1, i2);
        Date date = null;

        if (datePickerDialog.getTag().compareTo("startDate") == 0) {
            try {
                date = formatter.parse(strStartDate);
                strStartDate = formatter.format(calendar.getTime());
                startDateView.setText(strStartDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (datePickerDialog.getTag().compareTo("endDate") == 0) {
            try {
                date = formatter.parse(strEndDate);
                strEndDate = formatter.format(calendar.getTime());
                endDateView.setText(strEndDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        assert date != null;
        if (date.compareTo(calendar.getTime()) != 0) {
            data.clear();
            progressBar.setVisibility(View.VISIBLE);
            Downloader downloader = new Downloader();
            downloader.execute(symbol, strStartDate, strEndDate);
        }
    }

    class Downloader extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... queries) {

            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.getJsonFromQuery(queries[0], queries[1], queries[2]);

            try {
                if (json != null) {
                    json = json.getJSONObject("query").getJSONObject("results");
                } else return null;

            } catch (JSONException e) {
                return null;
            }

            JSONArray array;
            try {
                array = json.getJSONArray("quote");
            } catch (JSONException e) {
                return null;
            }

            String volume, close, date, open, min, max;

            if (array != null) {
                for (int i = array.length() - 1; i >= 0; i--) {
                    try {
                        json = array.getJSONObject(i);
                        date = json.getString("Date");
                        close = json.getString("Close");
                        open = json.getString("Open");
                        min = json.getString("Low");
                        max = json.getString("High");
                        volume = json.getString("Volume");

                        data.add(new GraphElement(date, open, close, min, max, volume));
                    } catch (JSONException e) {
                        return null;
                    }
                }
                signalSearchDone();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            if (result == null)
                sendMessage("BAD");
        }
    }

    class JSONParser {

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
    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }
    class HandleAsynkTask extends Handler {
        @Override
        public void handleMessage(Message msg){

            int i;

            String str = msg.getData().getString("myKey");
            if (str != null) {
                if (str.compareTo("BAD") == 0) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(GraphicActivity.this, str, Toast.LENGTH_SHORT).show();
                }
            }

            if (msg.getData().getString("SEARCH_DONE") != null) {
                progressBar.setVisibility(View.INVISIBLE);
                open_graph.removeAllSeries();
                volume_graph.removeAllSeries();


                Double min = Double.parseDouble(data.get(0).open);
                Double max = Double.parseDouble(data.get(0).open);
                Double volumeMax = (Double.parseDouble(data.get(0).volume)/1000000.0);

                String open, strdate, volume;
                Date date = null;
                DataPoint[] dp = new DataPoint[data.size()];
                DataPoint[] volumePoints = new DataPoint[data.size()];
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                long d = 0;
                Calendar calendar = Calendar.getInstance();
                Date d1 = calendar.getTime();

                for(i = 0; i < data.size(); i++) {
                    open = data.get(i).open;
                    strdate = data.get(i).date;
                    volume = data.get(i).volume;

                    try {
                        date = format.parse(strdate);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    assert date != null;
                    d = date.getTime();

                    if (open != null){
                        volumePoints[i] = (new DataPoint(d, Double.parseDouble(volume)/1000000.0));
                        dp[i] = (new DataPoint(d, Double.parseDouble(open)));
                        if (Double.parseDouble(open) > max)
                            max = Double.parseDouble(open);
                        if(Double.parseDouble(open) < min)
                            min = Double.parseDouble(open);
                        if(Double.parseDouble(volume)/1000000.0 > volumeMax)
                            volumeMax = Double.parseDouble(volume)/1000000.0;
                    }

                }

                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(dp);
                BarGraphSeries<DataPoint> volumeSeries = new BarGraphSeries<>(volumePoints);


                open_graph.addSeries(series2);
                series2.setTitle("bar");


                open_graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphicActivity.this));
                open_graph.getGridLabelRenderer().setNumHorizontalLabels(3);

                try {
                    d = format.parse(data.get(0).date).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                open_graph.getViewport().setMinX(d);
                long start = d;

                try {
                    d = format.parse(data.get(data.size() - 1).date).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Toast.makeText(GraphicActivity.this, "Nr. elem: " + Integer.toString(data.size()), Toast.LENGTH_SHORT).show();
                open_graph.getViewport().setMaxX(d);
                open_graph.getViewport().setXAxisBoundsManual(true);
                open_graph.getViewport().setMinY(min * 0.95);
                open_graph.getViewport().setMaxY(max * 1.05);
                open_graph.getViewport().setYAxisBoundsManual(true);

                volume_graph.addSeries(volumeSeries);
                volumeSeries.setTitle("Volume");

                volume_graph.getGridLabelRenderer().setNumHorizontalLabels(3);
                volume_graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphicActivity.this));
                volume_graph.getViewport().setMaxX(d);
                volume_graph.getViewport().setMinX(start);
                volume_graph.getViewport().setMaxY(volumeMax);
                volume_graph.getViewport().setYAxisBoundsManual(true);
                volume_graph.getViewport().setXAxisBoundsManual(true);
            }
        }

    }

    class DatePick implements View.OnClickListener {

        String type;

        public DatePick(String type) {
            this.type = type;
        }

        @Override
        public void onClick(View v) {

            Calendar calendar = Calendar.getInstance();
            if (type.compareTo("endDate") == 0) {
                try {
                    calendar.setTime(formatter.parse(strEndDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    calendar.setTime(formatter.parse(strStartDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    GraphicActivity.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getFragmentManager(), type);
        }
    }
}