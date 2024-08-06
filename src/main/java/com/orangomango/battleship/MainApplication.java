package com.orangomango.battleship;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class MainApplication extends Application{
	@Override
	public void start(Stage stage){
		HomeScreen homeScreen = new HomeScreen(stage);
		Scene scene = homeScreen.getScene();

		stage.setTitle("BattleShip");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
}