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

import com.sun.jersey.spi.container.servlet.ServletContainer;

import cn.edu.dufe.dufedata.controller.MainController;
import cn.edu.dufe.dufedata.node.Node;
import cn.edu.dufe.dufedata.plugin.Plugin;
import cn.edu.dufe.dufedata.servlet.API;

public class Launcher {
	
	
	
	public static void main(String[] args) throws Exception {
		final MainController mainController=MainController.getInstance();
		if (args!=null) {
			if (!args[0].isEmpty()&&args[0].equals("-startserver")) {
				if (!args[1].isEmpty()) {
					if (args.length>2&&!args[2].isEmpty()&&args[2].equals("-dist")) {
						if (!args[3].isEmpty()&&args[3].equals("true")) {
							mainController.setMode(CommonConfig.DISTRIBUTED);
						}
					}
					
					mainController.init(args);
					
					startServer(Integer.valueOf(args[1]));
				}else {
					return ;
				}
				
			}else if (!args[0].isEmpty()&&args[0].equals("-startplugin")) {
				//启动插件，将剩下的参数发给MainController
				mainController.init(args);
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
	
	public static void startPlugin(String pluginID,String[] args){
		MainController controller=MainController.getInstance();
		try {
			controller.crawl(controller.getOnePlugin(pluginID), args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
