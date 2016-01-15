package cn.edu.dufe.dufedata.controller;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.gargoylesoftware.htmlunit.javascript.host.PluginArray;

import cn.edu.dufe.dufedata.bean.PluginBean;
import cn.edu.dufe.dufedata.common.CommonConfig;
import cn.edu.dufe.dufedata.node.Node;
import cn.edu.dufe.dufedata.node.NodeRole;
import cn.edu.dufe.dufedata.node.NodeStatus;
import cn.edu.dufe.dufedata.plugin.Plugin;
import cn.edu.dufe.dufedata.plugin.PluginStatus;
import cn.edu.dufe.dufedata.util.MasterUtil;

public class NodeController {
/**
	 * @return the self
	 */
	public Node getSelf() {
		return self;
	}

	/**
	 * @param self the self to set
	 */
	public void setSelf(Node self) {
		this.self = self;
	}
	
	private static Logger log=Logger.getLogger(NodeController.class); 
	//本节点
	private Node self=new Node();
	private ArrayList<Node> slaves=new ArrayList<>();
	private Node master;
	private static NodeController nodeController;
	public static NodeController getInstance(){
		if (nodeController==null) {
			synchronized (NodeController.class) {
				if (nodeController==null) {
					nodeController=new NodeController();
				}
			}
		}
		return nodeController;
	}
	
	private NodeController(){
		
	}
	
	
	public void initNode(){
		try {
			log.info("using distributed mode");
			readConf();
			log.info("role:"+(self.getRole()==0?"master":"slave"));
			if (self.getRole()==NodeRole.MASTER) {
				ACKSlaves();
			}
//			startRemotePlugin(slaves.get(0), "hexun", new String[]{"-pageNumber","3"});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//心跳获取节点状态
	public void ACKSlaves(){
		Thread ackThread=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (slaves!=null&&slaves.size()>0) {
					while (true) {
						for (int i = 0; i < slaves.size(); i++) {
							if (slaves.get(i).getStatus()==NodeStatus.DEAD) {
								if (MasterUtil.sendAcktoSlaves(self, slaves.get(i))) {
									slaves.get(i).setStatus(NodeStatus.ALIVE);
									log.info(slaves.get(i).getName()+" connect successful!");
								}
							}else {
								if (MasterUtil.sendAcktoSlaves(self, slaves.get(i))) {
									slaves.get(i).setStatus(NodeStatus.ALIVE);
								}else {
									slaves.get(i).setStatus(NodeStatus.DEAD);
									log.warn(slaves.get(i).getName()+" lost connection!");
								}
							}
						}
						try {
							//3秒一次，太频繁不好
							Thread.currentThread().sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		ackThread.start();
	}
	//slave回应master的函数
	public String AckMaster(String masterName,String masterAddress, String slaveName,String slaveAddress){
		JSONObject ackObject=new JSONObject();
		if (masterName.equals(master.getName())&&masterAddress.equals(master.getAddress())&&slaveName.equals(self.getName())&&slaveAddress.equals(self.getAddress())) {
			if (master.getStatus()==NodeStatus.DEAD) {
				master.setStatus(NodeStatus.ALIVE);
				log.info(master.getName()+" connect successful!");
			}
			JSONArray pluginArray=new JSONArray();
			JSONArray fileArray=new JSONArray();
			MainController controller=MainController.getInstance();
			ArrayList<Plugin> plugins=controller.getPlugins();
			ArrayList<String> files=self.getFileList();
			//放入插件信息
			for (Plugin plugin : plugins) {
				JSONObject pluginObject=new JSONObject();
				pluginObject.put("pID", plugin.getPluginID());
				pluginObject.put("pStatus", plugin.getPluginStatus());
				pluginObject.put("pAuthor", plugin.getPluginAuthor());
				pluginObject.put("pVersion", plugin.getPluginVersion());
				pluginArray.put(pluginObject);
			}
			//放入文件信息
			for (String url : files) {
				JSONObject fileObject=new JSONObject();
				fileObject.put("url",url );
				fileArray.put(fileObject);
			}
			//清空待下载文件
			files.clear();
			ackObject.put("plugins", pluginArray);
			ackObject.put("files", fileArray);
			ackObject.put("ack", "true");
			ackObject.put("error", "");
		}
		else {
			ackObject.put("ack", "false");
			ackObject.put("error", "incorrect massage");
		}
		return ackObject.toString();
	}
	//启动一个远程插件
	public void startRemotePlugin(final Node slave,final String pluginID,String[] args){
		if (slave.getStatus()==NodeStatus.ALIVE&&slave.getPlugins().containsKey(pluginID)) {
			if (slave.getPlugins().get(pluginID).getPluginStatus()==PluginStatus.STOP) {
				if (MasterUtil.sendStartOrder(slave.getAddress(), pluginID, args)) {
					log.info("remote plugin "+slave.getName()+" : "+pluginID+" started");
				}
			}
		}
	}
	//停止一个远程插件
	public void stopRemotePlugin(Node slave,String pluginID){
		if (slave.getStatus()==NodeStatus.ALIVE&&slave.getPlugins().containsKey(pluginID)) {
			if (slave.getPlugins().get(pluginID).getPluginStatus()==PluginStatus.RUNNING) {
				if (MasterUtil.sendStopOrder(slave.getAddress(), pluginID)) {
					log.info("stopping remote plugin "+slave.getName()+" : "+pluginID);
				}
			}
		}
	}
	

	public ArrayList<Node> getAllSlaves(){
		return slaves;
	}
	
	public Node getOneSlave(String slaveName){
		for (Node slave : slaves) {
			if (slave.getName().equals(slaveName)) {
				return slave;
			}
		}
		return null;
	}
	
	//读取配置文件
	private void readConf() throws IOException{
		File confFile=new File(CommonConfig.CLUSTER_CONFIG);
		ArrayList<String> confString=(ArrayList<String>) FileUtils.readLines(confFile);
		for (String string : confString) {
			if (string.startsWith("role")) {
				if (string.split("=")[1].equals("master")) {
					self.setRole(NodeRole.MASTER);
					
				}else if (string.split("=")[1].equals("slave")) {
					self.setRole(NodeRole.SLAVE);
				}
			}
			if (string.startsWith("IP")) {
				self.setAddress(string.split("=")[1]);
			}
			if (string.startsWith("name")) {
				self.setName(string.split("=")[1]);
			}
			if (string.startsWith("slave")) {
				Node slave=new Node();
				slave.setName(string.split("=")[1].split("@")[0]);
				slave.setAddress(string.split("=")[1].split("@")[1]);
				slave.setRole(NodeRole.SLAVE);
				slave.setStatus(NodeStatus.DEAD);
				ArrayList<String> fileList=new ArrayList<>();
				slave.setFileList(fileList);
				HashMap<String, PluginBean> plugins=new HashMap<>();
				slave.setPlugins(plugins);
				slaves.add(slave);
			}
			if (string.startsWith("master")) {
				master=new Node();
				master.setRole(NodeRole.MASTER);
				master.setName(string.split("=")[1].split("@")[0]);
				master.setAddress(string.split("=")[1].split("@")[1]);
				ArrayList<String> fileList=new ArrayList<>();
				master.setFileList(fileList);
			}
		}
		ArrayList<String> fileList=new ArrayList<>();
		self.setFileList(fileList);
		
	}
	
	public ArrayList<Node> getSlaves(){
		return this.slaves;
	}
	
	public Node getMaster(){
		return this.master;
	}
	
}
