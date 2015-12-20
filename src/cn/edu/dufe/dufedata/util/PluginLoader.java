package cn.edu.dufe.dufedata.util;

import java.awt.List;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.ClassPathUtils;
import org.apache.commons.lang3.ClassUtils;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.webapp.JarScanner;
import org.json.JSONObject;

import cn.edu.dufe.dufedata.common.CommonConfig;
import cn.edu.dufe.dufedata.plugin.IPlugin;
import cn.edu.dufe.dufedata.plugin.Plugin;

public class PluginLoader  {
	public static ArrayList<Plugin> loadPlugins() throws Exception{
		
		File[] pluginFolders;
		ArrayList<Plugin> plugins=new ArrayList<>();
		pluginFolders=FileUtils.getFile(CommonConfig.PLUGIN_FOLDER).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		//遍历每一个目录
		for (int i = 0; i < pluginFolders.length; i++) {
			//获取插件信息
			File pluginInfoFile=FileUtils.getFile(new File(pluginFolders[i].getAbsolutePath()+"/plugin.info"));
			String pluginID="";
			String pluginVersion="";
			String pluginAuthor="";
			String info=FileUtils.readFileToString(pluginInfoFile,"utf-8");
			//赋值
			JSONObject infoObject=new JSONObject(info);
			pluginID=infoObject.getString("id");
			pluginVersion=infoObject.getString("version");
			pluginAuthor=infoObject.getString("author");
			
			File libFolder=null;
			//库目录
			File[] libFolders=pluginFolders[i].listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.isDirectory()&&pathname.getName().equals("lib")) {
						return true;
					}
					return false;
				}
			});
			
			if (libFolders!=null&&libFolders.length>0) {
				libFolder=libFolders[0];
			}
			//加载库文件
			File[] libFiles=null;
			if (libFolder!=null) {
				libFiles=libFolder.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						if (pathname.getName().endsWith(".jar")) {
							return true;
						}
						return false;
					}
				});
				JarFile[] libjarFiles=new JarFile[libFiles.length];
				URL[] urls=new URL[libFiles.length];
				for (int j = 0; j < libjarFiles.length; j++) {
					libjarFiles[j]=new JarFile(libFiles[j]);
					urls[j]=libFiles[j].toURI().toURL();
				}
				URLClassLoader classLoader=new URLClassLoader(urls);
				for (int j = 0; j < libjarFiles.length; j++) {
					Enumeration<JarEntry> entries=libjarFiles[j].entries();
					while (entries.hasMoreElements()) {
						JarEntry jarEntry = (JarEntry) entries.nextElement();
						if (jarEntry.getName().endsWith(".class")) {
							//将类名转换后载入类
							String fullClassName=jarEntry.getName().replaceAll("/", ".").split(".class")[0];
							classLoader.loadClass(fullClassName);
						}
					}
				}
			}
			//载入插件
			File pluginFile=pluginFolders[i].listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().endsWith(".jar")) {
						return true;
					}
					return false;
				}
			})[0];
			JarFile pluginJarfile=new JarFile(pluginFile);
			URL pluginURL=pluginFile.toURI().toURL();
			URLClassLoader pluginLoader=new URLClassLoader(new URL[]{pluginURL});
			Enumeration<JarEntry> entries=pluginJarfile.entries();
			Plugin plugin=null;
			
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) entries.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					String classFullName = jarEntry.getName().replaceAll("/", ".").split(".class")[0];
					//载入插件的类文件
					Class<?> myclass = pluginLoader.loadClass(classFullName);
					//如果这个类文件是Plugin类的子类，就实例化它
					if (Plugin.class.isAssignableFrom(myclass)) {
						Constructor<?> constructor=myclass.getDeclaredConstructor(String.class,String.class,String.class);
						constructor.setAccessible(true);
						plugin=(Plugin) constructor.newInstance(pluginID,pluginVersion,pluginAuthor);
					}
				}
			}
			File[] readmeFiles=pluginFolders[i].listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().equals("readme.txt")) {
						return true;
					}
					return false;
				}
			});
			if (readmeFiles!=null&&readmeFiles.length>0) {
				plugin.setReadmeFile(readmeFiles[0]);
			}
			plugins.add(plugin);
		}
		//去重
		ArrayList<Plugin> resultPlugins=new ArrayList<>();
		for (Plugin plugin : plugins) {
			if (!resultPlugins.contains(plugin)) {
				//
				resultPlugins.add(plugin);
			}
		}
		return plugins;
	}
	
}
