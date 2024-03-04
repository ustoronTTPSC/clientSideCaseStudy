package com.sg.data.utilities;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import wt.util.WTException;

public class SmsRedTextDataUtility extends DefaultDataUtility {

    @Override
    public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
        if (!(datum instanceof String)) {
            return "";
        }

        TextDisplayComponent coloredText = new TextDisplayComponent(componentId, String.valueOf(datum));
        coloredText.addStyleClass("calpastdaySel");
        return coloredText;
    }
}
