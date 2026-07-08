<!DOCTYPE HTML>
<html>

<head>
<title>Guacamole (EXAMPLE)</title>

<script type="text/javascript" src="/js/terminal/all.min.js"></script>
</head>

<body>
	<!-- Display -->
	<div id="display"></div>

	<!-- Init -->
	<script type="text/javascript">
		var display = document.getElementById("display");

		var socket = new Guacamole.WebSocketTunnel("/Terminal");
		var guac = new Guacamole.Client(socket); // websocket连接时开启
		
		// Add client to display div
		display.appendChild(guac.getDisplay().getElement());
		
		// Error handler
		guac.onerror = function(error) {
			alert(error);
		};

		// Connect
		guac.connect();
		
		// Disconnect on close
		window.onunload = function() {
			guac.disconnect();
		}

		// Mouse
		var mouse = new Guacamole.Mouse(guac.getDisplay().getElement());

		mouse.onmousedown = mouse.onmouseup = mouse.onmousemove = function(mouseState) {
			guac.sendMouseState(mouseState);
		};

		// Keyboard
		var keyboard = new Guacamole.Keyboard(document);

		keyboard.onkeydown = function(keysym) {
			guac.sendKeyEvent(1, keysym);
		};

		keyboard.onkeyup = function(keysym) {
			guac.sendKeyEvent(0, keysym);
		};
	</script>
</body>
</html>