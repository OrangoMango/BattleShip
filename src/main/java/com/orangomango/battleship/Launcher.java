package com.orangomango.battleship;

import java.io.*;

import com.orangomango.battleship.server.Server;
import com.orangomango.battleship.client.Client;

public class Launcher{
	public static void main(String[] args){
		String host = Util.getLocalAddress();
		System.out.println(host);

		if (args[0].equals("server")){
			Server server = new Server(host, 1234);
			server.listen();
		} else if (args[0].equals("client")){
			Client client = new Client(host, 1234);
			client.listen();

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

				while (client.isConnected()){
					String line = reader.readLine();

					// DEBUG
					if (line.equals("sendBoardData")){
						client.send("board_data");
						client.send(reader.readLine());
						client.send("0000000000");
						client.send("0010000000");
						client.send("0010001000");
						client.send("0000001001");
						client.send("0000001001");
						client.send("0010000001");
						client.send("0010000001");
						client.send("0010000001");
						client.send("0000000000");
						client.send("0000111100");
					}

					client.send(line);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
}