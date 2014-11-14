package com.sync;

import java.io.File;

public class App {

	public static void main(String[] args) {

		//(new Client("G:/TOOO", "127.0.0.1", "9900")).run();
		String errMsg = "";
		if (args.length != 3) {
			errMsg = "Please Enter The Dir parameter……\n" +
					 "Example:\n" +
					 "java -jar client.jar G:/TOOO 127.0.0.1 9900\n";
			System.out.println(errMsg);
		}
		else {

			File file = new File(args[0]);
			if (!file.isDirectory()) {
				errMsg = "Please Check If The Dir exist……\n";
				System.out.println(errMsg);
			}
			else {
				(new Client(args[0], args[1], args[2])).run();
			}
		}
	}
}
