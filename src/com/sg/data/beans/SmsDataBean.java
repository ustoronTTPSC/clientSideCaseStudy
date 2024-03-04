package com.sg.data.beans;

import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.util.WTException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmsDataBean {

    private WTPart sms;

    private String telephoneNumber;

    private String messageText;

    private String censorInfo;

    private String smsStatus;

    public SmsDataBean(WTPart sms) throws WTException {
        this.sms = sms;

        Set<String> attrLabels = new HashSet<>();
        attrLabels.add("MessageText");
        attrLabels.add("SMSStatus");
        attrLabels.add("TelephoneNumber");

        TypeIdentifier softType = ClientTypedUtility.getTypeIdentifier("pl.ttpsc.SMS");
        TypeIdentifier documentTypeId = ClientTypedUtility.getTypeIdentifier(sms);
        if (documentTypeId.isDescendedFrom(softType)) {
            Map<String, Object> attributes = getAttributes(sms, attrLabels);
            for (Map.Entry entry: attributes.entrySet()) {
                if (entry.getKey().equals("MessageText")) {
                    messageText = (String) entry.getValue();
                } else if (entry.getKey().equals("TelephoneNumber")) {
                    telephoneNumber = (String) entry.getValue();
                } else if (entry.getKey().equals("SMSStatus")) {
                    smsStatus = (String) entry.getValue();
                }
            }
        }
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

    public WTPart getSms() {
        return sms;
    }

    public void setSms(WTPart sms) {
        this.sms = sms;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(String smsStatus) {
        this.smsStatus = smsStatus;
    }
}
