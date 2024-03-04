package com.sg.components.tables.builders;
import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.mvc.ds.server.jmx.PerformanceConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.sg.data.beans.SmsCensorDataBean;
import com.sg.smsGatewayRB;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.config.LatestConfigSpec;

import java.util.*;

@ComponentBuilder("com.sg.components.tables.builders.GetCensorsTableBuilder")
public class GetCensorsTableBuilder extends AbstractComponentBuilder  {//AbstractComponentBuilder

    public static final String TABLE_ID = "showCensorsParts";

    public static final String TABLE_TOOLBAR_ID = "showCensorsParts toolbar";
    public static final String RECEIVER_COLUMN_ID = "receiverColumn";
    public static final String STATUS_COLUMN_ID = "statusColumn";
    public static final String DANGEROUS_COLUMN_ID = "dangerousCensorsColumn";
    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setTypes(WTPart.class.getName());
        table.setId(TABLE_ID);
        table.setActionModel(TABLE_TOOLBAR_ID);
        table.setLabel(new WTMessage(smsGatewayRB.class.getName(), smsGatewayRB.PRIVATE_CONSTANT_3, null).getLocalizedMessage());
        table.setSelectable(true);

        ColumnConfig icon = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.ICON, true);
        icon.setTargetObject("sms");
        table.addComponent(icon);

        ColumnConfig name = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NAME, true);
        name.setTargetObject("sms");
        table.addComponent(name);

        ColumnConfig number = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NUMBER, true);
        number.setTargetObject("sms");
        table.addComponent(number);

        ColumnConfig creator = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.CREATED_BY, true);
        creator.setTargetObject("sms");
        table.addComponent(creator);

        ColumnConfig receiver = factory.newColumnConfig(RECEIVER_COLUMN_ID, true);
        receiver.setTargetObject("receiver");
        receiver.setDataUtilityId(RECEIVER_COLUMN_ID);
        receiver.setLabel("Receiver");
        table.addComponent(receiver);

        ColumnConfig status = factory.newColumnConfig(STATUS_COLUMN_ID, true);
        status.setTargetObject("status");
        status.setDataUtilityId(STATUS_COLUMN_ID);
        status.setLabel("Status");
        table.addComponent(status);

        ColumnConfig iconDangerous = factory.newColumnConfig(DANGEROUS_COLUMN_ID, true);
        iconDangerous.setTargetObject("dangerous");
        iconDangerous.setDataUtilityId(DANGEROUS_COLUMN_ID);
        iconDangerous.setLabel("Danger");
        table.addComponent(iconDangerous);

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
                            beans.add(new SmsCensorDataBean((WTPart) child));
                        }
                    }
                }
            }

        } else {
            return null;
        }
        return beans;
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
            if (status.equals("Waiting")) {
                filter = true;
            }
//            if (status.equals("Approved")) {
//                if (message.toLowerCase().contains("bomb") || message.toLowerCase().contains("kill") || message.toLowerCase().contains("attack")) {
//                    filter = true;
//                }
//            }
        }
        return filter;
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
