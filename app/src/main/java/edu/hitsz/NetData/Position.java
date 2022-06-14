package edu.hitsz.NetData;

import java.io.Serializable;

public class Position implements Serializable {
    public int locationX,locationY;
    public Position(int X,int Y){
        locationX=X;
        locationY=Y;
    }
}
