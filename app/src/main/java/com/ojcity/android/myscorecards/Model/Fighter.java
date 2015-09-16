package com.ojcity.android.myscorecards.Model;

public class Fighter {

    private int id = 0;
    private String name;

    public Fighter ( ) {    }

    // constructor
    public Fighter( String name ) {
        this.name = name;
    }

    public String getName( ) {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
