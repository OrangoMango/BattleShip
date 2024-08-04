package com.orangomango.battleship;

import java.net.*;

public class Util{
	public static final String JOIN_ACCEPTED = "join_accepted";
	public static final String LOBBY_FULL = "lobby_full";
	public static final String BOARD_DATA = "board_data";
	public static final String SHOOT_MESSAGE = "shoot_message";
	public static final String PLAYER_READY = "player_ready";
	public static final String PLAYER_TURN = "player_turn";

	public static String getLocalAddress(){
		try (final DatagramSocket datagramSocket = new DatagramSocket()){
			datagramSocket.connect(InetAddress.getByName("127.0.0.1"), 12345); // 8.8.8.8
			return datagramSocket.getLocalAddress().getHostAddress();
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}