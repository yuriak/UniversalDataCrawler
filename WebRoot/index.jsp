<%@page import="cn.edu.dufe.dufedata.node.NodeStatus"%>
<%@page import="cn.edu.dufe.dufedata.bean.PluginBean"%>
<%@page import="cn.edu.dufe.dufedata.node.NodeRole"%>
<%@page import="cn.edu.dufe.dufedata.node.Node"%>
<%@page import="cn.edu.dufe.dufedata.controller.NodeController"%>
<%@page import="cn.edu.dufe.dufedata.plugin.Plugin"%>
<%@page import="cn.edu.dufe.dufedata.controller.MainController"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
MainController controller=MainController.getInstance();
NodeController nodeController=NodeController.getInstance();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>DufeDataCrawler</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="./css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="./css/dashboard.css">
	<link rel="stylesheet" type="text/css" href="./css/style.css">
	<script type="text/javascript" src="./js/bootstrap.js"></script>
	<script type="text/javascript" src="./js/holder.min.js"></script>
	<script type="text/javascript" src="./js/ie-emulation-modes-warning.js"></script>
	<script type="text/javascript" src="./js/ie10-viewport-bug-workaround.js"></script>
	<script type="text/javascript" src="./js/jquery-1.11.3.js"></script>
	<script type="text/javascript" src="./js/jquery.timer.js"></script>
	<script type="text/javascript" src="./js/jquery.placeholder.min.js"></script>
	<script type="text/javascript" src="./js/jquery.xdomainrequest.min.js"></script>
  </head>
  
  <body>
        <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
        <div class="navbar-header">
          <a class="navbar-brand" href="#">DufeDataCrawler</a>
        </div>
      </div>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <div class="col-sm-3 col-md-2 sidebar" style="background-color: black;">
          <ul class="nav nav-sidebar">
            <li class="active"><a href="index.jsp">Plugin <span class="sr-only">(current)</span></a></li>
            <li><a href="files.jsp">Result</a></li>
            <li><a href="log.jsp">Log</a></li>
          </ul>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Dashboard</h1>
			<hr/>
          <h2 class="sub-header">Plugins</h2>
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Author</th>
                  <th>Version</th>
                  <th>Location</th>
                  <th>Status</th>
                  <th>Operations</th>
                  <th>Params</th>
                </tr>
              </thead>
              <tbody>
              <% 
              for(Plugin plugin:controller.getPlugins()){
               %>
                <tr>
                  <td><%=plugin.getPluginID() %></td>
                  <td><%=plugin.getPluginAuthor() %></td>
                  <td><%=plugin.getPluginVersion() %></td>
                  <td>local</td>
                  <td class="status" id="st_local_<%=plugin.getPluginID()%>"></td>
                  <td><a class="operation" id="op_local_<%=plugin.getPluginID()%>" href="#">start</a> | <a href="readme.jsp?pluginID=<%=plugin.getPluginID()%>" target="_blank">readme</a></td>
                  <td><input class="param" type="text" id="param_local_<%=plugin.getPluginID()%>"></input></td>
                </tr>
                <%
                }
                %>
                 <hr/>
                <%
                if(nodeController.getSelf().getRole()==NodeRole.MASTER)
                {
                for(Node slave : nodeController.getSlaves()){
                if(slave.getStatus()==NodeStatus.ALIVE){
                	Set<String> key=slave.getPlugins().keySet();
                	Iterator<String> iterator=key.iterator();
                	while(iterator.hasNext()){
                	PluginBean pluginBean=slave.getPlugins().get(iterator.next());
                %>
                  <tr>
                  <td><%=pluginBean.getPluginID() %></td>
                  <td><%=pluginBean.getPluginAuthor() %></td>
                  <td><%=pluginBean.getPluginVersion() %></td>
                  <td><%=slave.getName() %></td>
                  <td class="status" id="st_<%=slave.getName() %>_<%=pluginBean.getPluginID()%>"></td>
                  <td><a class="operation" id="op_<%=slave.getName() %>_<%=pluginBean.getPluginID()%>" href="#">start</a> | <a href="readme.jsp?pluginID=<%=pluginBean.getPluginID()%>" target="_blank">readme</a></td>
                  <td><input class="param" type="text" id="param_<%=slave.getName() %>_<%=pluginBean.getPluginID()%>"></input></td>
                </tr>
               	<%
                  		}
                 	}
                 	}
               	}
                %>
              </tbody>
            </table>
			</div>
			<div class="container-fluid">
           <h2 class="sub-header">Log</h2>
           <div  id="log" style=" height:200px; overflow: scroll; background-color: black">
   			</div>
			</div> <!-- #log-container -->
			</div>
        </div>
    </div>
  </body>
</html>

<script type="text/javascript">
$(".operation").click(function(e){
	var list=$(".param");
	for(i=0;i<list.size();i++){
		loc=$(list.get(i)).attr("id").split("_")[1];
		pid=$(list.get(i)).attr("id").split("_")[2];
		if(loc==$(e.target).attr("id").split("_")[1]&&pid==$(e.target).attr("id").split("_")[2]){
		console.log({ action:$(e.target).text(),location:loc,  pluginID: pid, param: $(list.get(i)).val() });
		    $.post("API", { action:$(e.target).text(),location:loc,  pluginID: pid, param: $(list.get(i)).val() }, function (result) {
                var res=$.parseJSON(result);
                if(res.error==""){
                	$(e.target).html("stop");
                }
		    });
		}
	}
    
});

/*String.prototype.format = function() {
    var newStr = this, i = 0;
    while (/%s/.test(newStr)) {
        newStr = newStr.replace("%s", arguments[i++])
    }
    return newStr;
}*/

$(function(){
		window.currentCursor=0;
		var timer = $.timer(function() {
		getStatus();
		getLog();
		//console.log(window.currentCursor);
        });
        timer.set({
            time: 500,
            autostart: true
        });
});

function getLog(){
         $.post("API", { action:"log",cursor:window.currentCursor }, function (result) {
         var logs=$.parseJSON(result);
         if(logs.log!=null&&logs.log!=""&&logs.log.length>0&&logs.log!="\\n"){
         logs.log.replace("\\n","<br/>");
               $("#log").append("<p style=\"color: green;\">"+logs.log+"</p>");
               $("#log").scrollTop($("#log")[0].scrollHeight);
               window.currentCursor=logs.lastCursor;
               console.log(window.currentCursor);
               }
	    });
}

function getStatus(){
	var statusRequest={action:"status"};
	$.ajax({
            type: 'POST',
            url: '/API',
            data: statusRequest,
            dataType: 'HTML',
            success: function(result) {
            	var res=JSON.parse(result).result;
            	for(i=0;i<res.length;i++){
            		var op=$(".operation");
            		for(j=0;j<op.size();j++){
            			var pal=res[i].loc+"_"+res[i].pluginID;
            			if(pal==$(op[j]).attr("id").split("_")[1]+"_"+$(op[j]).attr("id").split("_")[2]){
            				if(res[i].status==1){
            					$(op[j]).html("stop");
            					$("#st_"+pal).html("running");
            					$("#st_"+pal).attr("style","color:red;");
            				}else if(res[j].status==0){
            					$(op[j]).html("start");
            					$("#st_"+pal).html("stopped");
            					$("#st_"+pal).attr("style","color:black;");
            				}else if(res[j].status==-1){
            					$(op[j]).html("wait");
            					$("#st_"+pal).html("stopping");
            					$("#st_"+pal).attr("style","color:blue;");
            				}
            			}
            		}
            	}
            }});
}
</script>

