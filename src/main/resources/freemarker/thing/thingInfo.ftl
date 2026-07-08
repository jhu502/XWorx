<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
	<div id="thingTabs" class="easyui-tabs" data-options="fit:true,selected:${(parameters.selectedTab)!0}">
		<div title="Basics" data-options="fit:true,href:'freemarker/thing/thingDetails?oid=${(primaryObj.oid)!}'"></div>
		<div title="Configuration" data-options="fit:true,href:'freemarker/thing/thingConfigs?oid=${(primaryObj.oid)!}'"></div>
		<div title="Properties" data-options="fit:true,href:'freemarker/thing/thingProperties?oid=${(primaryObj.oid)!}'"></div>
		<div title="Services" data-options="fit:true,href:'freemarker/thing/thingServices?oid=${(primaryObj.oid)!}'"></div>
	</div>
	
	<script type="text/javascript">
		/**
		 * -0用户点击某个Tab后，再次点击其他ThingModel，自动进入对应的Tab；
		 */
		$(function() {
			$('#thingTabs').tabs({
				border : false,
				onSelect : function(title, index) {
					$('#selectedTab').attr("value", index);
				}
			});
		})
	</script>
</body>
</html>