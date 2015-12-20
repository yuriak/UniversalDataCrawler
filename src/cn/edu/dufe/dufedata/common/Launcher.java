package cn.edu.dufe.dufedata.common;

import java.io.Console;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.impl.Log4JLogger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import cn.edu.dufe.dufedata.controller.MainController;
import cn.edu.dufe.dufedata.plugin.Plugin;
import cn.edu.dufe.dufedata.servlet.API;

public class Launcher {
	public static void main(String[] args) throws Exception {
		final MainController mainController=MainController.getInstance();
		mainController.init(args);
		if (args!=null) {
			if (!args[0].isEmpty()&&args[0].equals("-startserver")) {
				if (!args[1].isEmpty()) {
					startServer(Integer.valueOf(args[1]));
				}else {
					startServer(54321);
				}
			}else if (!args[0].isEmpty()&&args[0].equals("-startplugin")) {
				//启动插件，将剩下的参数发给MainController
				startPlugin(args[1], shrinkArgs(shrinkArgs(args)));
			}
		}
	}
	
	public static void startServer(int port) throws Exception{
		Server server = new Server(port);
		WebAppContext webAppContext = new WebAppContext();
		WebAppContext context=new WebAppContext();
		context.setContextPath(".");
		context.setResourceBase("WebRoot");
		server.setHandler(context);
		server.start();
		server.join();
	}
	
	public static void startPlugin(String pluginName,String[] args){
		MainController controller=MainController.getInstance();
		controller.crawl(pluginName, args);
	}
	//缩短参数
	private static String[] shrinkArgs(String[] args){
		String[] resultArg=new String[args.length-1];
			for (int i = 0; i < resultArg.length; i++) {
				resultArg[i]=args[i+1];
			}
			return resultArg;
		}
}
