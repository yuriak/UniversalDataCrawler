package cn.edu.dufe.dufedata.servlet;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import cn.edu.dufe.dufedata.controller.MainController;
import cn.edu.dufe.dufedata.controller.NodeController;
import cn.edu.dufe.dufedata.node.Node;
import cn.edu.dufe.dufedata.plugin.Plugin;

//此处用了jaxrs作为api，用起来比普通的servlet简单
@Produces("application/json;charset=UTF-8")
@Path("")
public class NodeAPI {
	MainController controller=MainController.getInstance();
	NodeController nodeController=NodeController.getInstance();
	//心跳状态API
	@POST
	@Path("ack.do")
	public String ack(@FormParam("mName") String masterName,
			@FormParam("mAddr") String masterAddress,
			@FormParam("sName") String slaveName,
			@FormParam("sAddr") String slaveAddress){
		if (!masterName.isEmpty()&&!masterAddress.isEmpty()&&!slaveName.isEmpty()&&!slaveAddress.isEmpty()) {
			//调用nodeController的回应Master函数
			return nodeController.AckMaster(masterName, masterAddress, slaveName, slaveAddress);
		}
		System.out.println("here");
		return "{ack:\"false\",error:\"incorrect massage\"}";
		
	}
	//启动插件
	@POST
	@Path("startPlugin.do")
	public String starPlugin(@FormParam("pID") String pluginID,@FormParam("args") String args){
		Plugin plugin=controller.getOnePlugin(pluginID);
		JSONObject resultObject=new JSONObject();
		if (plugin!=null) {
			try {
				//调用mainController的crawl函数
				controller.crawl(plugin, args.split(" "));
				resultObject.put("result", "success");
				resultObject.put("error", "");
			} catch (Exception e) {
				e.printStackTrace();
				resultObject.put("result", "failed");
				resultObject.put("error", e.getMessage());
			}
		}else {
			resultObject.put("result", "failed");
			resultObject.put("error", "no such plugin");
		}
		return resultObject.toString();
	}
	//停止插件
	@POST
	@Path("stopPlugin.do")
	public String stopPlugin(@FormParam("pID") String pluginID){
		Plugin plugin=controller.getOnePlugin(pluginID);
		JSONObject resultObject=new JSONObject();
		if (plugin!=null) {
			try {
				//调用mainController函数
				controller.stop(plugin);
				resultObject.put("result", "success");
				resultObject.put("error", "");
			} catch (Exception e) {
				e.printStackTrace();
				resultObject.put("result", "failed");
				resultObject.put("error", e.getMessage());
			}
		}else {
			resultObject.put("result", "failed");
			resultObject.put("error", "no such plugin");
		}
		return resultObject.toString();
	}
	
}
