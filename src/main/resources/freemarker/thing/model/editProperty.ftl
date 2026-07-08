<html>
<head>
<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<form id="${primaryObj.name}_form" method="post" action="TModelController/updatePropertyDefinition">
		<input type="hidden" name="oid" value="${primaryObj.oid}" />
		<div class="easyui-layout" data-options="border:false" style="height:268px;font-family:helvetica,arial,sans-serif,Segoe UI,tahoma;">
			<div data-options="region:'north'" style="padding:2px 0px 0px 10px; height:35px;background-color:#f5f5f5">
				<label style="padding-right:10px;font-size:18px;font-weight:bold;">Edit Property</label>
				<select class="easyui-combobox" name="state" data-options="panelHeight:'auto'" style="width:100px;height:27px;padding-bottom:10px;">
					<option value="AL">Alabama</option>
					<option value="AK">Alaska</option>
				</select>
			</div>
			<div data-options="region:'center'" style="padding: 2px">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'west',collapsible:false,split:true" style="width:33%;border-radius:8px">
						<div class="easyui-panel" title="General Property Info" data-options="border:false,fit:true" style="width:100%;padding:10px 20px 20px 20px">
							<div>
								<input class="easyui-textbox" name="name" value="${primaryObj.name}" data-options="label:'Name:',required:true" style="width:100%;height:27px;" />
							</div>
							<div>
								<input class="easyui-textbox" name="description" value="${primaryObj.description}" data-options="label:'Description:',multiline:true" style="width:100%;height:100px">
							</div>
						</div>
					</div>
					<div data-options="region:'center'" style="border-radius: 8px">
						<div class="easyui-panel" title="Base Type Info" data-options="border:false,fit:true" style="width:100%;padding:10px 20px 20px 20px">
							<div><!-- 使用FreeMarker去处理当前PropertyDefinition的基本类型 -->
								<#assign BaseType=statics['com.flame.types.BaseType'] /> <!-- FreeMarker获取静态类FieldType -->
								<#assign values=BaseType.propertyTypes() /> <!-- 然后FreeMarker获取静态类FieldType所有枚举 -->
								<select class="easyui-combobox" id="baseType" name="baseType" style="width:200px;height:27px"
									data-options="label:'Base Type:',editable:false,panelHeight:'auto'">
									<#list values as ftype> <!-- 遍历FieldType的枚举所有枚举值 -->
									<#if ftype == primaryObj.baseType>
										<option value="${ftype}" selected="selected">${ftype}</option>
									<#else>
										<option value="${ftype}">${ftype}</option>
									</#if>
									</#list>
								</select>
							</div>
							<div>
								<input class="easyui-textbox" name="defaultValue" value="${primaryObj.defaultValue}" data-options="label:'Default Val:'" style="width:100%;height:27px;" />
							</div>
						</div>
					</div>
					<div data-options="region:'east',collapsible:false,split:true" style="width: 33%;border-radius:8px">
						<div class="easyui-panel" title="Data Change Info" data-options="border:false,fit:true" style="width:100%;padding:5px 20px 20px 20px">
							<fieldset>
								<legend style="font-size:14px;font-style:italic;">Aspects</legend>
								<#if primaryObj.persistent>
									<label>Persistent: <input type="checkbox" name="persistent" checked /></label>
								<#else>
									<label>Persistent: <input type="checkbox" name="persistent" /></label>
								</#if>
								<#if primaryObj.readOnly>
									<label>Read-only: <input type="checkbox" name="readOnly" checked /></label>
								<#else>
									<label>Read-only: <input type="checkbox" name="readOnly" /></label>
								</#if>
								<br>
								<#if primaryObj.logged>
									<label>Logged: <input type="checkbox" name="logged" checked /></label>
								<#else>
									<label>Logged: <input type="checkbox" name="logged" /></label>
								</#if>
								<#if primaryObj.nullable>
									<label>Nullable: <input type="checkbox" name="nullable" checked/></label>
								<#else>
									<label>Nullable: <input type="checkbox" name="nullable" /></label>
								</#if>
							</fieldset>
						</div>
					</div>
				</div>
			</div>
			<div data-options="region:'south'" style="height: 35px; padding-right:10px; background-color: #f5f5f5">
				<div style="text-align: right; padding: 5px 0">
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="cancelForm('${parameters.index}')" style="width: 80px">Cancel</a>
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="flame.submitForm(this, 'com.thing.processor.EditPropertyDefinitionProcessor', collapseProperty)" style="width: 80px">Done</a> 
				</div>
			</div>
		</div>
	</form>
	<script type="text/javascript">
		function collapseProperty(result) {
			var pList = $('#propertyList');
			if (pList) {
				if (result) {
					pList.datagrid('updateRow', { index:${parameters.index}, result });
				}
				pList.datagrid('getExpander', ${parameters.index}).trigger('click');
			}
		}
	</script>
</body>
</html>