<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
	<div id="thg_services"></div>
	<script type="text/javascript">
		flame.xui.loadGrid("thg_services", "com.thing.builder.ThingServiceTableBuilder", {
			queryParams: {
				oid : '${(primaryObj.oid)!}'
			},
		});
	</script>
</body>
</html>