package com.orangomango.battleship.client;

import java.net.*;
import java.io.*;

import com.orangomango.battleship.Util;
import com.orangomango.battleship.core.Board;

public class Client{
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private boolean connected, currentTurn;
	private int id;

	public Client(String host, int port){
		try {
			this.socket = new Socket(host, port);
			this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			String confirm = this.reader.readLine();
			if (confirm.equals(Util.LOBBY_FULL)){
				System.out.println("LOBBY FULL");
				quit();
			} else if (confirm.equals(Util.JOIN_ACCEPTED)){
				System.out.println("Joined successfully");
				this.id = Integer.parseInt(this.reader.readLine());
				this.connected = true;
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public void listen(Board board, int[][] enemyBoard){
		Thread daemon = new Thread(() -> {
			try {
				while (isConnected()){
					String message = this.reader.readLine();
					if (message == null){
						quit();
					} else {
						System.out.println("> "+message);
						if (message.equals(Util.PLAYER_READY)){
							System.out.println("Player is ready");
						} else if (message.startsWith(Util.SHOOT_MESSAGE)){
							String pos = message.split(":")[1];
							System.out.println(pos);
							final int px = Util.getCol(pos.charAt(0));
							final int py = Integer.parseInt(String.valueOf(pos.charAt(1)))-1;
							board.update(px, py);
							send(Util.ENEMY_RESPONSE);
							send(String.format("%d %d %d", px, py, board.getCell(px, py)));
						} else if (message.equals(Util.PLAYER_TURN)){
							this.currentTurn = true;
							System.out.println("Your turn!");
						} else if (message.equals(Util.ENEMY_RESPONSE)){
							String data = this.reader.readLine();
							final int px = Integer.parseInt(data.split(" ")[0]);
							final int py = Integer.parseInt(data.split(" ")[1]);
							final int value = Integer.parseInt(data.split(" ")[2]);
							enemyBoard[px][py] = value;
							Util.schedule(() -> {
								this.currentTurn = false;
								send(Util.PLAYER_TURN);
							}, 1000);
						}
					}
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		});
		daemon.setDaemon(true);
		daemon.start();
	}

	public void send(String message){
		try {
			this.writer.write(message);
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public void quit(){
		try {
			this.socket.close();
			this.reader.close();
			this.writer.close();
			this.connected = false;
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public boolean isConnected(){
		return this.connected;
	}

	public boolean isCurrentTurn(){
		return this.currentTurn;
	}

	public int getId(){
		return this.id;
	}
}