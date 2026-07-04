package plm.dynamic.engine.load;

import plm.dynamic.engine.cvm.Emulator;

/**
 * 
 * @author hujin
 * @version 1.1
 * 
 */
public interface Loader {
	Emulator loadData2Emulator();

	Emulator loadData2Emulator(Emulator parent);
}
