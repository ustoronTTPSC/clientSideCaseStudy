package com.sg.data.beans;

import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.util.WTException;

import java.util.*;

public class SmsCensorDataBean {

    private WTPart sms;
    private String receiver;
    private String status;

    private String dangerous;

    public SmsCensorDataBean(WTPart sms) throws WTException {
        this.sms = sms;


        Set<String> attrLabels = new HashSet<>();
        attrLabels.add("SMSStatus");
        attrLabels.add("TelephoneNumber");
        attrLabels.add("MessageText");
        List<String> dangerousWords = new ArrayList<>();
        dangerousWords.add("bomb");
        dangerousWords.add("kill");
        dangerousWords.add("attack");

        TypeIdentifier softType = ClientTypedUtility.getTypeIdentifier("pl.ttpsc.SMS");
        TypeIdentifier documentTypeId = ClientTypedUtility.getTypeIdentifier(sms);
        if (documentTypeId.isDescendedFrom(softType)) {
            Map<String, Object> attributes = getAttributes(sms, attrLabels);
            for (Map.Entry entry: attributes.entrySet()) {
                if (entry.getKey().equals("SMSStatus")) {
                    status = (String) entry.getValue();
                } else if (entry.getKey().equals("TelephoneNumber")) {
                    receiver = (String) entry.getValue();
                } else if (entry.getKey().equals("MessageText")) {
                    if (filterHasDangerous(dangerousWords, (String) entry.getValue())) {
                        dangerous = "!!!!!!!!";
                    }
                }
            }
        }
    }

    boolean filterHasDangerous (List<String> dangerousWords, String messageText) {
        boolean filter = false;
        for (String word: dangerousWords) {
            if (messageText.toLowerCase().contains(word)) {
                filter = true;
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

    public WTPart getSms() {
        return sms;
    }

    public void setSms(WTPart sms) {
        this.sms = sms;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDangerous() {
        return dangerous;
    }

    public void setDangerous(String dangerous) {
        this.dangerous = dangerous;
    }
}
