package com.flame.loader;

import java.io.File;

import com.flame.thing.IModelManaged;
import com.flame.thing.ThingModelHelper;

public class FlameDataSetLoader extends AbstractDataLoader {

	@Override
	public void executeLoad(FlameDataLoad flameLoad) throws Exception {
		for (FlameDataLoad.LoadFile loadFile : flameLoad.getFiles()) {
			AbstractDataLoader.load("loadFiles" + File.separator + loadFile.getFilename());
		}
		for (FlameDataLoad.LoadClass loadModel : flameLoad.getModels()) {
			Class<? extends IModelManaged> model = (Class<? extends IModelManaged>) Class.forName(loadModel.getClassname());
			ThingModelHelper.manager().registerThingModel(model);
		}
	}
}
