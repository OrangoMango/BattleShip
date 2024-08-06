package com.orangomango.battleship;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * BattleShip
 * @version 1.0
 * @author OrangoMango

          |    |    |   
         )_)  )_)  )_) 
        )___))___))___)
       )____)____)_____) 
     _____|____|____|____\
----\                     /----
   ~~~~~~~~~~~~~~~~~~~~~~~~~~
    ~~~~~~~~~~~~~~~~~~~~~~~
*/
public class MainApplication extends Application{
	@Override
	public void start(Stage stage){
		HomeScreen homeScreen = new HomeScreen(stage);
		Scene scene = homeScreen.getScene();

		stage.setTitle(Util.APP_TITLE);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
}