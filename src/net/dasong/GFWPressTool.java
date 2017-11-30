package net.dasong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import net.dasong.common.Constants;

public class GFWPressTool {

	public void dlUserText(String host) {
		String user = "root";
		String rfile = "/gfw.press/user.txt";
		String lfile = Constants.DATA_DIR + "/user.txt";

		// 命令行输入root用户密码
		String pwd = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		while (pwd.equals("")) {
			System.out.println("请输入服务器root用户密码: ");

			try {
				pwd = reader.readLine();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 启动用户列表下载
		Scp scp = new Scp(host, user, pwd, rfile, lfile);

		System.out.println("\n下载用户列表 开始 [" + rfile + "]");

		scp.from();

		System.out.println("下载用户列表 结束 [" + lfile + "]");
	}

	public void chgpwd(String host, String user) {

		try {
			JSch jsch = new JSch();
			String pwd = "";
			String newPwd = "";
			String newPwdChk = "";

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			while (pwd.equals("")) {
				System.out.println("请输入服务器root用户密码: ");

				try {
					pwd = reader.readLine();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			while (newPwd.equals("")) {
				System.out.println("请输入新密码: ");

				try {
					newPwd = reader.readLine();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			while (newPwdChk.equals("")) {
				System.out.println("请再次输入新密码: ");

				try {
					newPwdChk = reader.readLine();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!newPwd.equals(newPwdChk)) {
				System.out.println("两次密码输入不一致，程序退出");
				System.exit(0);
			}

			Session session = jsch.getSession(user, host, 22);

			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.setPassword(pwd);
			session.connect();

			Channel channel = session.openChannel("exec");

			// man sudo
			// -S The -S (stdin) option causes sudo to read the password from the
			// standard input instead of the terminal device.
			// -p The -p (prompt) option allows you to override the default
			// password prompt and use a custom one.
			((ChannelExec) channel).setCommand("passwd " + user);

			InputStream in = channel.getInputStream();
			OutputStream out = channel.getOutputStream();
			((ChannelExec) channel).setErrStream(System.err);

			channel.connect();

			out.write((newPwd + "\n").getBytes());
			out.flush();
			out.write((newPwdChk + "\n").getBytes());
			out.flush();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.println(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					int exitCode = channel.getExitStatus();
					String exitStatus = exitCode == 0 ? "成功" : "失败";
					System.out.println("exit-status: " + exitStatus + "[" + exitCode + "]");
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}

			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		// 操作类型
		String opType = "";

		// 主机IP
		String host = "";

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		while (host.equals("")) {
			System.out.println("请输入服务器IP: ");

			try {
				host = reader.readLine();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		while (opType.equals("")) {
			System.out.println("请输入操作类型: 1-下载用户列表 2-修改root用户密码");

			try {
				opType = reader.readLine();

				if (!opType.trim().equals("1") && !opType.trim().equals("2")) {
					opType = "";
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		GFWPressTool gfwPressToll = new GFWPressTool();

		if (opType.equals("1")) {
			gfwPressToll.dlUserText(host);
		} else if (opType.equals("2")) {
			gfwPressToll.chgpwd(host, "root");
		}

		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
