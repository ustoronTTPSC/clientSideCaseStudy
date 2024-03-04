package com.sg.form.processors;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.DefaultEditFormProcessor;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

public class CensorVerificationFormProcessor extends DefaultEditFormProcessor {
    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {

        ArrayList selected = nmCommandBean.getSelectedInOpener();


        return super.doOperation(nmCommandBean, list);
    }
}
