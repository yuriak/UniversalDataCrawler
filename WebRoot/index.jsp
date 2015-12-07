<%@page import="cn.edu.dufe.dufedata.plugin.Plugin"%>
<%@page import="cn.edu.dufe.dufedata.controller.MainController"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
MainController controller=MainController.getInstance();
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
                  <td class="status" id="st_<%=plugin.getPluginID()%>"></td>
                  <td><a class="operation" id="op_<%=plugin.getPluginID()%>" href="#">start</a> | <a href="readme.jsp?pluginID=<%=plugin.getPluginID()%>" target="_blank">readme</a></td>
                  <td><input class="param" type="text" id="param_<%=plugin.getPluginID()%>"></input></td>
                </tr>
                <%
                }
                 %>
              </tbody>
            </table>

			</div>
			<div class="container-fluid">
           <h2 class="sub-header">Log</h2>
           <div  id="log" style=" height:200px; overflow: scroll; background-color: black">
   			</div>
<style type="text/css">
    .DUMMY { color: black }
    .DEBUG { color: #21610b }
    .INFO { color: blue }
    .WARNING { color: #ff8000 }
    .ERROR { color: #fe2e2e }
    .CRITICAL { color: #d7df01 }
</style>
<!-- JavaScript -->
<script type="text/javascript">
    $('#log').scroll(function() {
        var preservedHeight = $('#log').height() + 10,
            scrollHeight    = $('#log')[0].scrollHeight,
            scrollTop       = $('#log').scrollTop();
        if ( scrollTop + preservedHeight == scrollHeight ) {
            window.isAutoScrollLog = true;
        } else {
            window.isAutoScrollLog = false;
        }
    });
</script>
<script type="text/javascript">
    function scrollLog() {
        if ( window.isAutoScrollLog ) {
            $('#log').scrollTop($('#log')[0].scrollHeight);
        }
    }
</script>
<script type="text/javascript">
	var count=10;
    function getLog(){
    	 	var pageRequests = {
            'action': 'log',
        };
         $.post("API", { action:"log" }, function (result) {
         if(result!=null&&result!=""&&result.length>0&&result!="\\n"){
         count=count+30;
         result.replace("\\n","<br/>");
               $("#log").append("<p style=\"color: green;\">"+result+"</p>");
               $("#log").scrollTop($("#log")[0].scrollHeight);
               }
	    });
    
    }
</script>
<script type="text/javascript">
    function getLogLevel(log) {
        if ( log.indexOf('[DEBUG]') != -1 ) {
            return 'DEBUG';
        } else if ( log.indexOf('[INFO]') != -1 ) {
            return 'INFO';
        } else if ( log.indexOf('[WARNING]') != -1 ) {
            return 'WARNING';
        } else if ( log.indexOf('[ERROR]') != -1 ) {
            return 'ERROR';
        } else if ( log.indexOf('[CRITICAL]') != -1 ) {
            return 'CRITICAL';
        }
        return 'DUMMY';
    }
</script>
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
		id=$(list.get(i)).attr("id").split("_",2);
		if(id[1]==$(e.target).attr("id").split("_",2)[1]){
		    $.post("API", { action:$(e.target).text(),  pluginID: id[1], param: $(list.get(i)).val() }, function (result) {
                var res=$.parseJSON(result);
                if(res.error==""){
                	$(e.target).html("stop");
                }
		    });
		}
	}
    
});

String.prototype.format = function() {
    var newStr = this, i = 0;
    while (/%s/.test(newStr)) {
        newStr = newStr.replace("%s", arguments[i++])
    }
    return newStr;
}

$(function(){
		var timer = $.timer(function() {
		getStatus();
		getLog();
        });
        timer.set({
            time: 500,
            autostart: true
        });
});

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
            			if(res[i].pluginID==$(op[j]).attr("id").split("_")[1]){
            				if(res[i].status==1){
            					$(op[j]).html("stop");
            					$("#st_"+res[i].pluginID).html("running");
            					$("#st_"+res[i].pluginID).attr("style","color:red;");
            				}else if(res[j].status==0){
            					$(op[j]).html("start");
            					$("#st_"+res[i].pluginID).html("stopped");
            					$("#st_"+res[i].pluginID).attr("style","color:black;");
            				}
            			}
            		}
            	}
            }});
}
</script>

