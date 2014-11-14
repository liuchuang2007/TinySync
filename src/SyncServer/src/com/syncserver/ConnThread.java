package com.syncserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class ConnThread extends Thread {

	// Client information
	private ConnInfo mInfo;
	private FileManager mFileManager;
	private Writer mWriter;
	private Reader mReader;
	private InputStream mRStream;
	private OutputStream mWStream;
	private SimpleDateFormat mDf;
	public ConnThread(ConnInfo cInfo) throws IOException {
		mInfo = cInfo;
		mWStream = cInfo.mSock.getOutputStream();
		mWriter = new OutputStreamWriter(mWStream);
		mRStream = cInfo.mSock.getInputStream();
		mReader = new InputStreamReader(mRStream);
		mFileManager = new FileManager();
		mDf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}

	@Override
	public void run() {
		super.run();
		while(true) {
			try {

				// Check if there has any new files
				BufferedReader bufReader = new BufferedReader(mReader);
				String cmd = bufReader.readLine();
				if (cmd.equalsIgnoreCase(Constants.PULL_DATA)) {
					ArrayList<String> fileArr = mFileManager.getChangedFiles(mInfo.mSyncDir);
					if(!fileArr.isEmpty()) {
						if (mInfo.mSock != null) {
							for(String filePath : fileArr) {
								try {

									// start send file to client
									System.out.println(mInfo.mIpAddr + " "+ mDf.format(new Date()) + " Start:" + filePath);
									if(sendFile(filePath.replaceAll("\\\\", "/"))) {
										System.out.println(mInfo.mIpAddr + " "+ mDf.format(new Date()) + " End:" + filePath + "\n");
									}
									else {
										System.out.println(mInfo.mIpAddr + " "+ mDf.format(new Date()) + "Err:" + filePath + "\n");
									}
								} catch (IOException e) {

									// When file is writing, sending will failed.
									System.out.println(mInfo.mIpAddr + " "+ mDf.format(new Date()) + "Send file error:" + filePath + "\n");
									break;
								}
							}
						}
						else {
							System.out.println("Client [" + mInfo.mIpAddr + "] left");
						}
					}
					else {
						//System.out.println("No File To Sync");
					}

					_sendLine(Constants.TRANS_FINISHED);
					//System.out.println("Transform Finished One time");
				}
			} catch (IOException e) {
				break;
			}
		}	
	}

	public boolean sendFile(String fileName) throws IOException {

		// SEND FILE HAS TO STEP:
		// 1. SEND FILENAME, 
		_sendLine(Constants.TRANS_FILE);
	
		byte[] ack = new byte[1];
		byte[] content = new byte[Constants.PACKAGE_LEN];
		String tmpfileName = fileName.replaceAll(mInfo.mSyncDir, "");
		//System.out.println(tmpfileName+":"+mInfo.mSyncDir);
		_sendLine(tmpfileName);

		BufferedReader reader = new BufferedReader(mReader);
		String status = reader.readLine();
		if (Constants._OK.equals(status)) {

			// 2. SEND FILE DATA.
			FileInputStream fileInputStream = new FileInputStream(new File(fileName));
			CRC32 crc32 = new CRC32();
			CheckedInputStream checkedStream = new CheckedInputStream(fileInputStream, crc32);
			int len = 0;
			while((len = checkedStream.read(content, 3, 1021)) != -1) {

				// STREAM STRUCT: HAS DATA | DATA LEN | DATA
				content[0] = 1;
				System.arraycopy(Utils.shortToByte((short)len), 0, content, 1, 2);
				mWStream.write(content);

				// Consider the writing speed to fast will make local data buffer unsafe.
				// Here when we get the ack data, we post the next piece of data.
				mRStream.read(ack);
			}

			content[0] = 2;
			mWStream.write(content);
			mWStream.flush();
			String crc = Long.toHexString(crc32.getValue()).toUpperCase();

			// Verify Data
			_sendLine(Constants.VERIFYL_FILE);
			_sendLine(crc);
			status = reader.readLine();
			checkedStream.close();
			fileInputStream.close();
			return Constants._OK.trim().equals(status);
		}
		
		return false;
	}

	private void _sendLine(String bs) throws IOException {
	
		mWriter.write(bs + "\n");
		mWriter.flush();
	}

	public synchronized void start() {

		super.start();
	}
}
