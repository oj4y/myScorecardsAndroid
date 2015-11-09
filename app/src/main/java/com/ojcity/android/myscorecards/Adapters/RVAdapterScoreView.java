package com.ojcity.android.myscorecards.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ojcity.android.myscorecards.Model.Match;
import com.ojcity.android.myscorecards.R;
import com.ojcity.android.myscorecards.Activities.ScoreActivity;

/**
 * Created by ojc on 8/13/2015.
 */
public class RVAdapterScoreView extends RecyclerView.Adapter<RVAdapterScoreView.ScoreViewHolder> {

    private static final String TAG = "RVAdapterScoreView";
    private final TypedValue mTypedValue = new TypedValue();
    private Match match;
    private int mBackground;
    private Context context;
    private ScoreActivity activity;
    private boolean[] scoreAdjusted;
    private NumberPicker fighter1Picker;
    private NumberPicker fighter2Picker;
    // constructor
    public RVAdapterScoreView(ScoreActivity scoreActivity, Context context, Match match) {
        this.activity = scoreActivity;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        this.context = context;
        mBackground = mTypedValue.resourceId;
        this.match = match;
        this.scoreAdjusted = new boolean[match.getNumberOfRounds()];
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scoreview_item, viewGroup, false);
        // TODO:
        // new layout
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scoreview_item_nonedit, viewGroup, false);
        return new ScoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ScoreViewHolder scoreViewHolder, final int i) {

        // convert int to String real quick
        String roundToolbar = Integer.toString(i + 1);

        scoreViewHolder.roundToolbar.setTitle("Round " + roundToolbar);

        scoreViewHolder.fighter1Radio.setText(match.getFighter1().getName());
        scoreViewHolder.fighter2Radio.setText(match.getFighter2().getName());

        scoreViewHolder.fighter1RoundScoreTextView.setText(match.getFighter1Scores().get(i).toString());
        scoreViewHolder.fighter2RoundScoreTextView.setText(match.getFighter2Scores().get(i).toString());

        setRadioButtons(scoreViewHolder, i);

        scoreViewHolder.fighter1Radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                match.getFighter1Scores().set(i, 10);
                match.getFighter2Scores().set(i, 9);
                scoreViewHolder.fighter1RoundScoreTextView.setText(match.getFighter1Scores().get(i).toString());
                scoreViewHolder.fighter2RoundScoreTextView.setText(match.getFighter2Scores().get(i).toString());
                activity.updateTotals();
            }
        });

        scoreViewHolder.fighter2Radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                match.getFighter2Scores().set(i, 10);
                match.getFighter1Scores().set(i, 9);
                scoreViewHolder.fighter1RoundScoreTextView.setText(match.getFighter1Scores().get(i).toString());
                scoreViewHolder.fighter2RoundScoreTextView.setText(match.getFighter2Scores().get(i).toString());
                activity.updateTotals();
            }
        });

        scoreViewHolder.tieRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                match.getFighter1Scores().set(i, 9);
                match.getFighter2Scores().set(i, 9);
                scoreViewHolder.fighter1RoundScoreTextView.setText(match.getFighter1Scores().get(i).toString());
                scoreViewHolder.fighter2RoundScoreTextView.setText(match.getFighter2Scores().get(i).toString());
                activity.updateTotals();
            }
        });

        scoreViewHolder.tieRadio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                match.getFighter1Scores().set(i, 9);
                match.getFighter2Scores().set(i, 9);
                displayEditScore(i, scoreViewHolder);
                return true;
            }
        });

        scoreViewHolder.roundToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit_score:
                        displayEditScore(i, scoreViewHolder);
                        break;
                    case R.id.action_mark_stoppage:
                        //
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

    }

    private void displayEditScore(final int i, final ScoreViewHolder scoreViewHolder) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .title("Edit Score")
                .customView(R.layout.scorepicker, false)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        match.getFighter1Scores().set(i, fighter1Picker.getValue());
                        match.getFighter2Scores().set(i, fighter2Picker.getValue());
                        scoreViewHolder.fighter1RoundScoreTextView.setText(match.getFighter1Scores().get(i).toString());
                        scoreViewHolder.fighter2RoundScoreTextView.setText(match.getFighter2Scores().get(i).toString());
                        setRadioButtons(scoreViewHolder, i);
                        activity.updateTotals();
                        Log.v(TAG, "Positive onClick");
                    }
                })
                .build();

        fighter1Picker = (NumberPicker) materialDialog.getCustomView().findViewById(R.id.fighter1_picker);
        fighter1Picker.setMaxValue(10);
        fighter1Picker.setMinValue(6);
        fighter1Picker.setWrapSelectorWheel(false);
        fighter1Picker.setValue(match.getFighter1Scores().get(i));

        fighter2Picker = (NumberPicker) materialDialog.getCustomView().findViewById(R.id.fighter2_picker);
        fighter2Picker.setMaxValue(10);
        fighter2Picker.setMinValue(6);
        fighter2Picker.setWrapSelectorWheel(false);
        fighter2Picker.setValue(match.getFighter2Scores().get(i));

        materialDialog.show();
    }

    private void setRadioButtons(ScoreViewHolder scoreViewHolder, int i) {
        // set radio buttons to score
        if (match.getFighter1Scores().get(i) > match.getFighter2Scores().get(i))
            scoreViewHolder.radioGroup.check(R.id.fighter1_radio);
        else if (match.getFighter1Scores().get(i) < match.getFighter2Scores().get(i))
            scoreViewHolder.radioGroup.check(R.id.fighter2_radio);
        else if (match.getFighter1Scores().get(i) == match.getFighter2Scores().get(i)
                && match.getFighter1Scores().get(i) == 0
                && match.getFighter2Scores().get(i) == 0)
            scoreViewHolder.radioGroup.clearCheck();
        else { // tie, not zero
            scoreViewHolder.radioGroup.check(R.id.tie_radio);
        }
    }

    @Override
    public int getItemCount() {
        return match.getNumberOfRounds();
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Toolbar roundToolbar;
        TextView fighter1TextView;
        TextView fighter2TextView;
        TextView fighter1RoundScoreTextView;
        TextView fighter2RoundScoreTextView;
        RadioGroup radioGroup;
        RadioButton fighter1Radio;
        RadioButton fighter2Radio;
        RadioButton tieRadio;

        ScoreViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            roundToolbar = (Toolbar) itemView.findViewById(R.id.round_toolbar);
            roundToolbar.inflateMenu(R.menu.menu_score_card);
            fighter1TextView = (TextView) itemView.findViewById(R.id.fighter1_score_tv);
            fighter2TextView = (TextView) itemView.findViewById(R.id.fighter2_score_tv);

            fighter1RoundScoreTextView = (TextView) itemView.findViewById(R.id.fighter1_score);
            fighter2RoundScoreTextView = (TextView) itemView.findViewById(R.id.fighter2_score);

            radioGroup = (RadioGroup) itemView.findViewById(R.id.radio_group);
            fighter1Radio = (RadioButton) itemView.findViewById(R.id.fighter1_radio);
            fighter2Radio = (RadioButton) itemView.findViewById(R.id.fighter2_radio);
            tieRadio = (RadioButton) itemView.findViewById(R.id.tie_radio);

        }
    }
}
