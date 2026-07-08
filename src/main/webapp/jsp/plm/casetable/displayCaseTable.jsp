<%@ page language="java" pageEncoding="utf-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="plm.dynamic.XCaseTable" %>
<%@ page import="plm.dynamic.bean.XCaseRows" %>
<%@ page import="java.util.List" %>
<%@ include file="/jsp/util/popup.jsp" %>
<%
    XCaseTable casetable = (XCaseTable) commandBean.getPrimaryObj();
%>
<div style="padding: 10px;">
    <table style="table-layout: fixed; width: 500px;">
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
            <td style="font-size: 12px; font-weight: bold;">参数编号:</td>
            <td style="font-size: 12px; font-family: Consolas"><%=casetable.getNumber()%>
            </td>
            <td style="font-size: 12px; font-weight: bold;">参数名称:</td>
            <td style="font-size: 12px; font-family: Consolas"><%=casetable.getName()%>
            </td>
        </tr>
        <tr>
            <td style="font-size: 12px; font-weight: bold;">描述:</td>
            <td style="font-size: 12px; font-family: Consolas; colspan: 3"><%=casetable.getDescription()%>
            </td>
        </tr>
        <tr>
            <td style="font-size: 12px; font-weight: bold;">类型:</td>
            <td colspan=3 style="font-size: 12px; font-family: Consolas;"><%=casetable.getType().getDisplay()%>
            </td>
        </tr>
        <tr>
            <td style="font-size: 12px; font-family: Consolas" colspan=4>
                -------------------------------------------------------------------------------------------------------------------
            </td>
        </tr>
        <tr>
            <td style="font-size:12px; font-weight: bold;">列表</td>
        </tr>
        </tbody>
    </table>
    <table class="caseTable" width="100%" border="1" cellspacing="0" cellpadding="0">
        <tr>
            <%
                for (String column : casetable.getHead().getData()) {
                    out.print("<th style=\"text-align:center;\">" + column + "</th>");
                }
            %>
        </tr>
        <%
            XCaseRows caseRows = casetable.getRows();
            for (List<Object> row : caseRows.getData()) {
                out.print("<tr>");
                for (Object cell : row) {
                    out.print("<td>" + cell + "</td>");
                }
                out.print("</tr>");
            }
        %>
    </table>
</div>
<%@ include file="/jsp/util/end.jsp" %>