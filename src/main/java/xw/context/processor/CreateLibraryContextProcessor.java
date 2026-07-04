package xw.context.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import xw.context.ContextHelper;
import xw.context.entity.LibraryTypeRB;
import xw.context.entity.XLibrary;

public class CreateLibraryContextProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String number = commandBean.getTextParameter("number");
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");
        String libraryType = commandBean.getTextParameter("libraryType");
        this.checkRequiredField(number, "number");
        this.checkRequiredField(name, "name");
        this.checkRequiredField(libraryType, "libraryType");

        LibraryTypeRB libTypeRB = LibraryTypeRB.toLibraryTypeRB(libraryType);
        XLibrary xLibrary = ContextHelper.service().createXLibraryContext(number, name, description, libTypeRB);

        formResult.setStatus(FormStatus.SUCCESS);
        formResult.setData(xLibrary);

        return formResult;
    }
}
