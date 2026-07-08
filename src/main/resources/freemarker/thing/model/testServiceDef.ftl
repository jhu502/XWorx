<html>
<head>
<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<div class="easyui-layout" data-options="fit:true" style="font-family:helvetica, arial, sans-serif, Segoe UI, tahoma;">
		<form id="winServiceDef_form" method="post" action="TModelController/testServiceDefinition">
		<input type="hidden" name="oid" value="${primaryObj.oid}" /> <input type="hidden" id='params' name="params" value="" />
		<div data-options="region:'north'" style="padding: 2px 0px 0px 10px; height: 33px; background-color: #f5f5f5">
			<label style="padding-right: 10px; font-size: 18px; font-weight: bold;">Test Service Definition</label>
			<input class="easyui-textbox" id="thingIdent" name="thingIdent" value="FTUser:Administrator" data-options="required:true" style="width:200px;height:27px;" />
		</div>
		<div data-options="region:'center'" style="padding: 2px">
			<div class="easyui-layout" data-options="fit:true">
				<div data-options="region:'west',collapsible:false,split:true" style="width: 250px; border-radius: 8px">
					<div class="easyui-panel" title="Input" data-options="fit:true" style="width: 100%; padding: 10px 15px 10px 10px">
						<#list thing.inputParameter as paramDef>
						<div style="margin-bottom: 10px">
							<label for="${paramDef.name}">[${paramDef.baseType.display}] ${paramDef.name}:</label>
							<input class="easyui-textbox" name="param_${paramDef.name}" value="" style="width: 100%; height: 25px;" />
						</div>
						</#list>
						<div style="text-align: right; padding: 5px 0">
							<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:testServiceForm('winServiceDef_form')" style="width: 40px; align: right">Test</a>
						</div>
					</div>
				</div>
				<div data-options="region:'center'" style="border-radius: 8px">
					<div class="easyui-panel" title="Output" data-options="fit:true" style="width: 100%; padding: 5px 20px 5px 5px">
						<div id="resultDiv"></div>
					</div>
				</div>
			</div>
		</div>
		<div data-options="region:'south'" style="height: 35px; padding-right: 10px; background-color: #f5f5f5">
			<div style="text-align: right; padding: 5px 0">
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="flame.closeWindow(this)" style="width: 80px">Close</a>
			</div>
		</div>
		</form>
	</div>
	<script type="text/javascript">
		function testServiceForm(formid) {
			var params = $("input[name^='param_']"); //查询以“param_”开头的input
		    var pmap = {};
		    for(var i = 0;i<params.length;i++) {
		    	var n = params[i].name;
		    	var v = params[i].value;
		    	var p = n.substr(6);
		    	
		    	pmap[p] = v;
		    }
		    var value = JSON.stringify(pmap);
		    
		    $('#params').val(value);
			
			$.post($("#winServiceDef_form").attr("action"), 
				{ 
					'oid': '${primaryObj.oid}',
					'thingIdent': $('#thingIdent').val(),
					'serviceName': '${primaryObj.name}',
					'params': value 
				},
				function(data, status) {
					if (!isBlank(data)) {
						$('#resultDiv').html(JSON.stringify(data));
					} else {
						$('#resultDiv')[0].innerHTML = 'No Return Value';
					}
				}
			);
		}
	</script>
</body>
</html>