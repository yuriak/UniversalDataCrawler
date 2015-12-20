package cn.edu.dufe.dufedata.util;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;

public class LogFileUtil {
	private static LogFileUtil util;
	private File logFile;
	private StringBuffer buffer=new StringBuffer();
	//单例化
	public static LogFileUtil getInstance(){
		if (util==null) {
			synchronized (LogFileUtil.class) {
				if (util==null) {
					util=new LogFileUtil();
				}
			}
		}
		return util;
	}
	
	private LogFileUtil(){
		String time=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		logFile=new File("log/log_"+time+".txt");
	}
	//写入日志
	public void addLog(String logString){
		try {
			
			FileUtils.write(logFile, logString, "utf-8", true);
			FileUtils.copyFile(logFile, new File("WebRoot/log/"+logFile.getName()));
			//写入的时候要判断文件大小，用于切割
			checkFileSize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void checkFileSize(){
		if (FileUtils.sizeOf(logFile)>4096000) {
			String time=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
			logFile=new File("log/log_"+time+".txt");
		}
	}
}
