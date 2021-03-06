<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="cn.edu.dufe.dufedata.util.TimeUtil"%>
<%@page import="cn.edu.dufe.dufedata.util.MyFileUtils"%>
<%@page import="cn.edu.dufe.dufedata.controller.MainController"%>
<%@page import="java.io.File"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
MainController controller=MainController.getInstance();
File[] files=controller.listLogFiles();
MyFileUtils.sortByDate(files);
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
			<div class="col-sm-3 col-md-2 sidebar"
				style="background-color: black;">
				<ul class="nav nav-sidebar">
					<li><a href="index.jsp">Plugin</a></li>
					<li><a href="files.jsp">Result</a></li>
					<li class="active"><a href="log.jsp">Log</a></li>
				</ul>
			</div>
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">Dashboard</h1>
				<hr/>
				<h2 class="sub-header">Logs</h2>
				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<th>FileName</th>
								<th>LastModify</th>
								<th>Size</th>
								<th>Operations</th>
							</tr>
						</thead>
						<tbody>
						<%
						for(File file:files){
						 %>
						<tr>
						<td><%=file.getName() %></td>
						<td><%=TimeUtil.convertLongToDateString(String.valueOf(file.lastModified()/1000)) %></td>
						<td><%=MyFileUtils.calculateSize(FileUtils.sizeOf(file)) %></td>
						<td><a href="log/<%=file.getName() %>">Download</a> | 
						<a  href="reviewlog.jsp?file=<%=file.getName() %>" target="_blank">Review</a> | 
						<input type="button" class="delete btn btn-danger" id="<%=file.getName() %>" value="delete"></td>
						</tr>
						<%
						}
						 %>
						</tbody>
						</table>
				</div>
		</div>
</body>
</html>
<script type="text/javascript">
	$(".delete").click(function(e){
	
	var list=$(".delete");
	for(i=0;i<list.size();i++){
		name=$(list.get(i)).attr("id");
		if($(e.target).attr("id")==name){
		if(confirm("delete "+name+"?")!=true){
			return;
		}
	    $.post("API", { action:"delete",  fileName: name }, function (result) {
               var res=$.parseJSON(result);
               if(res.error==""){
               	 location.reload();
               }else{
               	 console.log(res);
               }
	    });
	    }
	}
    
});
</script>
