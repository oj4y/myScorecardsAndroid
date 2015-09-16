package com.ojcity.android.myscorecards.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ojcity.android.myscorecards.SQLiteDAO.DatabaseHandler;
import com.ojcity.android.myscorecards.Model.Fighter;
import com.ojcity.android.myscorecards.R;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFighterFragment extends Fragment {

    private View myFragmentView;
    private static final String TAG = "AddFighterFragment";
    private DatabaseHandler dataSource;

    public AddFighterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_add_fighter, container, false);

        setFighterEditTextButton();

        return myFragmentView;
    }

    // used in onCreate
    private void setFighterEditTextButton() {
        final EditText fighterNameEditText = (EditText) myFragmentView.findViewById(R.id.add_a_fighter_editText);
        Button addFighterButton = (Button) myFragmentView.findViewById(R.id.add_fighter_button);

        addFighterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fighterNameString =
                                fighterNameEditText.getText().toString().trim();

                        // log it verbose
                        Log.v(TAG, "Fighter Name=" + fighterNameString);

                        if(isEmpty(fighterNameString)) {
                            displaySnackbar("Fighter name can't be empty!");
                            Log.v(TAG, "Fighter Name is empty");
                            return;
                        }

                        // open up db helper
                        dataSource = new DatabaseHandler(getActivity());
                        dataSource.open();

                        // check in db
                        if( dataSource.isFighterAlreadyInDatabase(fighterNameString) ) {
                            displaySnackbar(fighterNameString
                                    + " already exists in database!");
                            return;
                        }

                        // put in db
                        dataSource.createFighter(fighterNameString);
                        dataSource.close();

                        // clear the EditText field
                        fighterNameEditText.setText("");

                        hideSoftKeyboard(getActivity());

                        displaySnackbar(fighterNameString + " inserted in database!");

                    }
                });
    }

    // helper for add button
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    // helper for Add Button
    private boolean isEmpty(String editText) {
        if(editText.matches(""))
            return true;
        return false;
    }

    // helper function for Snackbar
    private void displaySnackbar(String message) {
        Snackbar.make(myFragmentView, message,
                Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
