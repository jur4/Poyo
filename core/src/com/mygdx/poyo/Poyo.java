package com.mygdx.poyo;

import com.badlogic.gdx.Game;


public class Poyo extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}
