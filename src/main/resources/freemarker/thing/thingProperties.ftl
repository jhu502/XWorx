<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
	<div id="thg_properties"></div>
	<script type="text/javascript">
		flame.xui.loadGrid("thg_properties", "com.thing.builder.ThingPropertyTableBuilder", {
			queryParams: {
				oid : '${(primaryObj.oid)!}'
			},
		});
	</script>
</body>
</html>