<!DOCTYPE html>
<html>
<head>
	<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<div class="easyui-layout" data-options="fit:true" style="font-family:helvetica,arial,sans-serif,Segoe UI,tahoma;">
		<div data-options="region:'center',border:false" style="padding:2px;">
			<div class="easyui-layout" data-options="fit:true">
				<div data-options="region:'north',split:true,border:false" style="height:70%">
					<table id="propertyList" class="easyui-datagrid"></table>
					<script type="text/javascript">
						flame.xui.loadGrid("propertyList", "com.thing.builder.ThingModelPropertyBuilder", {
							view : detailview,
							queryParams: {
								oid : '${primaryObj.oid}'
							},
							detailFormatter : function(index, row) {
								return '<div class="ddv" style="padding:0px 0; border-radius:8px"></div>';
							}, 
							onExpandRow : function(index, row) { //点击行的修改按钮时调用这个方法去加载数据和界面
								var ddv = $(this).datagrid('getRowDetail', index).find('div.ddv');
								ddv.panel({
									height : 270,
									border : true,
									cache : false,
									href : 'freemarker/thing/model/editProperty?oid=' + row.oid + '&index=' + index,
									onLoad : function() {
										$('#propertyList').datagrid('fixDetailRowHeight', index);
									}
								});
								$('#propertyList').datagrid('fixDetailRowHeight', index);
							},
							onCollapseRow : function(index, row) {
								return true;
							}
						});
					</script>
				</div>
				<div data-options="region:'center',border:false" style="height:30%">
					<table id="inheritedList" class="easyui-datagrid"></table>
					<script type="text/javascript">
						flame.xui.loadGrid("inheritedList", "com.thing.builder.ThingModelInheritedBuilder", {
							queryParams: {
								oid : '${primaryObj.oid}'
							}
						});
					</script>
				</div>
			</div>
		</div>
	</div>

	<div id="propertyList-tb">
		<a class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true"
			onclick="flame.popupWindow('New Property', 'freemarker/thing/model/newProperty', 'width:1000px; height:338px; padding:5px;', { oid:$('#selectedOid')[0].value });">New</a>
		<a class="easyui-linkbutton" data-options="iconCls:'icon-delete',plain:true"
			onclick="flame.popupWindow('Edit Property', 'thymeleaf/thing/model/editProperty.html', 'width:1000px; height:338px; padding:5px;', { oid:$('#selectedOid')[0].value });">Delete</a>
	</div>

	<script type="text/javascript">
		function submitPtyForm(formid, index) {
			$('#'+formid).form('submit', {
				onSubmit : function() {
					return $(this).form('validate');
				},
				success : function(result) {
					if (result != '') {
						var json = jQuery.parseJSON(result);
						if (!isBlank(json.oid)) {
							var pList = $('#propertyList');
							if (pList) {
								if (index == -1) { //Property创建界面
									pList.datagrid('reload');
									$('#xx_flame_win_yy').window('close');
								} else { //Property编辑界面
									pList.datagrid('updateRow', { index:index, row:json	});
									pList.datagrid('getExpander', index).trigger('click');
								}
							}
						} else {
							$.messager.alert('Error', json.msg, 'error');
						}
					} else {
						$.messager.alert('Error', 'Update Failed!', 'error');
					}
				}
			});
		};

		function cancelForm(index) {
			var expander = $('#propertyList').datagrid('getExpander', index);
			expander.trigger('click');
		};

		$(window).resize(function() {
		//	resize_grid('#propertyList', 77);
		});
	</script>
</body>
</html>