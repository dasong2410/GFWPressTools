package net.dasong;

import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo {
	public String getPassword() {
		return null;
	}

	public boolean promptYesNo(String str) {
		return true;
	}

	public String getPassphrase() {
		return null;
	}

	public boolean promptPassphrase(String message) {
		return true;
	}

	public boolean promptPassword(String message) {
		return false;
	}

	public void showMessage(String message) {
	}

	public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
			boolean[] echo) {
		return null;
	}
}
