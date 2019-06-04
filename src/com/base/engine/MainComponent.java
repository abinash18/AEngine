package com.base.engine;

public class MainComponent {
	
	public MainComponent() {
		
	}
	
	public void start() {
		
		run();
		
	}
	
	public void stop() {
		
	}
	
	public void run() {
		
		while (!Window.closeRequested()) {
			
			render();
			
		}
		
	}
	
	public void render() {
		
		Window.render();
		
	}
	
	public void cleanUp() {
		
	}
	
	public static void main(String[] args) {

		System.out.println("Engine Initializing.");
		
		InitializeEngine.InitializeEngineGL();
		
		MainComponent game = new MainComponent();
		
		game.start();
		
		System.out.println("Engine Terminating.");
		
	}

}
