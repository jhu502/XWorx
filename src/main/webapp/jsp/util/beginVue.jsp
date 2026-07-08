<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="com.flame.xui.XCommandBean"%>
<%@ page import="com.flame.xui.HREFactory" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>XWorx</title>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<base id="basehref" href="<%=HREFactory.getBaseHREF()%>">

<script src="javascript/axios/axios.min.js" type="text/javascript"></script>
<script src="javascript/vue/vue.global.js" type="text/javascript"></script>

</head>
<body style="overflow: hidden">
<%
	XCommandBean commandBean = XCommandBean.newCommandBean(request, response);
%>
