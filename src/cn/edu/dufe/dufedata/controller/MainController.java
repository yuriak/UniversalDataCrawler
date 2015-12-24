package cn.edu.dufe.dufedata.controller;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.dufe.dufedata.common.CommonConfig;
import cn.edu.dufe.dufedata.node.Node;
import cn.edu.dufe.dufedata.plugin.Plugin;
import cn.edu.dufe.dufedata.util.PluginLoader;
import cn.edu.dufe.dufedata.util.WebUIPrintStream;

public class MainController implements IController {
	
	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	private static MainController controller;
	private ArrayList<Plugin> plugins;
	private HashMap<String,Thread> threadMap;
	private WebUIPrintStream uiPrintStream;
	private int mode=CommonConfig.SINGLE;
	private NodeController nodeController;
	private MainController(){
		
	}
	
	public static MainController getInstance(){
		if (controller == null) {    
            synchronized (MainController.class) {    
               if (controller == null) {    
            	   controller = new MainController();   
               }
            }    
        }    
        return controller;
	}
	
	@Override
	public void init(String[] args) throws Exception{
		/*
		 * 此处很重要！如果需要在IDE中调试，把
		 * uiPrintStream=new WebUIPrintStream(System.out);
		 * System.setOut(uiPrintStream);
		 * System.setErr(uiPrintStream);
		 * 这几行给注释掉，IDE的console才会显示。
		 */
		//截获系统输出流并将输出流设置给WebUIPrintStream
//		uiPrintStream=new WebUIPrintStream(System.out);
//		System.setOut(uiPrintStream);
//		System.setErr(uiPrintStream);
		if (System.console()!=null) {
			System.console().writer().write("DataCrawler Inited");
			System.console().writer().flush();
		}
		//线程池
		threadMap=new HashMap<>();
		loadPlugins();
		if (mode==CommonConfig.DISTRIBUTED) {
			nodeController=NodeController.getInstance();
			nodeController.initNode();
		}
	}
	
	@Override
	public void loadPlugins() throws Exception {
		plugins=PluginLoader.loadPlugins();
	}
	
	@Override
	public void crawl(final Plugin plugin,final String[] args) throws Exception {
		if (plugin==null) {
			return;
		}
		if (plugin.getPluginStatus()==0) {
			Thread thread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						//设置状态为启动状态
						plugin.setPluginStatus(1);
						plugin.init(args);
						plugin.crawl();
						//抓取完成后收回结果文件
						File[] resultFiles=plugin.getResultFiles();
						if (resultFiles!=null&&resultFiles.length>0) {
							//将结果文件存入WebRoot
							saveFileToDataRoot(plugin.getPluginID(), resultFiles);
						}
						plugin.setPluginStatus(0);
						//从线程池中移除该线程
						threadMap.remove(plugin.getPluginID());
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						plugin.setPluginStatus(0);
						threadMap.remove(plugin.getPluginID());
					}
						
				}
			});
			//在开始时将该线程放入线程池
			threadMap.put(plugin.getPluginID(), thread);
			thread.start();
			
		}
	}
	
	
	public ArrayList<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(ArrayList<Plugin> plugins) {
		this.plugins = plugins;
	}
	
	public Plugin getOnePlugin(String pluginId){
		for (Plugin plugin : plugins) {
			if (plugin.getPluginID().equals(pluginId)) {
				return plugin;
			}
		}
		return null;
	}
	
	//停止插件
	@SuppressWarnings("deprecation")
	@Override
	public synchronized void stop(final Plugin plugin) {
		if (threadMap.containsKey(plugin.getPluginID())&&plugin.getPluginStatus()==1) {
			try {
				//监听插件状态的线程
				Thread listenerThread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						while (true) {
							if (threadMap.get(plugin.getPluginID())!=null) {
								//同步threadMap，防止出现该线程已经被移除的情况
								synchronized (threadMap) {
									if (threadMap.get(plugin.getPluginID())!=null) {
										if (threadMap.get(plugin.getPluginID())!=null&&threadMap.get(plugin.getPluginID()).isAlive()) {
											//设置plugin状态为停止中
											plugin.setPluginStatus(-1);
										}
									}else {
										//设置状态为停止，移除该线程
										plugin.setPluginStatus(0);
										threadMap.remove(plugin.getPluginID());
										break;
									}
								}
							}else {
								plugin.setPluginStatus(0);
								threadMap.remove(plugin.getPluginID());
								break;
							}
						}
					}
				});
				//开始监听并开始停止插件
				listenerThread.start();
				plugin.stop();
				//最终要把结果文件拿回
				File[] resultFiles=plugin.getResultFiles();
				if (resultFiles!=null&&resultFiles.length>0) {
					saveFileToDataRoot(plugin.getPluginID(), resultFiles);
				}
			} catch (Exception e) {
				plugin.setPluginStatus(0);
				threadMap.remove(plugin.getPluginID());
				e.printStackTrace();
			}
			
			
		}
	}
		//保存结果文件到Data
	private void saveFileToDataRoot(String pluginID,File[] file){
		try {
			if (file!=null&&file.length>0) {
				for (int i = 0; i < file.length; i++) {
					if (file[i]!=null&&file[i].exists()&&file[i].canRead()) {
						//将文件复制并移动到系统和Webroot的data中
						FileUtils.copyFileToDirectory(file[i], new File("WebRoot/data/"+pluginID));
						FileUtils.moveFileToDirectory(file[i], new File("data/"+pluginID), true);
						//如果使用分布式，则把需要下载的文件装入本NODE的待下载列表
						if (mode==CommonConfig.DISTRIBUTED) {
							Node self=nodeController.getSelf();
							if (self.getFileList()!=null) {
								self.getFileList().add("http://"+self.getAddress()+"/data/"+pluginID+"/"+file[i].getName());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	//获得Data中用于下载的文件
	public File[] listDownloadFiles(String path){
		File[] files=FileUtils.getFile("WebRoot/data/"+path).listFiles();
		return files;
	}
	
	public File[] listLogFiles(){
		File[] files=FileUtils.getFile("WebRoot/log").listFiles();
		return files;
		
	}
	
}
