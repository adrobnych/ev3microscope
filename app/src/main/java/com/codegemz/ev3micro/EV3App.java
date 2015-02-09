package com.codegemz.ev3micro;

import android.app.Application;

/**
 * Created by adrobnych on 2/9/15.
 */
public class EV3App extends Application {

    public int getLevelA() {
        return levelA;
    }

    public void setLevelA(int levelA) {
        this.levelA = levelA;
    }

    private int levelA;

    public EV3App() {
        super();
    }
}