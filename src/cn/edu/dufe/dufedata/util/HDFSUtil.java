package cn.edu.dufe.dufedata.util;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import cn.edu.dufe.dufedata.common.CommonConfig;

public class HDFSUtil {
	Logger logger=Logger.getLogger(HDFSUtil.class);
	
	
	private static HDFSUtil hdfsUtil;
	private String serverAddress;
	private String port;
	private String username;
	private String HADOOP_PATH;
	private boolean useHdfs=false;
	private boolean inited=false;
	private FileSystem fileSystem;
	private HDFSUtil(){
		
	}
	
	public static HDFSUtil getInstance(){
		if (hdfsUtil==null) {
			synchronized (HDFSUtil.class) {
				if (hdfsUtil==null) {
					hdfsUtil=new HDFSUtil();
					hdfsUtil.initUtil();
				}
			}
		}
		return hdfsUtil;
	}
	
	private void initUtil(){
		File configFile=new File(CommonConfig.HDFS_CONFIG);
		try {
			List<String> configList =FileUtils.readLines(configFile);
			for (String string : configList) {
				String key=string.split("=")[0];
				String value=string.split("=")[1];
				if (key.equals("usehdfs")) {
					if (value.equals("true")) {
						useHdfs=true;
					}else if (value.equals("false")) {
						useHdfs=false;
					}
				}else if (key.equals("address")) {
					serverAddress=value;
				}else if (key.equals("port")) {
					port=value;
				}else if (key.equals("username")) {
					username=value;
				}
			}
			if (useHdfs==true&&serverAddress!=null&&port!=null&&username!=null) {
				fileSystem=FileSystem.get(new URI("hdfs://"+serverAddress+":"+port), new Configuration(), username);
				HADOOP_PATH="hdfs://"+serverAddress+":"+port;
//				fileSystem=FileSystem.get(new URI(HADOOP_PATH), new Configuration());
				logger.info("HDFSUtil inited");
			}
			inited=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
//		RemoteIterator fIterator=FileSystem.get(new URI("hdfs://192.168.10.10:8020"),new Configuration()).listFiles(new Path("/"), true);
//		while(fIterator.hasNext()){
//			System.out.println(fIterator.next().toString());
//		}
//		hdfsUtil.getInstance().uploadFiles(new File[]{new File("data/hexun_tfidfData.csv")}, "hx");;
	}
	
	public void uploadFile(File file,String pluginID){
		try {
			if (!inited||!useHdfs||fileSystem==null) {
				return;
			}
			logger.info("uploading file "+file.getName()+" to hdfs");
			fileSystem.copyFromLocalFile(new Path(file.getPath()), new Path("/DataCrawler/"+pluginID+"/"+file.getName()));
			logger.info("file "+file.getName()+" uploaded to hdfs");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void uploadFile(File file){
		
	}
	
	public void uploadFiles(File[] files,String pluginID){
		try {
			if (!inited||!useHdfs||fileSystem==null) {
				return;
			}
			for (File file : files) {
				logger.info("uploading file "+file.getName()+" to hdfs");
				fileSystem.copyFromLocalFile(new Path(file.getPath()), new Path("/DataCrawler/"+pluginID+"/"+file.getName()));
				logger.info("file "+file.getName()+" uploaded to hdfs");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
