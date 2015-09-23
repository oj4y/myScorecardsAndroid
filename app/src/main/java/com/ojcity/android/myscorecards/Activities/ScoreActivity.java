package com.ojcity.android.myscorecards.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ojcity.android.myscorecards.Adapters.RVAdapterScoreView;
import com.ojcity.android.myscorecards.Model.Match;
import com.ojcity.android.myscorecards.R;
import com.ojcity.android.myscorecards.SQLiteDAO.DatabaseHandler;

public class ScoreActivity extends AppCompatActivity {

    private static final String TAG = "ScoreActivity";
    private Match ourMatch;
    private TextView mFighter1Total;
    private TextView mFighter2Total;
    private RecyclerView rv;
    private DatabaseHandler dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up top action bar and nav button
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // retrieve from the last scoreActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // get matchId from HomeFragment's RecyclerView Adapter
            int matchId = extras.getInt("matchId");

            // fetch match from database
            dataSource = new DatabaseHandler(this);
            dataSource.open();
            ourMatch = dataSource.readMatch(matchId);
            dataSource.close();

            rv = (RecyclerView) findViewById(R.id.activity_score_rv);

            // init rv
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
            // initialize adapter
            RVAdapterScoreView adapter = new RVAdapterScoreView(ScoreActivity.this, getApplicationContext(), ourMatch);
            rv.setAdapter(adapter);

            rv.getAdapter().notifyDataSetChanged();

            mFighter1Total = (TextView) findViewById(R.id.fighter1_total);
            mFighter2Total = (TextView) findViewById(R.id.fighter2_total);

            ab.setTitle(ourMatch.getFighter1().getName() + " vs. "
                    + ourMatch.getFighter2().getName());

            updateTotals();

        }
    }

    @Override
    public void onPause() {
        rv.getAdapter().notifyDataSetChanged();
        updateTotals();
        dataSource.open();
        dataSource.updateScore(ourMatch);
        dataSource.close();

        super.onPause();
        Log.v(TAG, "onPause");
    }

    public void updateTotals() {
        int fighter1Total = 0;
        int fighter2Total = 0;

        for (int i = 0; i < ourMatch.getNumberOfRounds(); i++) {
            fighter1Total = fighter1Total + ourMatch.getFighter1Scores().get(i);
            fighter2Total = fighter2Total + ourMatch.getFighter2Scores().get(i);
        }

        mFighter1Total.setText(Integer.toString(fighter1Total));
        mFighter2Total.setText(Integer.toString(fighter2Total));
        Log.v(TAG, "fighter1Total= " + Integer.toString(fighter1Total));
        Log.v(TAG, "fighter2Total= " + Integer.toString(fighter2Total));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_score, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent scoreActivity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}