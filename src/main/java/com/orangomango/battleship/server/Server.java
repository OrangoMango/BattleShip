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

	public Server(String host, int port){
		try {
			this.server = new ServerSocket(port, 2, InetAddress.getByName(host));
		} catch (IOException ex){
			ex.printStackTrace();
		}

		this.board1 = new Board(null);
		this.board2 = new Board(null);
	}

	public void listen(){
		Thread daemon = new Thread(() -> {
			while (true){
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
}