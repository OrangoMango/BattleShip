package com.orangomango.battleship.server;

import java.net.*;
import java.io.*;
import java.util.*;

import com.orangomango.battleship.Util;
import com.orangomango.battleship.core.Board;

public class Server{
	private ServerSocket server;
	private ArrayList<ClientManager> clients = new ArrayList<>();
	private Board board1, board2;
	private String host;
	private int port;
	private boolean serverStarted = false;

	public Server(String host, int port){
		this.host = host;
		this.port = port;

		try {
			this.server = new ServerSocket(this.port, 2, InetAddress.getByName(this.host));
		} catch (IOException ex){
			ex.printStackTrace();
			Util.showErrorMessage("Error", "Server error", "Could not start server");
			return;
		}

		this.board1 = new Board(null);
		this.board2 = new Board(null);
		this.serverStarted = true;

		//startBroadcast();
	}

	public void doWhenReady(Runnable r){
		Thread waiter = new Thread(() -> {
			while (true){
				try {
					if (this.clients.size() == 1){
						r.run();
						break;
					}
					Thread.sleep(500);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		});
		waiter.setDaemon(true);
		waiter.start();
	}

	private void startBroadcast(){
		Thread t = new Thread(() -> {
			try {
				DatagramSocket socket = new DatagramSocket();
				while (!this.server.isClosed()){
					byte[] information = (this.host+";"+this.port).getBytes();
					DatagramPacket packet = new DatagramPacket(information, information.length, InetAddress.getByName("255.255.255.255"), Util.GAME_PORT);
					socket.send(packet);
					Thread.sleep(500);
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}, "server-broadcast");
		t.setDaemon(true);
		t.start();
	}

	public void listen(){
		Thread daemon = new Thread(() -> {
			while (!this.server.isClosed()){
				try {
					Socket socket = this.server.accept();
					ClientManager manager = new ClientManager(this, socket);
					if (manager.isInside()) this.clients.add(manager);
					Thread t = new Thread(manager);
					t.setDaemon(true);
					t.start();
					System.out.println("Socket connected");
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
		});
		daemon.setDaemon(true);
		daemon.start();
	}

	public void destroy(){
		try {
			this.server.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public void startGame(){
		if (this.board1.hasData() && this.board2.hasData()){
			boolean p1 = Math.random() < 0.5;
			this.clients.get(p1 ? 0 : 1).send(Util.PLAYER_TURN);
		}
	}

	public ArrayList<ClientManager> getClients(){
		return this.clients;
	}

	public Board getBoard1(){
		return this.board1;
	}

	public Board getBoard2(){
		return this.board2;
	}

	public boolean isServerStarted(){
		return this.serverStarted;
	}
}