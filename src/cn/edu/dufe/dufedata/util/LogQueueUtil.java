package cn.edu.dufe.dufedata.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import cn.edu.dufe.dufedata.bean.LogBean;
import cn.edu.dufe.dufedata.controller.MainController;

public class LogQueueUtil {
	private String tmpLog="";
	private static ArrayList<String> logQueue=new ArrayList<>();
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
	//通过游标获取日志
	public LogBean getLog(int currentCursor){
		if (!logQueue.isEmpty()) {
			StringBuffer buffer=new StringBuffer();
			int i = currentCursor;
			LogBean bean=new LogBean();
			for (; i < logQueue.size(); i++) {
				//返回游标之后的所有日志
				buffer.append(logQueue.get(i)+"<br/>");
			}
			bean.setCurrentCursor(i);
			bean.setLog(buffer.toString());
			return bean;
		}else {
			return null;
		}
	}
	
	public void addLog(String logString){
		//如果队列满了，就清空，然后再重新加入
		if (logQueue.size()==10000) {
			logQueue.clear();
		}
		logQueue.add(logString);
		
	}
	
	public int getQueueSize(){
		return logQueue.size();
	}
}
