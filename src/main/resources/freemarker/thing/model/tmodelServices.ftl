<!DOCTYPE html>
<html lang="en">
<head>
	<meta content="text/html;charset=UTF-8" />
</head>
<body>
	<div id="editor-container" class="easyui-layout" data-options="fit:true">
		<div data-options="region:'center',title:'Service Editor',tools:'#editor-tools'">
			<div id="code-title" data-options="region:'north'" style="padding:0px 0 0 5px;height:20px;background-color:#f5f5f5;font-family:consolas">
				<b style='font-style:italic;'>Service:</b>
			</div>
			<div id="code-container" class="easyui-layout" data-options="fit:true">
				<div data-options="region:'center'">
					<input type="hidden" id="serviceOid" name="serviceOid" />
					<textarea class="form-control" id="code-editor" name="code" style="font-size: 13"></textarea>
				</div>
			</div>
		</div>
		<div data-options="region:'east',split:true" style="width: 300px;border:0px">
			<div id="service-layout" class="easyui-layout" data-options="fit:true">
				<div data-options="region:'center'">
					<table id="serviceDefTree" class="easyui-treegrid" style="font-size:12px"></table>
					<script type="text/javascript">
						flame.xui.loadGrid("serviceDefTree", "com.thing.builder.ServiceHierarchyBuilder", {
							queryParams: {
								oid : '${primaryObj.oid}'
							},
							onClickRow : function(node) {
								if (node.oid.indexOf('ServiceDefinition') > 0) {
									$.get('TModelController/getServiceDefinition?oid='+node.oid, function(data,status) {
										$('#serviceOid').attr("value", node.oid);
										if (!isBlank(data.code)) {
											let code = _D(data.code);
											editor.setValue(code);
											editor.setOption("readOnly", true);
										} else {
											editor.setValue("");
											editor.setOption("readOnly", true);
										}
										$('#code-title').panel({
											content : data.method
										});
										l_rowoid = node.oid;
									});
									$('#serviceInfo-panel').panel('refresh', 'freemarker/thing/model/serviceInfo?oid='+node.oid);
								}
							}
						});
					</script>
				</div>
				<div data-options="region:'south'" style="height:150px;">
					<div id="serviceInfo-panel" class="easyui-panel" title="Service Info" data-options="fit:true"></div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="editor-tools">
		<a href="javascript:void(0)" class="icon-edit" onclick="$(function(){
			editor.setOption('readOnly', false);
        });"></a> 
        <a href="javascript:void(0)" class="icon-save" onclick="$(function(){
        	var code = editor.getValue();

        	if (isBlank(code)) {
        		code = '';
        	}

    		$.post('TModelController/saveServiceDefCode', { oid:l_rowoid, code:_E(code) },
	    		function(data, status){
	    			alert(data.oid);
	    		}
    		);
        });"></a>
		<a href="javascript:void(0)" class="icon-indent" onclick="javascript:alert('cut')"></a>
        <a href="javascript:void(0)" class="icon-format" onclick="javascript:alert('cut')"></a>
        <a href="javascript:void(0)" class="icon-help" onclick="javascript:alert('help')"></a>
    </div>

	<script type="text/javascript">
		CodeMirror.velocityContext = 'server software env'; //提取到外部，方便从后台获取数据
		CodeMirror.velocityCustomizedKeywords = "self";
		CodeMirror.commands.autocomplete = function(cm) {
			cm.showHint({
				hint : CodeMirror.hint.anyword
			});
		}
		var editor = CodeMirror.fromTextArea(document.getElementById('code-editor'), {
			mode : { name : 'javascript', globalVars : true },
			extraKeys : { 'Ctrl-Q' : 'autocomplete'}, //提示代码快捷键
			lineNumbers : true,
			styleActiveLine : true,
			line : true,
			readOnly : true,
			tabSize : 4,
			foldgutter : true,
			lineWrapping : true, //代码折叠
			foldGutter : true,
			matchBrackets : true, //括号匹配
			autoCloseBrackets : true,
			height : window.innerHeight - 125
			//hintOptions : {
			//	hint : handleShowHint,
			//	completeSingle : false
			//}
		});

		/**
		 * -当窗口改变大小后，自动设置CodeMirror编辑界面的大小
		 */
		$(window).resize(function() {
			setEditorSize();
		});
		setEditorSize();

		function setEditorSize() {
			setTimeout(function() {
				var $this = $('#code');
				var parent = $this.parents('.tabs-panels');
				var width = parent.width();
				var height = window.innerHeight - 125;
				editor.setSize('auto', height);
			}, 200);
		}
	</script>
</body>
</html>