<%@ page language="java" pageEncoding="utf-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="plm.dynamic.XExpression"%>
<%@ include file="/jsp/util/popup.jsp"%>
<%
XExpression express = (XExpression) commandBean.getPrimaryObj();
%>
<table style="table-layout: fixed; width: 500px; margin: 10px">
	<thead>
		<tr>
			<th style="width: 60px"></th>
			<th style="width: 220px"></th>
			<th style="width: 60px"></th>
			<th style="width: 220px"></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td style="font-size: 12; font-weight: bold;">参数编号:</td>
			<td style="font-size: 12; font-family: Consolas"><%=express.getNumber()%></td>
			<td style="font-size: 12; font-weight: bold;">参数名称:</td>
			<td style="font-size: 12; font-family: Consolas"><%=express.getName()%></td>
		</tr>
		<tr>
			<td style="font-size: 12; font-weight: bold;">描述:</td>
			<td style="font-size: 12; font-family: Consolas; colspan: 3"><%=express.getDescription()%></td>
		</tr>
		<tr>
			<td style="font-size: 12; font-weight: bold;">表达式:</td>
			<td colspan=3 style="font-size: 12; font-family: Consolas;"><%=express.getExpression()%></td>
		</tr>
	</tbody>
</table>
<%@ include file="/jsp/util/end.jsp"%>
