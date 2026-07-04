package com.flame.localize;

import com.flame.loader.AbstractDataLoader;
import com.flame.loader.FlameDataLoad;
import com.flame.orm.PersistenceHelper;

import java.lang.reflect.Constructor;
import java.util.List;

public class EnumeratedLoader extends AbstractDataLoader {

    @Override
    public void executeLoad(FlameDataLoad dataLoad) throws Exception {
        for (FlameDataLoad.LoadObject data : dataLoad.getData()) {
            List<?> list = this.queryObject(data);
            AbstractEnumerated<?> enumType = null;
            if (!list.isEmpty()) {
                enumType = (AbstractEnumerated<?>) list.get(0);
            } else {
                Constructor<?> constructor = data.getClazz().getConstructor();
                enumType = (AbstractEnumerated<?>) constructor.newInstance();
            }

            if (enumType == null)
                continue;

            enumType.setName(data.getAttribute("name"));
            enumType.setDisplay(data.getAttribute("display"));
            enumType.setEn_US(data.getAttribute("en_US"));
            enumType.setZh_CN(data.getAttribute("zh_CN"));
            enumType.setDescription(data.getAttribute("description"));

            String prompt = data.getAttribute("responsibility");
            if (prompt != null && !prompt.isEmpty()) {
                prompt =  prompt.replace("            ", "");
                setFieldIfSupported(enumType, "responsibility", prompt);
            }

            PersistenceHelper.service().save(enumType);
        }
    }
}
