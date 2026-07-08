<!DOCTYPE html>
<html lang="en">
<head>
<link href="js/easyui/themes/bootstrap/easyui.css" rel="stylesheet" />
<link href="js/easyui/themes/bootstrap/tabs.css" rel="stylesheet" />
<link href="js/easyui/themes/icon.css" rel="stylesheet" />

<script src="js/easyui/jquery.min.js" type="text/javascript"></script>
<script src="js/easyui/jquery.easyui.min.js" type="text/javascript"></script>
<script src="js/flame/flame.thing.js" type="text/javascript"></script>

<script type="text/javascript">
	function SSHTerminal() {
		this.result_show = null;
	}

	SSHTerminal.prototype = {
		resultCommand : function(message) {
			if (this.result_show == null) {
				this.result_show = $("#result_show");
			}
			this.result_show.val(this.result_show.val() + "\n" + message);
			this.result_show.scrollTop(this.result_show[0].scrollHeight);
			
			return;
		}
	};

	$(function() {
		var thingName = 'FTTerminal:${primaryObj.number}';

		var client = new FlameThingClient();
		client.connectServer("ws://localhost:2110/Websocket");
		client.onOpen = function() {
			if (WebSocket.OPEN == client.socket.readyState) {
				this.binding(thingName, new SSHTerminal());
			}
		}

		$("#command").bind("keypress", function() {
			if (event.keyCode == "13") {
				var message = $("#command").val();

				client.invoke(thingName, 'sendCommand', {
					'command' : BaseType.encode('STRING', message)
				})
				$("#command").val("");
			}
		});
		$("#result_show").val("");
	});
</script>

</head>
<body>
	<div class="easyui-tabs" data-options="fit:true">
		<div title="Shell" style="padding: 10px">
			<textarea id="result_show" readonly rows="40" style="width:100%;word-break:break-all;">
			</textarea>
			<p>
			请输入命令(按回车结束): 
			<input type="text" id="command" style="width: 100%; height: 40px; overflow: auto;" />
		</div>
		<div title="FTP" style="padding: 10px"></div>
		<div title="Help" data-options="iconCls:'icon-help',closable:true" style="padding: 10px">This is the help content.</div>
	</div>
</body>
</html>