package com.ojcity.android.myscorecards.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ojcity.android.myscorecards.Model.Fighter;
import com.ojcity.android.myscorecards.SQLiteDAO.DatabaseHandler;
import com.ojcity.android.myscorecards.Model.Match;
import com.ojcity.android.myscorecards.R;
import com.ojcity.android.myscorecards.Activities.ScoreActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ojc on 5/27/2015.
 */
public class RVAdapterMatchView extends RecyclerView.Adapter<RVAdapterMatchView.MatchViewHolder> {

    private static final String TAG = "RVAdapterMatchView";

    public static class MatchViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView fighter1Name;
        TextView fighter2Name;
        TextView date;
        ImageView image1;
        ImageView image2;
        TextView fighter1Score;
        TextView fighter2Score;

            // constructor
        MatchViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            fighter1Name = (TextView)itemView.findViewById(R.id.fighter1_name);
            fighter2Name = (TextView)itemView.findViewById(R.id.fighter2_name);
            date = (TextView)itemView.findViewById(R.id.date);
            image1 = (ImageView) itemView.findViewById(R.id.fighter1_tile);
            image2 = (ImageView) itemView.findViewById(R.id.fighter2_tile);
            fighter1Score = (TextView)itemView.findViewById(R.id.fighter1_score);
            fighter2Score = (TextView)itemView.findViewById(R.id.fighter2_score);
        }

    }

    private Context context;
    private List<Match> matches;
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private DatabaseHandler dataSource;
    private TextDrawable.IBuilder builder = TextDrawable.builder().round();
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private int fighter1TileColor;
    private int fighter2TileColor;

    // constructor
    public RVAdapterMatchView(Context context, List<Match> matches) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground =  mTypedValue.resourceId;
        this.matches = matches;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.matchview_item, viewGroup, false);
        return new MatchViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final MatchViewHolder matchViewHolder, final int i) {

        final Match match = matches.get(i);
        matchViewHolder.fighter1Name.setText(match.getFighter1().getName());
        matchViewHolder.fighter2Name.setText(match.getFighter2().getName());
        matchViewHolder.date.setText(formatDate(match.getDate()));

        calculateTotals(matchViewHolder, match);
        generateLetterTiles(matchViewHolder, match);

        // set onClick listener
        matchViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = v.getContext();
                // int position = matchViewHolder.getAdapterPosition();
                int matchId = match.getId();
                Log.v(TAG, "matchId=" + matchId + " clicking");

                Intent intent = new Intent(context, ScoreActivity.class);
                intent.putExtra("matchId", matchId);
                context.startActivity(intent);
            }
        });

        // set onLongClick listener
        matchViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                context = v.getContext();

                int matchId = match.getId();
                Log.v(TAG, "matchId=" + matchId + " long press");

                String fighter1Name = match.getFighter1().getName();
                String fighter2Name = match.getFighter2().getName();

                MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                        .title("Delete this match?")
                        .content(fighter1Name + " vs. " + fighter2Name )
                        .positiveText("Ok")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                matches.remove(i);
                                dataSource = new DatabaseHandler(context);
                                dataSource.open();
                                dataSource.deleteMatch(match);
                                dataSource.close();

                                // get updated list after deletion
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, getItemCount());

                                Log.v(TAG, "onPositive");
                            }
                        })
                        .show();

                return true;

            }
        });
    }

    private void generateLetterTiles(MatchViewHolder matchViewHolder, Match match) {
        fighter1TileColor = generator.getColor(match.getFighter1().getName());
        fighter2TileColor = generator.getColor(match.getFighter2().getName());
        String fighter1FirstChar = match.getFighter1().getName().substring(0, 1);
        String fighter2FirstChar = match.getFighter2().getName().substring(0, 1);
        TextDrawable ic1 = TextDrawable.builder().buildRound(fighter1FirstChar, fighter1TileColor);
        TextDrawable ic2 = TextDrawable.builder().buildRound(fighter2FirstChar, fighter2TileColor);
        matchViewHolder.image1.setImageDrawable(ic1);
        matchViewHolder.image2.setImageDrawable(ic2);
    }

    private void calculateTotals(MatchViewHolder matchViewHolder, Match match) {
        int fighter1Total = 0;
        int fighter2Total = 0;
        int numberOfRounds = match.getNumberOfRounds();

        for (int it = 0; it < numberOfRounds; it++) {
            fighter1Total = fighter1Total + match.getFighter1Scores().get(it);
            fighter2Total = fighter2Total + match.getFighter2Scores().get(it);
        }
        matchViewHolder.fighter1Score.setText(Integer.toString(fighter1Total));
        matchViewHolder.fighter2Score.setText(Integer.toString(fighter2Total));
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }
}
