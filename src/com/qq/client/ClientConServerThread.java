/**
 * 功能：客户端连接服务器线程
 */
package com.qq.client;

import java.net.*;
import java.io.*;

import com.qq.common.Message;
import com.qq.common.MessageType;

public class ClientConServerThread extends Thread {
	private Socket s;

	// 构造方法
	public ClientConServerThread(Socket s) {
		this.s = s;
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public void run() {
		while (true) {
			// 不停的读取从服务器发来的消息
			try {
				ObjectInputStream ois = new ObjectInputStream(
						s.getInputStream());
				Message m = (Message) ois.readObject();
				System.out.println("读取从服务器发来的消息" + m.getSender() + "给"
						+ m.getGetter() + "内容：" + m.getContent());

				if (m.getMesType().equals(MessageType.MESSAGE_COMM)) {
					// 普通包
					// 从服务器获得的消息显示到相应的聊天界面
					ClientChatView qqChat = ManageQQChat.getQQChat(m.getGetter() + " "
							+ m.getSender());
					// 显示
					qqChat.showMessage(m);
				} else if (m.getMesType().equals(
						MessageType.MESSAGE_RET_ONLINEFRIEND)) {
					// 返回在线好友的包
					System.out.println("客户端接收到好友的状态：" + m.getContent());
					String con = m.getContent();
					// getter是相对于服务器的接收者，也就是自己的QQ
					String getter = m.getGetter();
					System.out.println("getter=" + getter);
					
					// 修改相应的好友列表
					QQFriendList qqFriendList = ManageQQFriendList
							.getQQFriendList(getter);

					// 更新在线好友
					if (qqFriendList != null) {
						qqFriendList.updateFriend(m);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
