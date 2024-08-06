package com.orangomango.battleship;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.net.*;

public class Util{
	public static final String JOIN_ACCEPTED = "join_accepted";
	public static final String LOBBY_FULL = "lobby_full";
	public static final String BOARD_DATA = "board_data";
	public static final String SHOOT_MESSAGE = "shoot_message";
	public static final String PLAYER_READY = "player_ready";
	public static final String PLAYER_TURN = "player_turn";
	public static final String ENEMY_RESPONSE = "enemy_response";
	public static final String GAMEOVER = "gameover";

	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	public static final int GAME_PORT = 12345;

	public static String getLocalAddress(){
		try (final DatagramSocket datagramSocket = new DatagramSocket()){
			datagramSocket.connect(InetAddress.getByName("127.0.0.1"), GAME_PORT); // 8.8.8.8
			return datagramSocket.getLocalAddress().getHostAddress();
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public static int getCol(char c){
		char[] arr = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
		for (int i = 0; i < 10; i++){
			if (arr[i] == c){
				return i;
			}
		}

		return -1;
	}

	public static String convertPos(int x, int y){
		char[] arr = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
		return String.valueOf(arr[x])+(y+1);
	}

	public static void schedule(Runnable r, int delay){
		new Thread(() -> {
			try {
				Thread.sleep(delay);
				r.run();
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}).start();
	}

	public static void showErrorMessage(String title, String headerText, String contentText){
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(title);
			alert.setHeaderText(headerText);
			alert.setContentText(contentText);
			alert.showAndWait();
		});
	}
}