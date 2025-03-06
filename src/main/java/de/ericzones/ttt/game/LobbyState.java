// Created by Eric B. 28.05.2020 13:16
package de.ericzones.ttt.game;

import de.ericzones.ttt.countdowns.LobbyCountdown;
import de.ericzones.ttt.main.Main;

public class LobbyState extends GameState {

    public static final int MIN_PLAYERS = 4,
                            MAX_PLAYERS = 12;

    private LobbyCountdown countdown;

    public LobbyState(Main plugin, GameStateManager gameStateManager) {
        countdown = new LobbyCountdown(plugin, gameStateManager);
    }

    @Override
    public void start() {
        countdown.startIdle();

    }

    @Override
    public void stop() {
        countdown.stop();

    }

    public LobbyCountdown getCountdown() {
        return countdown;
    }
}
