package com.base.engine;

import org.lwjgl.input.Keyboard;

public class Game {

	public Game() {
		
	}
	
	public void input() {

		if (Input.getKeyDown(Keyboard.KEY_UP)) {
			System.out.println("Key Down");
		};
		
		if (Input.getKeyUp(Keyboard.KEY_UP)) {
			System.out.println("Key Up");
		};
		
		if (Input.getMouseDown(1)) {
			System.out.println("Key Down at " + Input.getMousePosition());
		};
		
		if (Input.getMouseUp(1)) {
			System.out.println("Key Up");
		};
		
	}

	public void update() {

	}

	public void render() {

	}
	
}
