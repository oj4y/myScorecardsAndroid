package com.ojcity.android.myscorecards.SQLiteDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ojc on 6/17/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHelper";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // fighters table
    public static final String TABLE_FIGHTERS = "fighters";
    public static final String COLUMN_FIGHTER_NAME = "name";

    // matches table
    public static final String TABLE_MATCHES = "matches";
    public static final String COLUMN_ROUNDS = "numberOfRounds";
    public static final String COLUMN_CREATED = "dateScored";
    public static final String COLUMN_FIGHTER1 = "fighter1";
    public static final String COLUMN_FIGHTER2 = "fighter2";

    // scores table
    public static final String TABLE_SCORES = "scorecard";

    // common column names
    public static final String COLUMN_ID = "_id";

    // database name and version
    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 1;

    // fighter table creation SQL statement
    private static final String CREATE_TABLE_FIGHTERS =
            "CREATE TABLE " + TABLE_FIGHTERS + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_FIGHTER_NAME + " text not null collate nocase);";

    // match table creation SQL statement
    private static final String CREATE_TABLE_MATCHES =
            "CREATE TABLE " + TABLE_MATCHES + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_ROUNDS + " int, "
                    + COLUMN_CREATED + " int not null, "
                    + COLUMN_FIGHTER1 + " integer, "
                    + COLUMN_FIGHTER2 + " integer, "
                    + "foreign key(" + COLUMN_FIGHTER1 + ")" + " references " + TABLE_FIGHTERS
                    + "(" + COLUMN_ID + "), "
                    + "foreign key(" + COLUMN_FIGHTER2 + ")" + " references " + TABLE_FIGHTERS
                    + "(" + COLUMN_ID + "));";

//    private static final String CREATE_TABLE_SCORES =
//            "CREATE TABLE " + TABLE_SCORES + "("
//                    + COLUMN_ID + " int, "
//                    + "foreign key(" + COLUMN_ID + ")" + " references " + TABLE_MATCHES
//                    + "(" + COLUMN_ID + "));";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_FIGHTERS);
        database.execSQL(CREATE_TABLE_MATCHES);
//        database.execSQL(CREATE_TABLE_SCORES);

        // create sql columns for each fighter's round scores
        for (int i = 1; i < 15 + 1; i++) {
            String columnFighter1ScoreRound = "fighter1ScoreRound" + i;
            String columnFighter2ScoreRound = "fighter2ScoreRound" + i;

            String sqlQuery1 = "ALTER TABLE " + TABLE_MATCHES;
            sqlQuery1 += " add " + columnFighter1ScoreRound + " integer";

            String sqlQuery2 = "ALTER TABLE " + TABLE_MATCHES;
            sqlQuery2 += " add " + columnFighter2ScoreRound + " integer";

            database.execSQL(sqlQuery1);
            database.execSQL(sqlQuery2);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIGHTERS);
        onCreate(db);
    }
}
