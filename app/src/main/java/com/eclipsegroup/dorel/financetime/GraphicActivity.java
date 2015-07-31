package com.eclipsegroup.dorel.financetime;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.eclipsegroup.dorel.financetime.R;
import com.eclipsegroup.dorel.financetime.models.Graph;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

import static com.eclipsegroup.dorel.financetime.R.*;

public class GraphicActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Graph graph_data;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_graphic);
        graph_data = new Graph(getIntent().getExtras().getString("INDEX_SYMBOL"));

        toolbar = (Toolbar) findViewById(id.graphic_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(graph_data.getName().toUpperCase());

        toolbar.setNavigationIcon(drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openMainActivity = new Intent(GraphicActivity.this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
            }
        });


        GraphView graph = (GraphView) findViewById(id.graphic_layout);
        //graph.getGridLabelRenderer().setGridColor(color.accentColor);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, -2),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 3),
                new DataPoint(1, 3),
                new DataPoint(2, 6),
                new DataPoint(3, 2),
                new DataPoint(4, 5)
        });
        graph.addSeries(series2);

        // style
        //series.setColor(Color.rgb(255, 120, 120));
        series.setSpacing(50);
    //graph.setDrawingCacheBackgroundColor(color.material_blue_grey_800);
        // legend
        series.setTitle("foo");
        series2.setTitle("bar");

    }
}