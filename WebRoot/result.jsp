<%@page import="java.io.File"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="cn.edu.dufe.dufedata.plugin.Plugin"%>
<%@page import="cn.edu.dufe.dufedata.controller.MainController"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String fileString="";
if(!request.getParameter("file").isEmpty()&&request.getParameter("file")!=null){
fileString=FileUtils.readFileToString(new File("WebRoot/data"+request.getParameter("file")),"utf-8");
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
	
    <script src="js/jquery-1.11.3.js" type="text/javascript"></script>
    <script src="js/jsonFormater.js" type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" href="./css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="./css/dashboard.css">
	<link rel="stylesheet" type="text/css" href="./css/style.css">
	<script type="text/javascript" src="./js/bootstrap.js"></script>
    <link href="css/jsonFormater.css" type="text/css" rel="stylesheet"/>
    <link href="css/demo.css" type="text/css" rel="stylesheet"/>
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
		<div class="col-md-12 main">
		<h1 class="page-header">ResultReviewer</h1>
<div class="HeadersRow">
    <textarea id="RawJson" class="form-control"><%=fileString %></textarea>
</div>
<br />
<div id="ControlsRow">
<div class="row">
<div class="col-md-3">
    <label>indentation: &nbsp</label>
    <select id="TabSize" >
        <option value="1">1</option>
        <option value="2" selected>2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
        <option value="6">6</option>
    </select>
    &nbsp
    <input type="button" value="Format" id='format' class="btn btn-primary" />
    <input type="button" value="toCSV" id="toCSV" class="btn btn-primary" />
    <a id="download" href="" hidden="true">download</a>
</div>
</div>
<div id="Canvas" class="Canvas"></div>
<script type="application/javascript">
$("#toCSV").click(function(){
		    $.post("API", { action:"toCSV",  values: "<%=request.getParameter("file")%>" }, function (result) {
		    alert("<%=request.getParameter("file")%>");
               var res=$.parseJSON(result);
               if(res.error==""&&res.url!=""){
               		$("#download").attr("href",res.url);
               		$("#download").attr("hidden",false);
               }
	    });
});
</script>
<script type="application/javascript">
    $(document).ready(function () {
    var fileString=<%=fileString %>;
    window.jf = new JsonFormater({
                dom: '#Canvas',
                isCollapsible: $('#CollapsibleView').prop('checked'),
                quoteKeys: $('#QuoteKeys').prop('checked'),
                tabSize: 2
            });
    jf.doFormat(fileString);
        var format = function () {
            var options = {
                dom: '#Canvas',
                isCollapsible: $('#CollapsibleView').prop('checked'),
                quoteKeys: $('#QuoteKeys').prop('checked'),
                tabSize: $('#TabSize').val()
            };
            window.jf = new JsonFormater(options);
            jf.doFormat($('#RawJson').val());
        };
        $('#format').click(function () {
            format();
        });
        $('#expandAll').click(function () {
            window.jf.expandAll();
        });
        $('#collapseAll').click(function () {
            window.jf.collapseAll();
        });
        $('#TabSize, #CollapsibleView, #QuoteKeys').change(function () {
            format();
        });
        $('.expand').click(function () {
            var level = $(this).data('level');
            window.jf.collapseLevel(level);
        });
    });
    
    function windowOpen(url,target){
    var a = document.createElement("a");
    a.setAttribute("href", url);
    if(target == null){
        target = '';
    }
    a.setAttribute("target", target);
    document.body.appendChild(a);
    if(a.click){
        a.click();
    }else{
        try{
            var evt = document.createEvent('Event');
            a.initEvent('click', true, true);
            a.dispatchEvent(evt);
        }catch(e){
            window.open(url);
        }
    }
    document.body.removeChild(a);
}
</script>
</div>
</div>
</div>

</body>
</html>
