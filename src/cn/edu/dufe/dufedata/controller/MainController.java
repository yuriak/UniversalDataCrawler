package cn.edu.dufe.dufedata.controller;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import cn.edu.dufe.dufedata.plugin.Plugin;
import cn.edu.dufe.dufedata.util.PluginLoader;
import cn.edu.dufe.dufedata.util.WebUIPrintStream;

public class MainController implements IController {
	
	private static MainController controller;
	private ArrayList<Plugin> plugins;
	private HashMap<String,Thread> threadMap;
	private WebUIPrintStream uiPrintStream;
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
		//截获系统输出流
		uiPrintStream=new WebUIPrintStream(System.out);
		//将输出流设置给WebUIPrintStream
		System.setOut(uiPrintStream);
		System.setErr(uiPrintStream);
		if (System.console()!=null) {
			System.console().writer().write("DataCrawler Inited");
			System.console().writer().flush();
		}
		//线程池
		threadMap=new HashMap<>();
		loadPlugins();
	}
	@Override
	public void loadPlugins() throws Exception {
		plugins=PluginLoader.loadPlugins();
	}
	
//	public static void main(String[] args) {
//		MainController controller=new MainController();
//		controller.init(new String[]{});
//		File[] files=new File[2];
//		files[0]=new File("data/hxTfidf2015_11_27_15_32_17.csv");
//		files[1]=new File("data/hxTfidf2015_11_27_15_31_17.csv");
//		controller.saveFileToWebRoot("court", files);
//	}
	//通过plugin对象启动plugin
	@Override
	public void crawl(final Plugin plugin,final String[] args) throws Exception {
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
							saveFileToWebRoot(plugin.getPluginID(), resultFiles);
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
	//通过pluginID启动插件,方法同上
	@Override
	public void crawl(final String pluginId,final String[] args){
		for (final Plugin plugin : plugins) {
			if (plugin.getPluginID().equals(pluginId)&&plugin.getPluginStatus()==0) {
				Thread thread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							plugin.setPluginStatus(1);
							plugin.init(args);
							plugin.crawl();
							File[] resultFiles=plugin.getResultFiles();
							if (resultFiles!=null&&resultFiles.length>0) {
								saveFileToWebRoot(plugin.getPluginID(), resultFiles);
							}
							plugin.setPluginStatus(0);
							threadMap.remove(plugin.getPluginID());
						} catch (Exception e) {
							e.printStackTrace();
							plugin.setPluginStatus(0);
							threadMap.remove(plugin.getPluginID());
						}
							
					}
				});
				threadMap.put(pluginId,thread);
				thread.start();
				break;
			}
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
					saveFileToWebRoot(plugin.getPluginID(), resultFiles);
				}
			} catch (Exception e) {
				plugin.setPluginStatus(0);
				threadMap.remove(plugin.getPluginID());
				e.printStackTrace();
			}
			
			
		}
	}
	//同上，但是通过pluginID停止
	@SuppressWarnings("deprecation")
	@Override
	public synchronized void stop(String pluginID) {
		for (final Plugin plugin : plugins) {
			if (plugin.getPluginID().equals(pluginID)&&plugin.getPluginStatus()==1) {
				try {
					Thread listenerThread=new Thread(new Runnable() {
						
						@Override
						public void run() {
							while (true) {
								if (threadMap.get(plugin.getPluginID())!=null&&threadMap.get(plugin.getPluginID()).isAlive()) {
									plugin.setPluginStatus(-1);
								}else {
									break;
								}
									
							}
						}
					});
					listenerThread.start();
					plugin.stop();
					File[] resultFiles=plugin.getResultFiles();
					if (resultFiles!=null&&resultFiles.length>0) {
						saveFileToWebRoot(plugin.getPluginID(), resultFiles);
					}
					plugin.setPluginStatus(0);
					threadMap.remove(plugin.getPluginID());
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	//保存结果文件到WebRoot
	private void saveFileToWebRoot(String pluginID,File[] file){
		try {
			if (file!=null&&file.length>0) {
				for (int i = 0; i < file.length; i++) {
					if (file[i]!=null&&file[i].exists()&&file[i].canRead()) {
						FileUtils.copyFileToDirectory(file[i], new File("WebRoot/data/"+pluginID));
					}
				}
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	//获得WebRoot中用于下载的文件
	public File[] listDownloadFiles(String path){
		File[] files=FileUtils.getFile("WebRoot/data/"+path).listFiles();
		return files;
	}
	
	public File[] listLogFiles(){
		File[] files=FileUtils.getFile("WebRoot/log").listFiles();
		return files;
		
	}

}
