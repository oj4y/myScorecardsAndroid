package com.ojcity.android.myscorecards.Fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ojcity.android.myscorecards.Model.Fighter;
import com.ojcity.android.myscorecards.R;
import com.ojcity.android.myscorecards.SQLiteDAO.DatabaseHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteFighterFragment extends Fragment {

    private View myFragmentView;
    private DatabaseHandler dataSource;
    private static final String TAG = "DeleteFighterFragment";

    public DeleteFighterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_delete_fighter, container, false);

        // set spinner
        final MaterialSpinner deleteFighterSpinner = (MaterialSpinner) myFragmentView.findViewById(R.id.delete_a_fighter_spinner);

        // get all fighters from database
        final ArrayList<String> allFightersList = getAllFightersList();

        // put in adapter
        final ArrayAdapter<String> fightersAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_dropdown_item, allFightersList);

        // set adapter to spinner
        deleteFighterSpinner.setAdapter(fightersAdapter);

        // set delete button
        Button deleteFighterButton = (Button) myFragmentView.findViewById(R.id.delete_fighter_button);
        deleteFighterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameToDelete = deleteFighterSpinner.getSelectedItem().toString();

                if(nameToDelete.matches("Saved Fighters")) {
                    deleteFighterSpinner.setError("Select a Fighter");
                    return;
                }

                MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                        .title("Delete this fighter?")
                        .content("This will not only delete " + nameToDelete + ", but all matches he is involved in." )
                        .positiveText("Ok")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                dataSource = new DatabaseHandler(getContext());
                                dataSource.open();
                                dataSource.deleteFighter(nameToDelete);
                                dataSource.close();

                                displaySnackbar(nameToDelete + " deleted");

                                // get updated list after deletion
                                fightersAdapter.remove(nameToDelete);
                                fightersAdapter.notifyDataSetChanged();
                                deleteFighterSpinner.setAdapter(fightersAdapter);

                                Log.v(TAG, "onPositive");
                            }
                        })
                        .show();
            }
        });

        return myFragmentView;

    }

    // helper to get all fighters from db
    private ArrayList<String> getAllFightersList() {
        dataSource = new DatabaseHandler(getContext());
        dataSource.open();

        List<Fighter> fighterList = dataSource.readAllFighters();
        dataSource.close();

        int fighterListCount = fighterList.size();

        // start an array for the fighters found from database
        ArrayList<String> arrayOfFighters = new ArrayList<>(fighterListCount);
        int x = 0;

        // each entry in fighterList, put in array
        for(Fighter entry : fighterList) {
            arrayOfFighters.add(entry.getName());
            x++;
        }

        return arrayOfFighters;
    }

    // helper function for Snackbar
    private void displaySnackbar(String message) {
        Snackbar.make(myFragmentView, message,
                Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
