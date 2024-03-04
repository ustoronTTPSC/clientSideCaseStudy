package com.sg.components.tables.builders;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.sg.data.SmsStates;
import com.sg.smsGatewayRB;
import wt.part.WTPart;
import wt.util.WTMessage;

@ComponentBuilder("com.sg.components.tables.builders.GetDraftMessagesTableBuilder")
public class GetDraftMessagesTableBuilder extends MessagesForClientTableBuilder {

    public static final String TABLE_ID = "showDraftMessages";

    public static final String TABLE_TOOLBAR_ID = "showDraftMessages toolbar";

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setTypes(WTPart.class.getName());
        table.setId(TABLE_ID);
        table.setActionModel(TABLE_TOOLBAR_ID);
        table.setLabel(new WTMessage(smsGatewayRB.class.getName(), smsGatewayRB.PRIVATE_CONSTANT_2, null).getLocalizedMessage());
        table.setSelectable(true);

        ColumnConfig icon = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.ICON, true);
        icon.setTargetObject(MAIN_TARGET_OBJECT);
        table.addComponent(icon);

        ColumnConfig telephoneNumber = factory.newColumnConfig(TELEPHONE_NUMBER_COLUMN_ID, true);
        telephoneNumber.setTargetObject(TARGET_OBJECT_TELEPHONE_NUMBER);
        telephoneNumber.setDataUtilityId(TELEPHONE_NUMBER_COLUMN_ID);
        telephoneNumber.setLabel("Telephone Number");
        table.addComponent(telephoneNumber);

        ColumnConfig messageText = factory.newColumnConfig(MESSAGE_TEXT_COLUMN_ID, true);
        messageText.setTargetObject(TARGET_OBJECT_MESSAGE_TEXT);
        messageText.setDataUtilityId(MESSAGE_TEXT_COLUMN_ID);
        messageText.setLabel("Message Text");
        table.addComponent(messageText);

        return table;
    }

    @Override
    protected boolean isSmsStatusSupportedInView(String status) {
        return SmsStates.DRAFT.getStatus().equals(status);
    }

}
