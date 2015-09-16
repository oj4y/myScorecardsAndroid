package com.ojcity.android.myscorecards.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.ojcity.android.myscorecards.SQLiteDAO.DatabaseHandler;
import com.ojcity.android.myscorecards.Model.Fighter;
import com.ojcity.android.myscorecards.Model.Match;
import com.ojcity.android.myscorecards.R;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;


public class AddMatchesFragment extends Fragment {

    private View myFragmentView;
    private Fighter fighter1;
    private Fighter fighter2;
    private DatabaseHandler dataSource;
    private static final String TAG = "AddMatchesFragment";

    public AddMatchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_add_matches, container, false);

        dataSource = new DatabaseHandler( getActivity() );
        dataSource.open();

        // put adapters in spinners
        String[] allFighters = getAllFightersFromDb();
        final ArrayAdapter<String> fightersAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, allFighters);

        final MaterialSpinner aSideSpinner = (MaterialSpinner) myFragmentView.findViewById(R.id.a_side_spinner);
        aSideSpinner.setAdapter(fightersAdapter);
        final MaterialSpinner bSideSpinner = (MaterialSpinner) myFragmentView.findViewById(R.id.b_side_spinner);
        bSideSpinner.setAdapter(fightersAdapter);

        Integer[] possibleNumberOfRounds = {3, 5, 10, 12, 15};

        final ArrayAdapter<Integer> roundAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_dropdown_item, possibleNumberOfRounds);

        final MaterialSpinner roundSpinner = (MaterialSpinner) myFragmentView.findViewById(R.id.round_spinner);
        roundSpinner.setAdapter(roundAdapter);

        final Button addMatchButton = (Button) myFragmentView.findViewById(R.id.score_button);

        addMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get fighters from fields
                String fighter1Name = aSideSpinner.getSelectedItem().toString();
                String fighter2Name = bSideSpinner.getSelectedItem().toString();
                Log.v(TAG, "fighter1Name=" + fighter1Name);
                Log.v(TAG, "fighter2Name=" + fighter2Name);

                // check if spinners are filled out
                if(aSideSpinner.getHint().toString() == fighter1Name) {
                    aSideSpinner.setError("Select a Fighter");
                    return;
                }
                if(bSideSpinner.getHint().toString() == fighter2Name) {
                    bSideSpinner.setError("Select a Fighter");
                    return;
                }

                // check if fighters are the same
                if(fighter1Name.equals(fighter2Name)) {
                    aSideSpinner.setError("I can't fight myself!");
                    bSideSpinner.setError("I can't fight myself!");
                    return;
                }

                // get rounds as string to check
                String numberOfRounds = roundSpinner.getSelectedItem().toString();
                Log.v(TAG, numberOfRounds);

                // check if rounds was filled
                if(numberOfRounds.matches("scheduled for")) {
                    roundSpinner.setError("Pick the number of rounds");
                    return;
                }

                // get rounds for real this time
                int numberOfRoundsInt = Integer.parseInt(numberOfRounds);
                Log.v(TAG, String.valueOf(numberOfRoundsInt));

                // create the new fighter object
                fighter1 = new Fighter(fighter1Name);
                fighter2 = new Fighter(fighter2Name);

                // read fighter id from database
                int fighter1Id = dataSource.getFighterId(fighter1.getName());
                int fighter2Id = dataSource.getFighterId(fighter2.getName());

                // set fighter id
                fighter1.setId(fighter1Id);
                fighter2.setId(fighter2Id);

                // get rounds from field
                Match newMatch = new Match(fighter1,fighter2);

                // set number of rounds in  match
                newMatch.setNumberOfRounds(numberOfRoundsInt);

                // add to match database
                dataSource.createMatch(newMatch);

                // clear spinners after
                aSideSpinner.setAdapter(fightersAdapter);
                bSideSpinner.setAdapter(fightersAdapter);
                roundSpinner.setAdapter(roundAdapter);

                Snackbar.make(myFragmentView, fighter1Name + " vs. " + fighter2Name + " added!",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });

        return myFragmentView;
    }

    private String[] getAllFightersFromDb() {
        List<Fighter> fighterList = dataSource.readAllFighters();
        int fighterListCount = fighterList.size();

        // start an array for the fighters found from database
        String[] arrayOfFighters = new String[fighterListCount];
        int x = 0;

        // each entry in fighterList, put in array
        for(Fighter entry : fighterList) {
            arrayOfFighters[x] = entry.getName();
            x++;
        }

        return arrayOfFighters;
    }

}
