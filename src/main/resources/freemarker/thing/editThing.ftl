<html>
<head>
<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<form id="editThing_form" method="post" action="/TModelController/updateThing">
			<div data-options="region:'north'" style="height: 30px; padding: 2px 0px 0px 10px; background-color: #f5f5f5">
				<label style="padding-right: 10px; font-size: 18px; font-weight: bold;">Edit ${primaryObj.thingIdentity}</label> <input type="hidden" name="oid" value="${primaryObj.oid}" />
			</div>
			<div data-options="region:'center'" style="padding:2px;border:0px;">
				<div class="easyui-tabs" data-options="fit:true">
					<div title="Attributes" style="padding: 10px">
						<fieldset style="border-width:1px;border-color:#D4D4D4;">
							<legend>Attributes</legend>
							<table style="width:100%;">
								<tr>
									<td><input class="easyui-textbox" id="number" name="number" value="${primaryObj.number}" style="width:300px;height:24px" data-options="label:'Number:',required:true"></td>
								</tr>
								<tr>
									<td><input class="easyui-textbox" id="name" name="name" value="${primaryObj.name}" style="width:400px;height:24px" data-options="label:'Name:',required:true"></td>
								</tr>
								<tr>
									<td><input class="easyui-textbox" id="description" name="description" value="${primaryObj.description}" style="width:500px;height:150px" data-options="label:'Description:',multiline:true"></td>
								</tr>
							</table>
						</fieldset>
					</div>
					<div title="Configuration" style="padding: 10px">
						<fieldset style="border-width:1px;border-color:#D4D4D4;">
							<legend>Configuration</legend>
							<#assign configList=statics["com.flame.util.ThingUtils"].getThingConfiguration(primaryObj) />
							<table style="width:100%;">
							<#list configList as config>
								<#if config.created>
								<tr>
									<td>${config.display}</td>
									<#if (config.type) == "DateTime">
										<td><input class="easyui-datebox" id="${config.name}" name="${config.name}" value="${config.value!''}" style="width:150px;height:24px" data-options="required:${config.required?c}"></td>
									<#elseif (config.type) == "Boolean">
										<td><input class="easyui-checkbox" id="${config.name}" name="${config.name}" value="true" data-options="checked:${((config.value)!false)?c}" style="width:20px;height:20px;border-width:1px"></td>
									<#elseif (config.type) == "Password">
										<td><input class="easyui-passwordbox" id="${config.name}" name="${config.name}" value="${config.value!''}" style="width:200px;" data-options="required:${config.required?c}"></td>
									<#else>
										<td><input class="easyui-textbox" id="${config.name}" name="${config.name}" value="${config.value!''}" style="width:300px;height:24px" data-options="required:${config.required?c}"></td>
									</#if>
								</tr>
								</#if>
							</#list>
							</table>
						</fieldset>
					</div>
					<div title="Attachment" data-options="iconCls:'icon-help'" style="padding: 10px">This is the help content.</div>
				</div>
			</div>
			<div data-options="region:'south'" style="height: 35px; padding-right: 10px; background-color: #f5f5f5">
				<div style="text-align: right; padding: 5px 0">
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="flame.closeWindow(this)" style="width: 80px">Cancel</a>
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitThingForm('editThing_form', '0')" style="width: 80px">Done</a>
				</div>
			</div>
		</form>
	</div>
</body>
</html>