<html>
<head>
<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<div class="easyui-layout" data-options="fit:true" style="font-family: helvetica, arial, sans-serif, Segoe UI, tahoma;">
		<form id="invokeService_form" method="post" action="Thing$/serviceTest">
		<input type="hidden" name="oid" value="${primaryObj.oid}" />
		<input type="hidden" name="serviceName" value="${parameters.name}" />
		<input type="hidden" id='params' name="params" value="" />
		<div data-options="region:'north'" style="padding: 2px 0px 0px 10px; height: 36px; background-color: #f5f5f5">
			<label style="padding-right: 10px; font-size: 18px; font-weight: bold;">Invoke [${parameters.name}] Service</label>
			<input class="easyui-textbox" name="thingIdent" value="${primaryObj.thingIdentity}" data-options="required:true,readonly:true" style="width:200px;height:27px;" />
		</div>
		<div data-options="region:'center'" style="padding:2px">
			<div class="easyui-layout" data-options="fit:true">
				<div data-options="region:'center'" style="border-radius: 8px">
					<div class="easyui-panel" title="Input" data-options="border:false,fit:true" style="width: 100%; padding: 10px 15px 10px 10px">
						<#assign serviceInfo=statics['com.flame.util.ThingUtils'].getThingServiceInfo(primaryObj, parameters.name) />
						<#list serviceInfo.params as p>
						<div style="margin-bottom: 10px">
							<label for="${p.name}">[${p.type.display}] ${p.name}:</label>
							<input class="easyui-textbox" name="param_${p.name}" value="" style="width: 100%; height: 27px;" />
						</div>
						</#list>
						<div style="text-align: right; padding: 5px 0">
							<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:invokeServiceForm('invokeService_form')" style="width:40px;align:right">Test</a>
						</div>
					</div>
				</div>
				<div data-options="region:'east',collapsible:false,split:true" style="width:700px;border-radius:8px">
					<div class="easyui-panel" title="Output" data-options="border:false,fit:true" style="width: 100%; padding: 5px 20px 5px 5px">
						<pre id="resultDiv"></pre>
					</div>
				</div>
			</div>
		</div>
		<div data-options="region:'south'" style="height: 36px; padding-right: 10px; background-color: #f5f5f5">
			<div style="text-align: right; padding: 5px 0">
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="flame.closeWindow(this)" style="width: 80px">Close</a>
			</div>
		</div>
		</form>
	</div>
	<script type="text/javascript">
		function invokeServiceForm(formid) {
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
		    
			$.post($("#invokeService_form").attr("action"), { 
					oid: '${primaryObj.oid}',
					thingIdent: '${primaryObj.thingIdentity}',
					serviceName: '${parameters.name}',
					params: value 
				},
				function(data, status) {
					if (!isBlank(data)) {
						$('#resultDiv').html(JSON.stringify(data,null,4));
					} else {
						$('#resultDiv')[0].innerHTML = 'No Return Value';
					}
				}
			);
		}
	</script>
</body>
</html>