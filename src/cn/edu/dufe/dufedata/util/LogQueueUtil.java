package cn.edu.dufe.dufedata.util;

import java.util.concurrent.ArrayBlockingQueue;

import cn.edu.dufe.dufedata.controller.MainController;

public class LogQueueUtil {
	private String tmpLog="";
	private static ArrayBlockingQueue<String> logQueue=new ArrayBlockingQueue<>(5000);
	private static LogQueueUtil util=null;
	public static LogQueueUtil getInstance(){
		if (util == null) {    
            synchronized (LogQueueUtil.class) {    
               if (util == null) {    
            	   util = new LogQueueUtil();   
               }
            }    
        }    
        return util;
	}
	private LogQueueUtil(){}
	public String getLog(){
		if (!logQueue.isEmpty()) {
			try {
				StringBuffer buffer=new StringBuffer();
				for (int i = 0; i < logQueue.size(); i++) {
					buffer.append(logQueue.take()+"<br/>");
				}
				return buffer.toString();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		}else {
			return "";
		}
	}
	
	public void addLog(String logString){
		if (logQueue.size()==5000) {
			logQueue.clear();
		}
		logQueue.add(logString);
	}
	
	public int getQueueSize(){
		return logQueue.size();
	}
}
