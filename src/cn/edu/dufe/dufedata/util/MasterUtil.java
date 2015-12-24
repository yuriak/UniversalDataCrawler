package cn.edu.dufe.dufedata.util;
//-_-我为何要写这么一堆Util类？？？？
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.tagplugins.jstl.If;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import cn.edu.dufe.dufedata.bean.PluginBean;
import cn.edu.dufe.dufedata.controller.NodeController;
import cn.edu.dufe.dufedata.node.Node;
import cn.edu.dufe.dufedata.plugin.Plugin;
import cn.edu.dufe.dufedata.plugin.PluginStatus;

public class MasterUtil {
	
	private static Log log=LogFactory.getLog(NodeController.class);
	//发送的具体过程
	public static boolean sendAcktoSlaves(Node self, Node slave){
		try {
			Map<String, String> dataMap=MyMapUtils.fillMap("mName",self.getName(),"mAddr",self.getAddress(),"sName",slave.getName(),"sAddr",slave.getAddress());
			String result=PostUtil.post("http://"+slave.getAddress()+"/NAPI/ack.do", dataMap);
			//接收返回的结果
			if (!result.isEmpty()&&result.startsWith("{")) {
				JSONObject resultObject=new JSONObject(result);
				if (resultObject.get("ack").equals("true")&&resultObject.get("error").equals("")) {
					JSONArray pluginArray=resultObject.getJSONArray("plugins");
					JSONArray fileArray=resultObject.getJSONArray("files");
					//维护slave的plugin列表
					if (pluginArray.length()>0) {
						if (slave.getPlugins()!=null) {
							for (int i = 0; i < pluginArray.length(); i++) {
								JSONObject pluginObject=pluginArray.getJSONObject(i);
								String pluginID=pluginObject.get("pID").toString();
								Integer pluginStatus=Integer.valueOf(pluginObject.get("pStatus").toString());
								String pluginAuthor=pluginObject.get("pAuthor").toString();
								String pluginVersion=pluginObject.get("pVersion").toString();
								if (slave.getPlugins().containsKey(pluginID)) {
									slave.getPlugins().get(pluginID).setPluginStatus(pluginStatus);
								}else {
									PluginBean pluginBean=new PluginBean();
									pluginBean.setPluginAuthor(pluginAuthor);
									pluginBean.setPluginVersion(pluginVersion);
									pluginBean.setPluginID(pluginID);
									pluginBean.setPluginStatus(pluginStatus);
									slave.getPlugins().put(pluginID, pluginBean);
								}
							}
						}
					}
					//维护文件列表
					if (fileArray.length()>0) {
						if (slave.getFileList()!=null) {
							for (int i = 0; i < fileArray.length(); i++) {
								JSONObject fileObject=fileArray.getJSONObject(i);
								String url=fileObject.get("url").toString();
								String pluginID=url.split("/")[4];
								String fileName=url.split("/")[5];
								downloadResultFile(url, pluginID, fileName);
							}
						}
					}
					slave.getFileList().clear();
					return true;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//发送启动命令
	public static boolean sendStartOrder(String slaveAddress,String pluginID,String[] args){
		//用来装参数
		StringBuilder argBuilder=new StringBuilder(); 
		if (args!=null&&args.length>0) {
			for (int i = 0; i < args.length; i++) {
				if (i!=args.length-1) {
					argBuilder.append(args[i]+" ");
				}else {
					argBuilder.append(args[i]);
				}
			}
		}
		try {
			//用我的maputil把pluginid和参数都塞进去
			Map<String, String> params=MyMapUtils.fillMap("pID",pluginID,"args",argBuilder.toString());
			//然后发出去，等回复
			String result=PostUtil.post("http://"+slaveAddress+"/NAPI/startPlugin.do", params);
			if (result!=null&&result.startsWith("{")) {
				//然后把回复解析一下
				JSONObject resultObject=new JSONObject(result);
				String resString=resultObject.get("result").toString();
				String errorString=resultObject.getString("error").toString();
				if (resString.equals("success")) {
					log.info("starting remote plugin: "+pluginID+" "+resString);
					return true;
				}else {
					log.info("start remote plugin: "+pluginID+" "+resString);
					log.info("error: "+errorString);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//跟启动插件差不多
	public static boolean sendStopOrder(String slaveAddress,String pluginID){
		try {
			Map<String, String> params=MyMapUtils.fillMap("pID",pluginID);
			String result=PostUtil.post("http://"+slaveAddress+"/NAPI/stopPlugin.do", params);
			if (result!=null&&result.startsWith("{")) {
				JSONObject resultObject=new JSONObject(result);
				String resString=resultObject.get("result").toString();
				String errorString=resultObject.getString("error").toString();
				if (resString.equals("success")) {
					log.info("stop plugin: "+pluginID+" "+resString);
					return true;
				}else {
					log.info("stop plugin: "+pluginID+" "+resString);
					log.info("error: "+errorString);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	//下载结果文件
	private static void downloadResultFile(final String url,final String pluginID,final String fileName){
		//启动一个线程下载，以防止心跳线程被阻塞
		Thread downloadThread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					log.info("downloading file: "+fileName);
					Response response=Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).execute();
					FileUtils.writeByteArrayToFile(new File("data"+File.separator+pluginID+File.separator+fileName), response.bodyAsBytes(), false);
					FileUtils.writeByteArrayToFile(new File("WebRoot"+File.separator+"data"+File.separator+pluginID+File.separator+fileName), response.bodyAsBytes(),false);
					log.info("file: "+fileName+" downloaded");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		downloadThread.start();
	}
}
