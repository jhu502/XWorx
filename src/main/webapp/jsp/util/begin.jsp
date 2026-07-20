<%@ page language="java" pageEncoding="utf-8" %>
<%@ page import="com.flame.xui.XCommandBean" %>
<%@ page import="com.flame.xui.HREFactory" %>
<%@ page import="com.flame.auths.SessionHelper" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>XWorx</title>
	<meta charset="utf-8"/>
	<link rel="icon" href="images/favicon.ico" type="image/x-icon"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
	<base id="basehref" href="<%=HREFactory.getBaseHREF()%>">

	<script src="javascript/flame/common.xui.js" type="module"></script>
	<script src="javascript/flame/flame.xui.js" type="module"></script>
	<script src="javascript/easyui/jquery.min.js" type="text/javascript"></script>
	<script src="javascript/axios/axios.min.js" type="text/javascript"></script>
	<script src="javascript/vue/vue.global.prod.js" type="text/javascript"></script>
	<script src="javascript/easyui/jquery.easyui.min.js" type="text/javascript"></script>
	<script src="javascript/flame/bootstrap.min.js" type="text/javascript"></script>
	<script src="javascript/flame/flame.thing.js" type="text/javascript"></script>

	<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
	<link href="css/flame/themes-xui.css" rel="stylesheet" type="text/css"/>
	<link href="css/flame/themes-icon.css" rel="stylesheet" type="text/css"/>
	<link href="css/flame/easyui/tabs.css" rel="stylesheet" type="text/css"/>
	<link href="css/flame/easyui/easyui.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript">
		$.parser.auto = false;
	</script>
	<style>
		.x-app-header {
			height: 50px;
			background: linear-gradient(90deg, #03a9f4, #03a9f4, #adfaea);
		}
	</style>
</head>
<body id="xui-app-window">
	<%XCommandBean commandBean = XCommandBean.newCommandBean(request, response);%>
	<header id="x-app-header" class="x-app-header" @click="hiddenComposer($event)">
		<span><a id="xui-app-menu-toggle" @click="loadMainContent('XWX-Navigator:workHomeTab', 'thymeleaf/home/homeTab.html', $event)"></a></span>
		<div class="media-body">
			<div style="float:left;width:200px;height:50px;">
				<a href="javascript:void(0)" onclick="xui.shell.changeHashURI('netmarkets/home/welcome.html');"><img style="height:48px" src="images/home/logo.png"></a>
			</div>
			<div class="xui-module" style="position:relative;float:left;margin-left:200px;margin-right:200px;height:50px;">
				<button><img src="images/body/plm.png"/></button>
				<button><img src="images/body/crm.png"/></button>
				<button><img src="images/body/iot.png"/></button>
				<button onclick="loadSideXWindow()"><img src="images/body/project.png"/></button>
				<script type="text/javascript">
					function loadSideXWindow() {
						xui.util.newWindow({ 
							parent: '#xui-app-content', 
							url: 'http://flame.xworx.cn/XWorx/thymeleaf/part/partDetailsTab.html?oid=OR:plm.part.XPart:284',//'http://flame.xworx.cn/XWorx/XUI$/info/InfoPage?oid=OR:plm.part.XPart:284', 
							options: "park:'right',draggable:false,resize:'left'", 
							style: "top:0px;width:1024px;height:100%;" 
						});
					}
				</script>
			</div>
			<div style="float:right;width:200px;height:50px;position:relative;display:flex;align-items:center;justify-content:flex-end;">
				<button id="claw-chat-btn" onclick="openClawChatWindow()" title="Claw AI 助手" style="background:#07C160;border:none;cursor:pointer;padding:4px 6px;border-radius:4px;margin-right:10px;display:flex;align-items:center;">
					<svg width="20" height="20" viewBox="0 0 24 24" fill="#fff">
						<path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
						<path d="M7 9h10v2H7zm0-3h10v2H7z"/>
					</svg>
				</button>
				<span style="margin-right:12px;"><%=SessionHelper.getCurrentUser().getName()%></span>
			</div>
		</div>
	</header>
	<script type="text/javascript">
		function openClawChatWindow() {
			xui.util.newWindow({ 
				parent: '#xui-app-content', 
				url: 'thymeleaf/ai/clawChat.html', 
				options: "park:'center',draggable:true,resize:true,minimize:true,maximize:true,closable:true", 
				style: "width:420px;height:580px;" 
			});
		}
	</script>