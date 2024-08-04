package com.orangomango.battleship.client;

import java.net.*;
import java.io.*;

import com.orangomango.battleship.Util;

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

	public void listen(){
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
							// Update local board
							// ...
						} else if (message.equals(Util.PLAYER_TURN)){
							this.currentTurn = true;
							System.out.println("Your turn!");
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

			if (this.currentTurn){
				this.currentTurn = false;
			}
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

	public int getId(){
		return this.id;
	}
}