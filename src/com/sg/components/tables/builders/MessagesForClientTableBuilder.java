package com.sg.components.tables.builders;

import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.sg.data.beans.SmsDataBean;
import wt.fc.Persistable;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public abstract class MessagesForClientTableBuilder extends AbstractComponentBuilder {

    protected static final String MAIN_TARGET_OBJECT = "sms";
    protected static final String TARGET_OBJECT_TELEPHONE_NUMBER = "telephoneNumber";
    protected static final String TARGET_OBJECT_MESSAGE_TEXT = "messageText";
    protected static final String TARGET_OBJECT_SMS_STATUS = "smsStatus";

    protected static final String TELEPHONE_NUMBER_COLUMN_ID = "telephoneNumberColumn";
    protected static final String MESSAGE_TEXT_COLUMN_ID = "messageTextColumn";
    protected static final String SMS_STATUS_COLUMN_ID = "smsStatusColumn";

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        NmHelperBean helperBean = ((JcaComponentParams) componentParams).getHelperBean();
        NmCommandBean nmCommandBean = helperBean.getNmCommandBean();
        Object refObject = nmCommandBean.getPrimaryOid().getRefObject();
        Set<Object> beans = new HashSet<>();

        if (refObject instanceof WTPart) {
            WTPart wtPart = (WTPart) refObject;
            List parentNode = new ArrayList();
            parentNode.add(wtPart);

            Map<Object, List> nodes = getNodes(parentNode);

            for (Map.Entry entry: nodes.entrySet()) {
                List children = (List) entry.getValue();
                for (Object child: children) {
                    if (child instanceof WTPart) {
                        if (filterSMS((WTPart) child)) {
                            beans.add(new SmsDataBean((WTPart) child));
                        }
                    }
                }
            }

        } else {
            return null;
        }
        return beans;
    }

    protected Map<Object, List> getNodes(List parents) throws WTException {
        Map<Object, List> result = new HashMap<>();

        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents), new LatestConfigSpec());

        for (ListIterator i = parents.listIterator(); i.hasNext(); ) {
            WTPart parent = (WTPart) i.next();
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List<Object> children = new ArrayList<>();

            for (Persistable[] child: branch) {
                children.add(child[1]);
            }
            result.put(parent, children);
        }
        return result;
    }

    protected boolean filterSMS(WTPart part) throws WTException {
        String status = "";
        Set<String> attrLabels = new HashSet<>();
        attrLabels.add("SMSStatus");
        TypeIdentifier softType = ClientTypedUtility.getTypeIdentifier("pl.ttpsc.SMS");
        TypeIdentifier documentTypeId = ClientTypedUtility.getTypeIdentifier(part);
        if (documentTypeId.isDescendedFrom(softType)) {
            Map<String, Object> attributes = getAttributes(part, attrLabels);
            for (Map.Entry entry: attributes.entrySet()) {
                if (entry.getKey().equals("SMSStatus")) {
                    status = (String) entry.getValue();
                }
            }
            return isSmsStatusSupportedInView(status);
        }
        return false;
    }

    protected Map<String, Object> getAttributes(Persistable persistable, Set<String> attributes) throws WTException {
        PersistableAdapter adapter = new PersistableAdapter(persistable, null, SessionHelper.getLocale(), new DisplayOperationIdentifier());
        adapter.load(attributes);
        Map<String, Object> resMap = new HashMap<>();
        for (String attr: attributes) {
            resMap.put(attr, adapter.get(attr));
        }
        return resMap;
    }


    protected abstract boolean isSmsStatusSupportedInView(String status);
}
