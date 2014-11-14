package com.sync;

public class Utils {

	/**
	 * Covert int to byte array
	 * @param num  The number to be converted
	 * @return byte[]
	 */
	public static byte[] intToByte(int num) {
	
		byte[] arr = new byte[4];
		for (int i = 0; i < 4; i++) {
			arr[i] = (byte) (num >> 8 * i & 0xFF);
		}
		return arr;
	}

	/**
	 * Covert byte array to int
	 * @param byte[]  The byte array to be converted
	 * @return int
	 */
	public static int byteToInt(byte[] arr) {
	
		int num = 0;
		for (int i = 0; i < arr.length; i++) {
			num += (arr[i] & 0xFF) << (8 * i);
		}
	
		return num;
	}

	/**
	 * Covert short to byte array
	 * @param num  The number to be converted
	 * @return byte[]
	 */
	public static byte[] shortToByte(short num) {
	
		byte[] arr = new byte[2];
		for (int i = 0; i < 2; i++) {
			arr[i] = (byte) (num >> 8 * i & 0xFF);
		}
		return arr;
	}

	/**
	 * Covert byte array to short
	 * @param byte[]  The byte array to be converted
	 * @return short
	 */
	public static short byteToShort(byte[] arr) {
		
		short num = 0;
		if(arr.length == 2) {

			for (int i = 0; i < arr.length; i++) {
				num += (arr[i] & 0xFF) << (8 * i);
			}
		}

		return num;
	}
}
