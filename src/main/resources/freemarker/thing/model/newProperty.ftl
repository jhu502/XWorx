<html>
<head>
<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<form id="newProperty_form" method="post" action="TModelController/createPropertyDefinition">
		<input type="hidden" name="oid" value="${primaryObj.oid}" />
		<div class="easyui-layout" style="height:288px;font-family:helvetica,arial,sans-serif,Segoe UI,tahoma;">
			<div data-options="region:'north'" style="padding:2px 0px 0px 10px; height:36px;background-color:#f5f5f5">
				<label style="padding-right:10px;font-size:18px;font-weight:bold;">New Property</label>
				<select class="easyui-combobox" name="state" data-options="panelHeight:'auto'" style="width:100px;height:27px;padding-bottom:10px;">
					<option value="AL">Alabama</option>
					<option value="AK">Alaska</option>
				</select>
			</div>
			<div data-options="region:'center'" style="padding:2px">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'west',collapsible:false,split:true" style="width:33%;border-radius:8px">
						<div class="easyui-panel" title="General Property Info" data-options="fit:true" style="width:100%;padding:10px 10px 20px 20px">
							<div style="margin-bottom:10px">
								<input class="easyui-textbox" name="name" data-options="label:'Name:',required:true" style="width:94%;height:27px;" />
							</div>
							<div style="margin-bottom:10px">
								<input class="easyui-textbox" name="description" data-options="label:'Description:',multiline:true" style="width:94%;height:80px">
							</div>
						</div>
					</div>
					<div data-options="region:'center'" style="border-radius:8px">
						<div class="easyui-panel" title="Base Type Info" data-options="fit:true" style="width:100%;padding:10px 20px 20px 20px">
							<!-- 使用FreeMarker去处理当前PropertyDefinition的基本类型 -->
							<div style="margin-bottom:20px">
								<#assign BaseType=statics['com.flame.types.BaseType'] /> <!-- FreeMarker获取静态类FieldType -->
								<#assign values=BaseType.propertyTypes() /> <!-- 然后FreeMarker获取静态类FieldType所有枚举 -->
								<select class="easyui-combobox" id="baseType" name="baseType" style="width:200px;height:27px"
									data-options="label:'Base Type:',editable:false,panelHeight:'auto'">
									<#list values as ftype> <!-- 遍历FieldType的枚举所有枚举值 -->
										<option value="${ftype}">${ftype}</option>
									</#list>
								</select>
							</div>
							<div style="margin-bottom:10px">
								<input class="easyui-textbox" name="defaultValue" value="" data-options="label:'Default Val:'" style="width:100%;height:27px;" />
							</div>
						</div>
					</div>
					<div data-options="region:'east',collapsible:false,split:true" style="width:33%;border-radius:8px">
						<div class="easyui-panel" title="Data Change Info" data-options="fit:true" style="width:100%;padding:5px 20px 20px 20px">
							<fieldset>
								<legend style="font-size:16px;font-style:italic;">Aspects</legend>
								<label>Persistent: <input type="checkbox" name="persistent" /></label>
								<label>Read-only: <input type="checkbox" name="readOnly" /></label>
								<label>Logged: <input type="checkbox" name="logged" /></label>
								<label>Nullable: <input type="checkbox" name="nullable" /></label>
							</fieldset>
						</div>
					</div>
				</div>
			</div>
			<div data-options="region:'south'" style="height:36px; padding-right:10px; background-color:#f5f5f5">
				<div style="text-align:right; padding:5px 0">
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="flame.closeWindow(this)" style="width:80px">Cancel</a>
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitPtyForm('newProperty_form', '-1')" style="width:80px">Done</a> 
				</div>
			</div>
		</div>
	</form>
</body>
</html>