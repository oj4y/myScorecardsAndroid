package com.ojcity.android.myscorecards.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.ojcity.android.myscorecards.Model.Match;
import com.ojcity.android.myscorecards.R;
import com.ojcity.android.myscorecards.Activities.ScoreActivity;

/**
 * Created by ojc on 8/13/2015.
 */
public class RVAdapterScoreView extends RecyclerView.Adapter<RVAdapterScoreView.ScoreViewHolder> {

    private static final String TAG = "RVAdapterScoreView";

    private Match match;

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Toolbar roundToolbar;
        NumberPicker fighter1Picker;
        NumberPicker fighter2Picker;

        ScoreViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            roundToolbar = (Toolbar)itemView.findViewById(R.id.round_toolbar);
            fighter1Picker = (NumberPicker)itemView.findViewById(R.id.fighter1_score);
            fighter2Picker = (NumberPicker)itemView.findViewById(R.id.fighter2_score);

//            String[] fighterPickerValues = {Integer.toString(0),
//                    Integer.toString(6),
//                    Integer.toString(7),
//                    Integer.toString(8),
//                    Integer.toString(9),
//                    Integer.toString(10)
//            };

            fighter1Picker.setMaxValue(10);
            fighter1Picker.setMinValue(6);
            fighter1Picker.setWrapSelectorWheel(false);

//            fighter1Picker.setDisplayedValues(fighterPickerValues);

            fighter2Picker.setMaxValue(10);
            fighter2Picker.setMinValue(6);
            fighter2Picker.setWrapSelectorWheel(false);

        }
    }

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private Context context;
    private ScoreActivity activity;

    // constructor
    public RVAdapterScoreView(ScoreActivity scoreActivity, Context context, Match match) {
        this.activity = scoreActivity;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        this.context = context;
        mBackground =  mTypedValue.resourceId;
        this.match = match;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scoreview_item, viewGroup, false);
        return new ScoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ScoreViewHolder scoreViewHolder, final int i) {

        // convert int to String real quick
        String roundToolbar = Integer.toString(i + 1);

        scoreViewHolder.roundToolbar.setTitle("Round " + roundToolbar);

        scoreViewHolder.fighter1Picker.setValue(match.getFighter1Scores().get(i));
        scoreViewHolder.fighter2Picker.setValue(match.getFighter2Scores().get(i));

        scoreViewHolder.fighter1Picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                match.getFighter1Scores().set(i, newVal);
                Log.v(TAG, "Round " + Integer.toString(i + 1) + " fighter1Score=" + Integer.toString(newVal));
                activity.updateTotals();
            }
        });

        scoreViewHolder.fighter2Picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                match.getFighter2Scores().set(i, newVal);
                Log.v(TAG, "Round " + Integer.toString(i + 1) + " fighter2Score=" + Integer.toString(newVal));
                activity.updateTotals();
            }
        });

    }

    @Override
    public int getItemCount() {
        return match.getNumberOfRounds();
    }
}
