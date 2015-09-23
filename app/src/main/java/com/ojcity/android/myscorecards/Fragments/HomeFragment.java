package com.ojcity.android.myscorecards.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.ojcity.android.myscorecards.Activities.MainActivity;
import com.ojcity.android.myscorecards.Adapters.RVAdapterMatchView;
import com.ojcity.android.myscorecards.SQLiteDAO.DatabaseHandler;
import com.ojcity.android.myscorecards.Model.Match;
import com.ojcity.android.myscorecards.R;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private View myFragmentView;
    private DatabaseHandler dataSource;
    private RecyclerView rv;
    private RVAdapterMatchView adapter;
    private List<Match> matchList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        initializeMatchView();

        // fab here
        // set up fab
        FloatingActionButton fab = (FloatingActionButton) myFragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        fab.hide();

        // check if matchList comes back empty
        if(matchList.size() == 0) {
            // Snackbar to say no matches
            Snackbar.make(myFragmentView, "Please add fighters & matches to score!",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        return myFragmentView;
    }

    private void initializeMatchView() {
        dataSource = new DatabaseHandler( getActivity() );
        dataSource.open();
        rv = (RecyclerView) myFragmentView.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        matchList = dataSource.readMatches();
        dataSource.close();
        adapter = new RVAdapterMatchView(getActivity(), matchList);
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        rv.getAdapter().notifyDataSetChanged();
        initializeMatchView();
        Log.v(TAG, "onResume");
        super.onResume();
    }

}
