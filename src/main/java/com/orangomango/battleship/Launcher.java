package com.orangomango.battleship;

import javafx.application.Application;

import java.io.*;

import com.orangomango.battleship.server.Server;
import com.orangomango.battleship.client.Client;
import com.orangomango.battleship.client.ClientApplication;

public class Launcher{
	public static void main(String[] args){
		String host = Util.getLocalAddress();
		System.out.println(host);

		if (args[0].equals("server")){
			Server server = new Server(host, 1234);
			server.listen();
		} else if (args[0].equals("client")){
			Application.launch(ClientApplication.class);
		}
	}
}