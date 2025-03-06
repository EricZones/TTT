// Created by Eric B. 28.05.2020 13:13
package de.ericzones.ttt.game;

public abstract class GameState {

    public static final int LOBBY_STATE = 0,
                            INGAME_STATE = 1,
                            ENDING_STATE = 2;

    public abstract void start();

    public abstract void stop();

}
