package com.sg.components.tables.builders;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.sg.data.beans.SmsDataBean;
import com.sg.smsGatewayRB;
import wt.fc.Persistable;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.config.LatestConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

@ComponentBuilder("com.sg.components.tables.builders.GetFBIAgentsTableBuilder")
public class GetFBIAgentsTableBuilder extends AbstractComponentBuilder {

    public static final String TABLE_ID = "showFBIDanderMessages";
    public static final String TELEPHONE_NUMBER_COLUMN_ID = "telephoneNumberColumn";
    public static final String MESSAGE_TEXT_COLUMN_ID = "messageTextColumn";


    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setTypes(WTPart.class.getName());
        table.setId(TABLE_ID);
        table.setLabel(new WTMessage(smsGatewayRB.class.getName(), smsGatewayRB.PRIVATE_CONSTANT_22, null).getLocalizedMessage());
        table.setSelectable(true);

        ColumnConfig icon = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.ICON, true);
        icon.setTargetObject("sms");
        table.addComponent(icon);

        ColumnConfig date = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED, true);
        date.setTargetObject("sms");
        table.addComponent(date);

        ColumnConfig creator = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.CREATED_BY, true);
        creator.setTargetObject("sms");
        table.addComponent(creator);

        ColumnConfig telephoneNumber = factory.newColumnConfig(TELEPHONE_NUMBER_COLUMN_ID, true);
        telephoneNumber.setTargetObject("telephoneNumber");
        telephoneNumber.setDataUtilityId(TELEPHONE_NUMBER_COLUMN_ID);
        telephoneNumber.setLabel("Telephone Number");
        table.addComponent(telephoneNumber);

        ColumnConfig messageText = factory.newColumnConfig(MESSAGE_TEXT_COLUMN_ID, true);
        messageText.setTargetObject("messageText");
        messageText.setDataUtilityId(MESSAGE_TEXT_COLUMN_ID);
        messageText.setLabel("Message Text");
        table.addComponent(messageText);

        return table;
    }

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

    public Map<Object, List> getNodes(List parents) throws WTException {
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

    public boolean filterSMS (WTPart part) throws WTException {
        boolean filter = false;
        String status = "";
        String message = "";
        Set<String> attrLabels = new HashSet<>();
        attrLabels.add("MessageText");
        attrLabels.add("SMSStatus");
        TypeIdentifier softType = ClientTypedUtility.getTypeIdentifier("pl.ttpsc.SMS");
        TypeIdentifier documentTypeId = ClientTypedUtility.getTypeIdentifier(part);
        if (documentTypeId.isDescendedFrom(softType)) {
            Map<String, Object> attributes = getAttributes(part, attrLabels);
            for (Map.Entry entry: attributes.entrySet()) {
                if (entry.getKey().equals("MessageText")) {
                    message = (String) entry.getValue();
                } else if (entry.getKey().equals("SMSStatus")) {
                    status = (String) entry.getValue();
                }
            }
            if (status.equals("Approved")) {
                if (message.toLowerCase().contains("bomb") || message.toLowerCase().contains("kill") || message.toLowerCase().contains("attack")) {
                    filter = true;
                }
            }
        }
        return filter;
    }

    public Map<String, Object> getAttributes(Persistable persistable, Set<String> attributes) throws WTException {
        PersistableAdapter adapter = new PersistableAdapter(persistable, null, SessionHelper.getLocale(), new DisplayOperationIdentifier());
        adapter.load(attributes);
        Map<String, Object> resMap = new HashMap<>();
        for (String attr: attributes) {
            resMap.put(attr, adapter.get(attr));
        }
        return resMap;
    }
}
