<%@ page pageEncoding="utf-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="plm.dynamic.bean.XChoice"%>
<%@ page import="plm.dynamic.XCharacteristic"%>
<%@ include file="/jsp/util/popup.jsp"%>
<%
XCharacteristic character = (XCharacteristic) commandBean.getPrimaryObj();
%>
<div style="padding: 10px;">
	<table style="width: 100%">
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
				<td style="font-size:12px; font-weight: bold;">参数编号:</td>
				<td style="font-size:12px; font-family: Consolas; padding-left: 1px"><%=character.getNumber()%></td>
				<td style="font-size:12px; font-weight: bold;">参数名称:</td>
				<td style="font-size:12px; font-family: Consolas; padding-left: 1px"><%=character.getName()%></td>
			</tr>
			<tr>
				<td style="font-size:12px; font-weight: bold;">描述:</td>
				<td style="font-size:12px; font-family: Consolas; padding-left: 1px; cospan: 3"><%=character.getDescription()%></td>
			</tr>
			<tr>
				<td style="font-size:12px; font-weight: bold;">类型:</td>
				<td style="font-size:12px; font-family: Consolas; padding-left: 1px;"><%=character.getBaseType().getDisplay()%></td>
				<td style="font-size:12px; font-weight: bold;">输入方式:</td>
				<td style="font-size:12px; font-family: Consolas; padding-left: 1px"><%=character.getInputType().getDisplay()%></td>
			</tr>
			<tr>
				<td style="font-size:12px; font-weight: bold;">选项类型:</td>
				<td style="font-size:12px; font-family: Consolas; padding-left: 1px"><%=character.getOptionMode().getDisplay()%></td>
			</tr>
			<tr>
				<td style="font-size:12px; font-weight: bold;">常量值:</td>
				<td style="font-size:12px; font-family: Consolas"></td>
			</tr>
			<tr>
				<td style="font-size:12px; font-weight: bold;">表达式:</td>
				<td style="font-size:12px; font-family: Consolas"></td>
			</tr>
			<tr>
				<td style="font-size:12px; font-family: Consolas" colspan=4>-------------------------------------------------------------------------------------</td>
			</tr>
			<tr>
				<td style="font-size:12px; font-weight: bold;">列表</td>
			</tr>
		</tbody>
	</table>
	<table style="width: 100%;border: 1px solid #ccc;border-collapse: collapse;" cellspacing="0" cellpadding="0">
		<tr>
			<th style="text-align: center;border: 1px solid #ccc;">值</th>
			<th style="text-align: center;border: 1px solid #ccc;">描述</th>
			<th style="width:20px;border: 1px solid #ccc;"></th>
		</tr>
		<%
		for (XChoice choice : character.getChoices()) {
		%>
		<tr>
			<td style="border: 1px solid #ccc; padding-left: 2px"><%=choice.getValue()%></td>
			<td style="border: 1px solid #ccc; padding-left: 2px"><%=choice.getDescription()%></td>
			<td style="border: 1px solid #ccc;"></td>
		</tr>
		<%
		}
		%>
	</table>
</div>
<%@ include file="/jsp/util/end.jsp"%>
