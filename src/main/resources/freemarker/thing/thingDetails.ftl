<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
	<table id="thing_property" class="easyui-propertygrid"></table>
	<script type="text/javascript">
		flame.xui.loadGrid("thing_property", "com.thing.builder.ThingDetailPropertyBuilder", {
			queryParams: {
				oid : '${(primaryObj.oid)!}'
			}
		});
	</script>
</body>
</html>