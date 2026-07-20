<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="com.flame.xui.XCommandBean"%>
<%@ page import="com.flame.xui.HREFactory" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>XWorx</title>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <base id="basehref" href="<%=HREFactory.getBaseHREF()%>">

    <link href="css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="css/flame/themes-xui.css" rel="stylesheet" type="text/css"/>
    <link href="css/flame/themes-icon.css" rel="stylesheet" type="text/css"/>
    <link href="css/flame/easyui/easyui.css" rel="stylesheet" type="text/css"/>
    <link href="css/flame/easyui/tabs.css" rel="stylesheet" type="text/css"/>

    <script src="javascript/easyui/jquery.min.js" type="text/javascript"></script>
    <script src="javascript/easyui/jquery.easyui.min.js" type="text/javascript"></script>
    <script src="javascript/easyui/datagrid-detailview.js" type="text/javascript"></script>
    <script src="javascript/flame/bootstrap.min.js" type="text/javascript"></script>
    <script src="javascript/flame/common.xui.js" type="module"></script>
    <script src="javascript/flame/flame.xui.js" type="text/javascript"></script>
    <script src="javascript/flame/flame.thing.js" type="text/javascript"></script>
</head>
<body style="overflow: hidden">
<%
	XCommandBean commandBean = XCommandBean.newCommandBean(request, response);
%>
