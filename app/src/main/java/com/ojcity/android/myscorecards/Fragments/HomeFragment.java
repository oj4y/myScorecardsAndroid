package com.ojcity.android.myscorecards.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private View myFragmentView;
    private DatabaseHandler dataSource;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataSource = new DatabaseHandler( getActivity() );
        dataSource.open();

        myFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

//        RecyclerView rv = (RecyclerView) inflater.inflate(
//                R.layout.fragment_home, container, false);

        RecyclerView rv = (RecyclerView) myFragmentView.findViewById(R.id.recyclerview);

        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        List<Match> matchList = dataSource.readMatches();
        dataSource.close();

        RVAdapterMatchView adapter = new RVAdapterMatchView(getActivity(), matchList);

        rv.setAdapter(adapter);

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

}
