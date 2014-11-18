package com.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;


public class Client {
	private String mHost;
	private int mPort;
	private String mSyncDir;
	private Socket mSocket;
	private Writer mWriter;
	private Reader mReader;
	private InputStream mRStream;
	private OutputStream mWStream;
	private boolean mInSync;
	public Client(String dir, String host, String port) {

		mSyncDir = dir;
		mHost = host;
		mPort = Integer.parseInt(port);
		mInSync = false;
		_connect();
	}

	public void run() {

		Timer timer = new Timer();  
		timer.schedule(new SyncTimer(), 1000, 2000);
	}

	private void _sendLine(String bs) throws IOException {

		mWriter.write(bs + "\n");
		mWriter.flush();
	}

	private void _connect() {

		try {

			mSocket = new Socket(mHost, mPort);
			mWStream = mSocket.getOutputStream();
			mWriter = new OutputStreamWriter(mWStream);
			mRStream = mSocket.getInputStream();
			mReader = new InputStreamReader(mRStream);

		} catch (UnknownHostException e) {
			System.out.println("Please check if IP, PORT, and FIRE WALL is ok……\n");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Please check if IP, PORT, and FIRE WALL is ok……\n");
			System.exit(0);
		}
	}

	public class SyncTimer extends TimerTask{

		@Override
		public void run() {

			if (mInSync) {
				return;
			}
			else {
				mInSync = true;
			}

			if (mSocket == null) {
				_connect();
			}

			try {
				_sendLine(Constants.PULL_DATA);
				BufferedReader bufReader = new BufferedReader(mReader);
				FileManager fileManager = new FileManager();
				while(true) {
					
					String cmd = bufReader.readLine();
					if (cmd.equals(Constants.TRANS_FINISHED)) {
						break;
					}
					else if (cmd.equals(Constants.TRANS_FILE)) {
						String fileName = mSyncDir + bufReader.readLine();
						File file = fileManager.createFile(fileName);
						_sendLine(Constants._OK);
						
						// Add log
						SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						System.out.println("[" + df.format(new Date()) + "] " + fileName);
	
						// Save File;
						FileOutputStream fOutputStream = new FileOutputStream(file);
						CRC32 crc32 = new CRC32();
						CheckedOutputStream checkedStream = new CheckedOutputStream(fOutputStream, crc32);
						String savedCrcStr = Long.toHexString(crc32.getValue()).toUpperCase();
						byte[] data = new byte[1024];
						byte[] byteLen = new byte[2];
						byte[] ack = new byte[1];
						while(mRStream.read(data) != -1) {

							// STREAM STRUCT: HAS DATA | DATA LEN | DATA
							if (data[0] == 2) {
								break;
							}
							else {
								System.arraycopy(data, 1, byteLen, 0, 2);
								checkedStream.write(data, 3, Utils.byteToShort(byteLen));

								// When received data success. return ack to get next piece.
								mWStream.write(ack);
							}
						}

						// Verify Data
						cmd = bufReader.readLine();
						if (cmd.equals(Constants.VERIFYL_FILE)) {
							String crcStr = bufReader.readLine();
							savedCrcStr = Long.toHexString(crc32.getValue()).toUpperCase();
							System.out.println("Verify CRC: [Server]"+crcStr + " <--> [Local]" + savedCrcStr);
							if (crcStr.equals(savedCrcStr)) {
								_sendLine(Constants._OK);
								System.out.println("File Verified Success\n");
							}
							else {
								_sendLine(Constants._ERR);
								fileManager.delete(fileName);
								System.out.println("File Verified Failed\n");
							}
						}
						checkedStream.close();
						fOutputStream.close();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mInSync = false;
		}
	}
}
