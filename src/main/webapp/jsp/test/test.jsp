<%@ page language="java" pageEncoding="utf-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/jsp/util/begin.jsp"%>
<div style="padding: 5px; height: 100%">
	<table id="testBrowserTable"></table>
	<script type="text/javascript">
		flame.xui.loadGrid("testBrowserTable", "com.flame.test.TestBrowserTableBuilder", {});
	</script>
</div>
<%@ include file="/jsp/util/end.jsp"%>