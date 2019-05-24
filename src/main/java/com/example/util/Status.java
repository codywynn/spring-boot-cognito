package com.example.util;

public enum Status {
	UP(0), SAME(1), DOWN(2);

	/**
	 * Status of the Medication, Activity
	 */
	private int status;

	private Status(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
