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
	public void init(String[] args) {
//		System.setOut(new WebUIPrintStream(System.out));
		uiPrintStream=new WebUIPrintStream(System.out);
		System.setOut(uiPrintStream);
		System.setErr(uiPrintStream);
		if (System.console()!=null) {
			System.console().writer().write("inited");
			System.console().writer().flush();
		}
		threadMap=new HashMap<>();
		loadPlugins();
	}
	@Override
	public void loadPlugins() {
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

	@Override
	public void crawl(final Plugin plugin,final String[] args) {
		if (plugin.getPluginStatus()==0) {
			Thread thread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
						plugin.setPluginStatus(1);
						plugin.init(args);
						plugin.crawl();
						File[] resultFiles=plugin.getResultFiles();
						if (resultFiles!=null&&resultFiles.length>0) {
							saveFileToWebRoot(plugin.getPluginID(), resultFiles);
						}
						File[] tfidfFiles=plugin.getTFIDFFiles();
						if (tfidfFiles!=null&&tfidfFiles.length>0) {
							saveFileToWebRoot(plugin.getPluginID(), tfidfFiles);
						}
						plugin.setPluginStatus(0);
				}
			});
			thread.start();
			threadMap.put(plugin.getPluginID(), thread);
		}
	}
	
	@Override
	public void crawl(final String pluginId,final String[] args) {
		for (final Plugin plugin : plugins) {
			if (plugin.getPluginID().equals(pluginId)&&plugin.getPluginStatus()==0) {
				Thread thread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
							plugin.setPluginStatus(1);
							plugin.init(args);
							plugin.crawl();
							File[] resultFiles=plugin.getResultFiles();
							if (resultFiles!=null&&resultFiles.length>0) {
								saveFileToWebRoot(plugin.getPluginID(), resultFiles);
							}
							File[] tfidfFiles=plugin.getTFIDFFiles();
							if (tfidfFiles!=null&&tfidfFiles.length>0) {
								saveFileToWebRoot(plugin.getPluginID(), tfidfFiles);
							}
							
							plugin.setPluginStatus(0);
						}
				});
				thread.start();
				threadMap.put(pluginId,thread);
				break;
			}
		}
	}
	
//	public void runAllPlugin(){
//		for (Plugin plugin : plugins) {
//			plugin.init(new String[]{});
//			plugin.crawl();
//		}
//	}
	
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void stop(Plugin plugin) {
		// TODO Auto-generated method stub
		if (threadMap.containsKey(plugin.getPluginID())&&plugin.getPluginStatus()==1) {
			plugin.setPluginStatus(0);
			
			threadMap.get(plugin.getPluginID()).stop();
			System.out.println(plugin.getPluginID()+"stopped");
			threadMap.remove(plugin.getPluginID());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void stop(String pluginID) {
		for (final Plugin plugin : plugins) {
			if (plugin.getPluginID().equals(pluginID)&&plugin.getPluginStatus()==1) {
				plugin.setPluginStatus(0);
				plugin.getCrawler().stop();
				threadMap.get(pluginID).stop();
				threadMap.remove(plugin.getPluginID());
			}
		}
	}
	
	private void saveFileToWebRoot(String pluginID,File[] file){
		try {
			if (file!=null&&file.length>0) {
				
				for (int i = 0; i < file.length; i++) {
					
					FileUtils.copyFile(file[i], new File("WebRoot/data/"+pluginID+"_"+file[i].getName()));
				}
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public File[] listDownloadFiles(){
		File[] files=FileUtils.getFile("WebRoot/data").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (!pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		return files;
	}
	
	public File[] listLogFiles(){
		File[] files=FileUtils.getFile("WebRoot/log").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (!pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		return files;
	}
	
//	public InputStream getInputStream(){
//		return this.inputStream;
//	}
	

}
