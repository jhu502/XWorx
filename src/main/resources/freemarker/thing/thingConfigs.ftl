<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
	<div id="thg_configuration"></div>
	<script type="text/javascript">
		flame.xui.loadGrid("thg_configuration", "com.thing.builder.ThingConfigTableBuilder", {
			queryParams: {
				oid : '${(primaryObj.oid)!}'
			},
		});
	</script>
</body>
</html>