package com.example.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtil {

	private static Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

	static String CHAR_L = "abcdefghijklmnopqrstuvwxyz";
	static String CHAR_U = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static String NUM = "1234567890";
	static String CHAR_S = "!@#$%^&*()_=+";
	static final int PASSWORD_LENGTH = 8;

	/**
	 * The method generates the temporary password for the user
	 * 
	 * @return
	 */
	public static String generateTemporaryPassword() {
		logger.debug("Generating the temporary password");
		StringBuffer randPass = new StringBuffer();
		char ch;
		for (int i = 0; i < PASSWORD_LENGTH; i++) {
			if (randPass.length() < PASSWORD_LENGTH) {
				ch = CHAR_L.charAt(getRandomNumber(CHAR_L.length()));
				randPass.append(ch);
			}
			if (randPass.length() < PASSWORD_LENGTH) {
				ch = CHAR_U.charAt(getRandomNumber(CHAR_U.length()));
				randPass.append(ch);
			}
			if (randPass.length() < PASSWORD_LENGTH) {
				ch = NUM.charAt(getRandomNumber(NUM.length()));
				randPass.append(ch);
			}
			if (randPass.length() < PASSWORD_LENGTH) {
				ch = CHAR_S.charAt(getRandomNumber(CHAR_S.length()));
				randPass.append(ch);
			}
		}
		logger.debug("Password has been generated : {}", randPass.toString());
		return randPass.toString();
	}

	/**
	 * The method generates a random number
	 * @param index
	 * @return
	 */
	private static int getRandomNumber(int index) {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(index - 1);
		return randomInt;
	}
}
