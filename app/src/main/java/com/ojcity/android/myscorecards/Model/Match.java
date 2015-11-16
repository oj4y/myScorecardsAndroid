package com.ojcity.android.myscorecards.Model;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ojc on 5/27/2015.
 */
public class Match {

    private int id;
    private Fighter fighter1;
    private Fighter fighter2;
    private long dateScoredLong;
    private Date dateScored;
    private Integer numberOfRounds;
    private ArrayList<Integer> fighter1Scores;
    private ArrayList<Integer> fighter2Scores;
    private boolean earlyStoppage;
    private Integer roundStoppage;
    private Integer winnerFighterId;

    public Match() {
        this.id = 0;
        this.fighter1 = null;
        this.fighter2 = null;
        this.dateScoredLong = 0;
        this.dateScored = null;
        this.numberOfRounds = 0;
        this.fighter1Scores = new ArrayList<>();
        this.fighter2Scores = new ArrayList<>();
        this.earlyStoppage = false;
        this.roundStoppage = null;
        this.winnerFighterId = null;
    }

    public Match( Fighter fighter1, Fighter fighter2 ) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.dateScoredLong = System.currentTimeMillis();
        this.dateScored = new Date(this.dateScoredLong);
        this.numberOfRounds = null;
        this.fighter1Scores = new ArrayList<>();
        this.fighter2Scores = new ArrayList<>();
        this.earlyStoppage = false;
        this.roundStoppage = null;
        this.winnerFighterId = null;
    }

    public Fighter getFighter1() {
        return fighter1;
    }

    public void setFighter1(Fighter fighter) {
        this.fighter1 = fighter;
    }

    public void setFighter2(Fighter fighter) {
        this.fighter2 = fighter;
    }

    public Fighter getFighter2() {
        return fighter2;
    }

    public long getDateLong() {
        return dateScoredLong;
    }

    public void setDateLong(long date) {
        this.dateScoredLong = date;
        this.dateScored = new Date(this.dateScoredLong);
    }

    public Date getDate() {
        return this.dateScored;
    }

    public ArrayList<Integer> getFighter1Scores() {
        return fighter1Scores;
    }

    public ArrayList<Integer> getFighter2Scores() {
        return fighter2Scores;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isEarlyStoppage() {
        return earlyStoppage;
    }

    public void setEarlyStoppage(boolean earlyStoppage) {
        this.earlyStoppage = earlyStoppage;
    }

    public Integer getRoundStoppage() {
        return roundStoppage;
    }

    public void setRoundStoppage(int roundStoppage) {
        this.roundStoppage = roundStoppage;
    }

    public Integer getWinnerFighterId() {
        return winnerFighterId;
    }

    public void setWinnerFighterId(int winnerFighterId) {
        this.winnerFighterId = winnerFighterId;
    }

}
