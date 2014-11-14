package com.syncserver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class FileManager {
	private HashMap<String, Long> mLastFileInfo;

	public FileManager() {
		mLastFileInfo = new HashMap<String, Long>();
	}

	public ArrayList<String> getChangedFiles(String scanDir) {
		ArrayList<String> changedFiles = new ArrayList<String>();
		ArrayList<String> allFiles = getSubFiles(scanDir);
		
		// Check the last update time, if it updated since last time, then it needs sync
		for(String item : allFiles) {
			
			File fp = new File(item);
			Long lastTime = fp.lastModified();
			if (!lastTime.equals(mLastFileInfo.get(item))) {
				changedFiles.add(item);
			}
			
			mLastFileInfo.put(item, lastTime);
		}
		
		return changedFiles;
	}
	
	public ArrayList<String> getSubFiles(String dir) {
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<String> tmp;
		File fp = new File(dir);
		if (fp != null) {
			if (fp.isDirectory()) {
				for (String subfile : fp.list()) {
					tmp = getSubFiles(dir + File.separator + subfile);
					for(String filePath : tmp) {

						files.add(filePath);
					}
				}
			}
			else {
				files.add(fp.getAbsolutePath());
			}
		}

		return files;
	}
}
