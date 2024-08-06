package com.orangomango.battleship.client;

import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.function.Consumer;

import com.orangomango.battleship.Util;
import com.orangomango.battleship.core.Board;
import com.orangomango.battleship.core.Ship;

public class Client{
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private boolean connected, currentTurn;
	private int id;

	public static HashSet<String> servers = new HashSet<>();
	private static volatile boolean discovering = true;

	static {
		// DEBUG
		Client.servers.add("127.0.0.1;12345");
	}

	@FunctionalInterface
	public static interface ShipDestroyedEvent{
		public void fire(boolean mySide, Ship ship);
	}

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

	public static void discover(){
		Thread t = new Thread(() -> {
			try {
				DatagramSocket socket = new DatagramSocket(Util.GAME_PORT);
				while (Client.discovering){
					DatagramPacket packet = new DatagramPacket(new byte[32], 32);
					socket.receive(packet);
					String info = new String(packet.getData());
					Client.servers.add(info);
					Thread.sleep(500);

					System.out.println(Client.servers);
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}, "discover-thread");
		t.setDaemon(true);
		t.start();
	}

	public static void stopDiscovering(){
		Client.discovering = false;
	}

	public void listen(Board board, int[][] enemyBoard, ShipDestroyedEvent onShipDestroyed, Consumer<String> onGameOver){
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
							final Ship shipDestroyed = board.update(px, py);
							if (shipDestroyed != null) onShipDestroyed.fire(true, shipDestroyed);
							send(Util.ENEMY_RESPONSE);
							send(String.format("%d %d %d %s", px, py, board.getCell(px, py), shipDestroyed));

							// Check for gameover
							if (board.isGameOver()){
								send(Util.GAMEOVER);
								send(board.toString());
								onGameOver.accept(null);
							}
						} else if (message.equals(Util.PLAYER_TURN)){
							this.currentTurn = true;
							System.out.println("Your turn!");
						} else if (message.equals(Util.ENEMY_RESPONSE)){
							String data = this.reader.readLine();
							final int px = Integer.parseInt(data.split(" ")[0]);
							final int py = Integer.parseInt(data.split(" ")[1]);
							final int value = Integer.parseInt(data.split(" ")[2]);
							final Ship shipDestroyed = Ship.parseShip(data.split(" ")[3]);
							if (shipDestroyed != null) onShipDestroyed.fire(false, shipDestroyed);
							enemyBoard[px][py] = value;
							if (value == 2){
								Util.schedule(() -> {
									this.currentTurn = false;
									send(Util.PLAYER_TURN);
								}, 1000);
							}
						} else if (message.equals(Util.GAMEOVER)){
							StringBuilder builder = new StringBuilder();
							for (int i = 0; i < 10; i++){
								builder.append(this.reader.readLine());
								builder.append("\n");
							}
							onGameOver.accept(builder.toString());
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