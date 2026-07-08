<html>
<head>
<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<div class="easyui-layout" style="height:440px;font-family:helvetica,arial,sans-serif,Segoe UI,tahoma;">
		<form id="winServiceDef_form" method="post" action="FUI$/form/com.thing.processor.CreateServiceDefProcessor">
			<input type="hidden" name="oid" value="${primaryObj.oid}" />
			<input type="hidden" id='arguments' name="arguments" value="" />
			<div data-options="region:'north'" style="padding:2px 0px 0px 10px; height:37px;background-color:#f5f5f5">
				<label style="padding-right:10px;font-size:18px;font-weight:bold;">New Service Definition</label>
				<#assign ServiceType=statics['com.flame.types.ServiceType'] /> <!-- FreeMarker获取静态类FieldType -->
				<#assign typeList=ServiceType.values() /> <!-- 然后FreeMarker获取静态类ServiceType所有枚举 -->
				<select class="easyui-combobox" id="serviceType" name="serviceType" style="width:200px;height:27px" data-options="editable:false,panelHeight:'auto'">
					<#list typeList as stype> <!-- 遍历ServiceType的枚举所有枚举值 -->
						<option value="${stype}">${stype}</option>
					</#list>
				</select>
			</div>
			<div data-options="region:'center'" style="padding:2px">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'west'" style="width: 300px; border-radius: 8px">
						<div class="easyui-panel" title="Service Info" data-options="fit:true" style="width: 100%; padding: 10px 10px 10px 10px">
							<div style="margin-bottom: 10px">
								<input class="easyui-textbox" name="name" value="" data-options="label:'Name:',required:true" style="width: 100%; height: 27px;" />
							</div>
							<div style="margin-bottom: 10px">
								<input class="easyui-textbox" name="description" value="" data-options="label:'Description:',multiline:true" style="width: 100%; height: 80px">
							</div>
						</div>
					</div>
					<div data-options="region:'center',collapsible:false,split:true" style="border-radius:8px">
						<div class="easyui-panel" title="Input / Output" data-options="fit:true" style="width:100%;padding:10px 10px 10px 10px">
							<table id="inputParams" class="easyui-datagrid" style="width:100%;height:200px" data-options="idField:'oid',singleSelect:true,toolbar:'#inputOutput-tb',onClickCell:onClickCell">
								<thead>
									<tr>
										<th data-options="field:'name',width:150,editor:{type:'textbox',options:{required:true}}">Parameter</th>
										<th data-options="field:'type',width:100,editor:{type:'combobox',options:{valueField:'type',textField:'display',method:'Get',panelHeight:'auto',url:'TModelController/listBaseType',required:true}}">Type</th>
										<th data-options="field:'description',width:220,editor:'textbox'">Description</th>
									</tr>
								</thead>
							</table>
							<div id="inputOutput-tb">
								<a class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="append()"></a>
								<a class="easyui-linkbutton" data-options="iconCls:'icon-delete',plain:true" onclick="removeit()"></a>
							</div>
							<br>
							<fieldset style="border:1px solid #D4D4D4;">
								<legend style="font-size:13px;font-weight:bold;">Output</legend>
								<table>
									<tr>
										<td style="width:250px">
											<!-- FreeMarker获取静态类FieldType -->
											<#assign BaseType=statics['com.flame.types.BaseType'] />
											<!-- 然后FreeMarker获取静态类FieldType所有枚举 -->
											<#assign values=BaseType.values() />
											<select class="easyui-combobox" id="resultType" name="resultType" style="width:200px;height:27px"
												data-options="label:'Result Type:',editable:false,panelHeight:'auto'">
												<!-- 遍历BaseType的枚举所有枚举值 -->
												<#list values as ftype>
													<option value="${ftype}">${ftype.display}</option>
												</#list>
											</select>
										</td>
										<td>
											<label class="textbox-label">Name: </label><span>result</span>
										</td>
									</tr>
								</table>
							</fieldset>
						</div>
					</div>
				</div>
			</div>
			<div data-options="region:'south'" style="height:35px;padding-right:11px;background-color:#f5f5f5">
				<div style="text-align:right;padding:4px 0">
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="flame.closeWindow(this)" style="width:80px">Cancel</a>
					<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitServiceForm('winServiceDef_form')" style="width:80px">Done</a>
				</div>
			</div>
		</form>
	</div>
	<script type="text/javascript">
		var editIndex = undefined;
		function endEditing() {
			if (editIndex == undefined) {
				return true
			}
			if ($('#inputParams').datagrid('validateRow', editIndex)) {
				$('#inputParams').datagrid('endEdit', editIndex);
				editIndex = undefined;
				return true;
			} else {
				return false;
			}
		}
		function append() {
			if (endEditing()) {
				$('#inputParams').datagrid('appendRow', {
					type : 'STRING'
				});
				editIndex = $('#inputParams').datagrid('getRows').length - 1;
				$('#inputParams').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
			}
		}
		function removeit() {
			if (editIndex == undefined) {
				return
			}
			$('#inputParams').datagrid('cancelEdit', editIndex).datagrid('deleteRow', editIndex);
			editIndex = undefined;
		}
		function onClickCell(index, field) {
			if (editIndex != index) {
				if (endEditing()) {
					$('#inputParams').datagrid('selectRow', index).datagrid('beginEdit', index);
					var ed = $('#inputParams').datagrid('getEditor', {
						index : index,
						field : field
					});
					if (ed) {
						($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).focus();
					}
					editIndex = index;
				} else {
					setTimeout(function() {
						$('#inputParams').datagrid('selectRow', editIndex);
					}, 0);
				}
			}
		}

		function submitServiceForm(formid) {
			endEditing(); // 编辑状态的行无法去获取数据，需要强制结束编辑状态；
			var rows = $('#inputParams').datagrid('getRows');
			var _array = [];
		    for(i = 0; i<rows.length; i++) {
		    	_array.push(rows[i]);
		    }
		    $('#arguments').val(JSON.stringify(_array));
			
			$('#'+formid).form('submit', {
				onSubmit : function() {
					return $(this).form('validate');
				},
				success : function(result) {
					if (result != '') {
						var json = jQuery.parseJSON(result);
						if (!isBlank(json.oid)) {
							$('#serviceDefTree').treegrid('reload');
							$('#winServiceDef').window('close');
						} else {
							$.messager.alert('Error', json.msg, 'error');
						}
					} else {
						$.messager.alert('Error', 'Update Failed!', 'error');
					}
				}
			});
		}
	</script>
</body>
</html>