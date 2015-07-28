package com.eclipsegroup.dorel.financetime;

import android.content.Intent;
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

public class GraphicActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        graph = new Graph(getIntent().getExtras().getString("INDEX_SYMBOL"));

        toolbar = (Toolbar)findViewById(R.id.graphic_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(graph.getName().toUpperCase());

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent openMainActivity= new Intent(GraphicActivity.this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
            }
        });
    }
}
