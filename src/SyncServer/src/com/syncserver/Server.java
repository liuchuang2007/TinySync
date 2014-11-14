package com.syncserver;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/*
 * @Name: File synchronous Server
 * @Description: File scan machine, once client connect to server, server will check the lastest
 * 				 file and push it to the client.
 * @Author: george liu
 * @date:	2014-10-12
 */
public class Server {

	public ArrayList<ConnInfo> mClients;
	private String mSyncDir;
	public Server(String dir) {
	
		// Process the windows path condition
		mSyncDir = dir.replaceAll("\\\\", "/").replaceAll("//", "/");
		mClients = new ArrayList<ConnInfo>();
	}

	public void run() {
		try {

			// Check if the client is still connected
			//Timer timer = new Timer();  
			//timer.schedule(new CheckSocTimer(), 1000, 2000);

			// init server
			ServerSocket server = new ServerSocket(9900);
			System.out.println("Server started……");
			Socket socket = null;
			while((socket = server.accept()) != null) {
				ConnInfo client = new ConnInfo();
				client.mSock = socket;
				client.mSyncDir = mSyncDir;
				client.mIpAddr = socket.getInetAddress().getHostAddress();
				mClients.add(client);
				
				// Start thread to process the new client
				System.out.println("Client [" + client.mIpAddr + "] connected");
				new ConnThread(client).start();
			}

			server.close();
		} catch (UnknownHostException e) {
			System.out.println("Server Failed……");
		} catch (IOException e) {
			System.out.println("Server Failed……");
		}
	}

	/*private class CheckSocTimer extends TimerTask {

		@Override
		public void run() {
			for (ConnInfo client : mClients) {
				if (client.mSock.isClosed()) {
					System.out.println("Client [" + client.mIpAddr + "] left");
					mClients.remove(client);
				}
				else {
					System.out.println("Client [" + client.mIpAddr + "] active");
				}
			}
		}
	}*/
}
