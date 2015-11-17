package com.ojcity.android.myscorecards.SQLiteDAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ojcity.android.myscorecards.Model.Fighter;
import com.ojcity.android.myscorecards.Model.Match;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ojc on 7/10/2015.
 * Class that handles CRUD functions
 */
public class DatabaseHandler {

    private static final String TAG = "DatabaseHandler";

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public DatabaseHandler(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database.close();
    }

    public boolean isFighterAlreadyInDatabase(String fieldValue) {
        String query = "select * from " + dbHelper.TABLE_FIGHTERS + " where "
                + dbHelper.COLUMN_FIGHTER_NAME + " = " + "'" + fieldValue + "'";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();

        Log.v(TAG, "Fighter already exists in database");
        return true;
    }

    public void createFighter(String fighterName) {
        ContentValues fighterContentValues = new ContentValues();
        fighterContentValues.put(SQLiteHelper.COLUMN_FIGHTER_NAME, fighterName);

        long fighterRowId;
        fighterRowId = database.insert(SQLiteHelper.TABLE_FIGHTERS, null, fighterContentValues);

        Log.v(TAG, fighterName + " inserted at RowId=" + Long.toString(fighterRowId));

    }

    public void createMatch(Match match) {

        // get contentvalues from given match
        ContentValues matchContentValues = new ContentValues();
        matchContentValues.put(dbHelper.COLUMN_FIGHTER1, match.getFighter1().getId());
        matchContentValues.put(dbHelper.COLUMN_FIGHTER2, match.getFighter2().getId());
        matchContentValues.put(dbHelper.COLUMN_ROUNDS, match.getNumberOfRounds());
        matchContentValues.put(dbHelper.COLUMN_CREATED, match.getDateLong());

        long matchRowId;
        matchRowId = database.insert(SQLiteHelper.TABLE_MATCHES, null, matchContentValues);
        Log.v(TAG, "match inserted at RowId=" + Long.toString(matchRowId));

    }

    // used to read from database with a search term returning a list of fighters (autocomplete)
    public List<Fighter> readFighters(String searchTerm) {

        List<Fighter> fighterList = new ArrayList<>();

        // assemble the query
        String sqlQuery = "";
        sqlQuery += "select * from " + dbHelper.TABLE_FIGHTERS;
        sqlQuery += " where " + dbHelper.COLUMN_FIGHTER_NAME + " LIKE '%" + searchTerm + "%'";
        sqlQuery += " order by " + dbHelper.COLUMN_ID + " desc";
        sqlQuery += " limit 0,5";

        // execute the query
        Cursor cursor = database.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()) {
            do {
                // get fighterName string from db
                String fighterName = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_FIGHTER_NAME));

                // add to fighter object
                Fighter fighter = new Fighter(fighterName);

                // add to fighter list
                fighterList.add(fighter);

            } while(cursor.moveToNext());

            cursor.close();
        }

        return fighterList;
    }

    public List<Fighter> readAllFighters() {

        List<Fighter> fighterList = new ArrayList<>();

        // assemble the query
        String sqlQuery = "";
        sqlQuery += "select * from " + dbHelper.TABLE_FIGHTERS;

        // execute the query
        Cursor cursor = database.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()) {
            do {
                // get fighterName string from db
                String fighterName = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_FIGHTER_NAME));

                // add to fighter object
                Fighter fighter = new Fighter(fighterName);

                // add to fighter list
                fighterList.add(fighter);

            } while(cursor.moveToNext());

            cursor.close();
        }

        return fighterList;
    }

    public Match readMatch(int matchId) {

        Match match = new Match();
        int numberOfRounds;

        // make query
        String sqlQuery = "";
        sqlQuery += "select * from " + dbHelper.TABLE_MATCHES;
        sqlQuery += " where " + dbHelper.TABLE_MATCHES + "." + dbHelper.COLUMN_ID;
        sqlQuery += " = " + matchId + ";";
        Log.v(TAG, sqlQuery);

        Cursor cursor = database.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()) {
            do {
                // get numberOfRounds
                numberOfRounds = cursor.getInt(1);
                match.setNumberOfRounds(numberOfRounds);

                // get date
                long date = cursor.getLong(2);

                // get fighterNames
                int fighter1Id = cursor.getInt(3);
                int fighter2Id = cursor.getInt(4);
                // query the db to get fighters
                Fighter fighter1 = readFighter(fighter1Id);
                Fighter fighter2 = readFighter(fighter2Id);

                int matchStopped = cursor.getInt(5);
                int roundStoppage = cursor.getInt(6);
                int winnerFighterId = cursor.getInt(7);

                // set the values
                match.setFighter1(fighter1);
                match.setFighter2(fighter2);
                match.setId(matchId);
                match.setDateLong(date);
                if (matchStopped == 1) {
                    match.setEarlyStoppage(true);
                    match.setRoundStoppage(roundStoppage);
                    match.setWinnerFighterId(winnerFighterId);
                }

                // get scores
                // score columns start at columns 5 to 34
                // probably dual counters
                for (int i = 8, k = 1; i < 38 && k <= numberOfRounds; i += 2, k++) {
                    int fighter1RoundScore = cursor.getInt(i);
                    int fighter2RoundScore = cursor.getInt(i + 1);
                    match.getFighter1Scores().add(fighter1RoundScore);
                    match.getFighter2Scores().add(fighter2RoundScore);
                }

            } while(cursor.moveToNext());

            cursor.close();
        }

        return match;
    }

    // get fighter from database
    public Fighter readFighter(int fighterId) {

        Fighter fighter = new Fighter();

        // assemble the query
        String sqlQuery = "";
        sqlQuery += "select * from fighters where ";
        sqlQuery += dbHelper.COLUMN_ID + " = " + fighterId;

        // execute the query
        Cursor cursor = database.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()) {
            do {
                // get fighterName string from db
                String fighterName = cursor.getString(1);

                // add to fighter object
                fighter.setId(fighterId);
                fighter.setName(fighterName);

            } while(cursor.moveToNext());

            cursor.close();
        }

        return fighter;
    }

    // used to get all matches from database
    public List<Match> readMatches() {

        List<Match> matchList = new ArrayList<>();

        // make query
//        String sqlQuery = "select matches._id, group_concat(fighters.name) ";
//        sqlQuery += "from matches, fighters ";
//        sqlQuery += "where matches.fighter1 = fighters._id OR matches.fighter2 = fighters._id ";
//        sqlQuery += "group by matches._id";

        // query version 2
        String sqlQuery2 = "select *, group_concat(fighters.name) ";
        sqlQuery2 += "from matches, fighters ";
        sqlQuery2 += "where matches.fighter1 = fighters._id OR matches.fighter2 = fighters._id ";
        sqlQuery2 += "group by matches._id ";
        sqlQuery2 += "order by dateScored DESC";

        // execute SQL
        Cursor cursor = database.rawQuery(sqlQuery2, null);

        if(cursor.moveToFirst()) {
            do {
                // column names _id and (last column) group_concat(fighters.name)
                int matchId = cursor.getInt(0);
                int rounds = cursor.getInt(1);
                long date = cursor.getLong(2);

                int fighter1Id = cursor.getInt(3);
                int fighter2Id = cursor.getInt(4);

                String bothFighters = cursor.getString(40);
                int delimiter = bothFighters.indexOf(',');

                String fighter1Name;
                String fighter2Name;

                if (fighter1Id < fighter2Id) {
                    fighter1Name = bothFighters.substring(0, delimiter);
                    fighter2Name = bothFighters.substring(delimiter + 1, bothFighters.length());
                } else { // fighter1Id > fighter2Id
                    fighter2Name = bothFighters.substring(0, delimiter);
                    fighter1Name = bothFighters.substring(delimiter + 1, bothFighters.length());
                }

                Log.v(TAG, "fighter1Name=" + fighter1Name);
                Log.v(TAG, "fighter2Name=" + fighter2Name);

                // create the fighter objects
                Fighter fighter1 = new Fighter(fighter1Name);
                Fighter fighter2 = new Fighter(fighter2Name);

                // create the match object and setId
                Match match = new Match(fighter1, fighter2);
                match.setId(matchId);
                match.setNumberOfRounds(rounds);
                match.setDateLong(date);
                // scores at indices 5 to 34
                for (int i = 8, k = 1; i < 38 && k <= rounds; i += 2, k++) {
                    match.getFighter1Scores().add(cursor.getInt(i));
                    match.getFighter2Scores().add(cursor.getInt(i+1));
                }

                // add to match list
                matchList.add(match);

            } while(cursor.moveToNext());

            cursor.close();
        }
        return matchList;
    }

    // update score given (2) ArrayLists of scores
    public void updateScore(Match match) {

        int numberOfRounds = match.getNumberOfRounds();

        // get contentvalues from given match
        ContentValues matchContentValues = new ContentValues();

        for( int i = 0; i < numberOfRounds; i++ ) {
            int roundNumber = i+1;
            String columnFighter1ScoreRound = "fighter1ScoreRound" + Integer.toString(roundNumber);
            String columnFighter2ScoreRound = "fighter2ScoreRound" + Integer.toString(roundNumber);

            matchContentValues.put(columnFighter1ScoreRound, match.getFighter1Scores().get(i));
            matchContentValues.put(columnFighter2ScoreRound, match.getFighter2Scores().get(i));
        }

        int resultRowsAffected = database.update(dbHelper.TABLE_MATCHES, matchContentValues,
                dbHelper.COLUMN_ID + "=" + match.getId(), null);

        Log.v(TAG, "rows updated: " + Long.toString(resultRowsAffected));

    }

    public void updateStoppage(Match match) {

        // get contentvalues from given match
        ContentValues matchContentValues = new ContentValues();

        int earlyStoppageAsInt = -1;

        if (match.isEarlyStoppage())
            earlyStoppageAsInt = 1;
        else {
            earlyStoppageAsInt = 0;
        }

        matchContentValues.put("earlyStoppage", earlyStoppageAsInt);
        matchContentValues.put("roundStoppage", match.getRoundStoppage());
        matchContentValues.put("winningFighter", match.getWinnerFighterId());

        int resultRowsAffected = database.update(dbHelper.TABLE_MATCHES, matchContentValues,
                dbHelper.COLUMN_ID + "=" + match.getId(), null);

        Log.v(TAG, "rows updated: " + Long.toString(resultRowsAffected));

    }

    public void deleteFighter(String fighterName) {

        int fighterId = getFighterId(fighterName);

        int matchesDeleted = database.delete(dbHelper.TABLE_MATCHES,
                dbHelper.COLUMN_FIGHTER1 + "= ? OR " + dbHelper.COLUMN_FIGHTER2 + "= ?",
                new String[] { Integer.toString(fighterId), Integer.toString(fighterId) } );

        Log.v(TAG, "matchesDeleted= " + Integer.toString(matchesDeleted));

        int fighterDeleted = database.delete(dbHelper.TABLE_FIGHTERS,
                dbHelper.COLUMN_ID + "=" + fighterId, null);

        Log.v(TAG, "fightersDeleted= " + Integer.toString(fighterDeleted));

    }

    // deletes a match given match
    public void deleteMatch(Match match) {

        int rowsDeleted = database.delete(dbHelper.TABLE_MATCHES,
                dbHelper.COLUMN_ID + "=" + match.getId(), null);

        Log.v(TAG, "Number of rows deleted: " + Integer.toString(rowsDeleted));

    }

    // used to read fighter's ID from database with a search term
    public int getFighterId(String searchTerm) {
        int fighterId = 0;

        // assemble the query
        String sqlQuery = "";
        sqlQuery += "select * from " + dbHelper.TABLE_FIGHTERS;
        sqlQuery += " where " + dbHelper.COLUMN_FIGHTER_NAME + " LIKE '%" + searchTerm + "%'";

        // execute the query
        Cursor cursor = database.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()) {
            do {
                // get fighterId from db
                fighterId = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID));

            } while(cursor.moveToNext());

            cursor.close();
        }

        return fighterId;
    }

}
