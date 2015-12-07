<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="java.io.File"%>
<%@page import="cn.edu.dufe.dufedata.controller.MainController"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
MainController controller=MainController.getInstance();
String readmeContent="";
if(request.getParameter("pluginID")!=null&&request.getParameter("pluginID").length()>0&&!request.getParameter("pluginID").equals("")){
	File readmeFile=controller.getOnePlugin(request.getParameter("pluginID")).getReadmeFile();
	if(readmeFile!=null){
	readmeContent=FileUtils.readFileToString(readmeFile,"utf-8").replaceAll(System.getProperty("line.separator"), "<br/>");
	}
}
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
	<script type="text/javascript" src="./js/jquery.placeholder.min.js"></script>

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
		
		<div class="col-md-12">
		<h2 class="sub-header">Introduction of <%=request.getParameter("pluginID")%></h2>
		<hr>
		<div id="log" style="overflow: auto;height: 100%">
		<p><%=readmeContent %></p>
   		</div>
    </div>
    </div></div>
  </body>
</html>
