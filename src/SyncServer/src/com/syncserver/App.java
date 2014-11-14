package com.syncserver;

import java.io.File;

public class App {

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Please Enter The Dir parameter……");
		}
		else {
			File file = new File(args[0]);
			if (!file.isDirectory()) {
				System.out.println("Please Check If The Dir exist……");
			}
			else {
				System.out.println("Starting server on port 9900……");
				(new Server(args[0])).run();
			}
		}
	}
}
