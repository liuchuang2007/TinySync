package com.sync;

import java.io.File;
import java.io.IOException;
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
		for(String item : allFiles) {

			File fp = new File(item);
			Long lastTime = fp.lastModified();
			if (!lastTime.equals(mLastFileInfo.get(item))) {
				changedFiles.add(item);
				//System.out.println(item);
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

	public File createFile(String pathname) throws IOException {
		File file = new File(pathname);
		createDir(file.getParent());
		
		// Create file
		if (!file.exists()) {
			file.createNewFile();
		}

		
		return file;
	}

	public void createDir(String pathname) {
		File file = new File(pathname);
		if (!file.getParentFile().exists()) {
			createDir(file.getParent());
		}

		if (!file.exists()) {
			file.mkdir();
		}
		
	}

	public void delete(String pathname) {
		File file = new File(pathname);
		if (!file.exists()) {
			file.delete();
		}
	}
}
