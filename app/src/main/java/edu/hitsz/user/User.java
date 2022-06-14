package edu.hitsz.user;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    public int userID;
    public String Name;
    public int score;
    public Date date;
    private int rank;
    public User(int userID,String Name,int score,Date date){
        this.userID=userID;
        this.Name=Name;
        this.score=score;
        this.date=date;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
