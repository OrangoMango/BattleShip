package com.orangomango.battleship.server;

import java.net.*;
import java.io.*;

import com.orangomango.battleship.Util;

public class ClientManager implements Runnable{
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Server server;
	private boolean inside;

	public ClientManager(Server server, Socket socket){
		this.server = server;
		try {
			this.socket = socket;
			this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			if (this.server.getClients().size() == 2){
				this.writer.write(Util.LOBBY_FULL);
				this.writer.newLine();
				this.writer.flush();
			} else {
				this.inside = true;
				this.writer.write(Util.JOIN_ACCEPTED);
				this.writer.newLine();
				this.writer.write(Integer.toString(this.server.getClients().size()+1));
				this.writer.newLine();
				this.writer.flush();
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public boolean isInside(){
		return this.inside;
	}

	@Override
	public void run(){
		while (this.server.isServerStarted()){
			try {
				String header = this.reader.readLine();
				System.out.println("Got: "+header);
				if (header != null){
					if (header.equals(Util.BOARD_DATA)){
						String player = this.reader.readLine();
						StringBuilder data = new StringBuilder();
						for (int i = 0; i < 10; i++){
							String line = this.reader.readLine();
							data.append(line);
							data.append("\n");
						}

						if (player.equals("player1")){
							System.out.println("Setting board for player 1");
							this.server.getBoard1().setData(data.toString());
						} else if (player.equals("player2")){
							System.out.println("Setting board for player 2");
							this.server.getBoard2().setData(data.toString());
						}

						this.server.startGame();

						broadcast(Util.PLAYER_READY);
					} else if (header.equals(Util.SHOOT_MESSAGE)){
						String message = this.reader.readLine();
						broadcast(message);
					} else {
						broadcast(header);
					}
				} else {
					// ERROR
					this.server.fireError();
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
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

	private void broadcast(String message){
		System.out.println("Broadcasting: "+message);
		for (ClientManager manager : this.server.getClients()){
			if (manager != this){
				try {
					manager.writer.write(message);
					manager.writer.newLine();
					manager.writer.flush();
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
		}
	}
}