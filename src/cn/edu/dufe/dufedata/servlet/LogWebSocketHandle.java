package cn.edu.dufe.dufedata.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.websocket.ClientEndpoint;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import cn.edu.dufe.dufedata.controller.MainController;
import cn.edu.dufe.dufedata.util.TailLogThread;

@ServerEndpoint("/log")
public class LogWebSocketHandle {
	 private InputStream inputStream;
	 
	 @OnOpen
	 public void onOpen(Session session){
		 System.out.println("yes");
		 try {
	            // 执行tail -f命令
//	            inputStream = MainController.getInputStream();
	            // 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
	            TailLogThread thread = new TailLogThread(inputStream, session);
	            thread.start();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		 
	 }
	 
	 @OnClose
	    public void onClose() {
	        try {
	            if(inputStream != null)
	                inputStream.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 
	    @OnError
	    public void onError(Throwable thr) {
	        thr.printStackTrace();
	    }
}
