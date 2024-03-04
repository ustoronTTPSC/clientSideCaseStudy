package com.sg.form.processors;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.collections.CollectionsHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTList;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CreateSMSGatewayFormProcessor extends CreateObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
        FormResult formResult = super.doOperation(nmCommandBean, list);

        WTPart child = (WTPart) list.get(0).getObject();
        if (nmCommandBean.getPrimaryOid().getRefObject() instanceof WTPart) {
            WTPart parent = (WTPart) nmCommandBean.getPrimaryOid().getRefObject();

            try {
                createPartUsageLink(parent, child);
            } catch (WTPropertyVetoException e) {
                throw new RuntimeException(e);
            }
        }

        formResult.setStatus(FormProcessingStatus.SUCCESS);
        return formResult;
    }

    @Override
    public FormResult postTransactionProcess(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
        Object clickedButton = nmCommandBean.getRequestData().getParameterMap().get("clickedButton");

        if (nmCommandBean.getPrimaryOid().getRefObject() instanceof WTPart) {
            if (clickedButton != null) {
                if (((String[]) clickedButton)[0].equals("ext-gen35") || ((String[]) clickedButton)[0].equals("ext-gen37")) {
                    if (list.get(0).getObject() instanceof WTPart) {
                        WTPart part = (WTPart) list.get(0).getObject();
                        TypeIdentifier softType = ClientTypedUtility.getTypeIdentifier("pl.ttpsc.SMS");
                        TypeIdentifier documentTypeId = ClientTypedUtility.getTypeIdentifier(part);
                        if (documentTypeId.isDescendedFrom(softType)) {
                            if (((String[]) clickedButton)[0].equals("ext-gen35")) {
                                try {
                                    setAttributes((Persistable) part, Collections.singletonMap("SMSStatus", "Waiting"));
                                } catch (WTPropertyVetoException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (((String[]) clickedButton)[0].equals("ext-gen37")) {
                                try {
                                    setAttributes((Persistable) part, Collections.singletonMap("SMSStatus", "Drafted"));
                                } catch (WTPropertyVetoException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }
                    }
                }
            }
        }
        return super.postTransactionProcess(nmCommandBean, list);
    }

    public Persistable setAttributes(Persistable persistable, Map<String, Object> attributes) throws WTException, WTPropertyVetoException {
        Workable checkout = checkout((Workable) persistable);
        PersistableAdapter adapter = new PersistableAdapter(checkout, null, SessionHelper.getLocale(), new UpdateOperationIdentifier());
        adapter.load(attributes.keySet());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            adapter.set(entry.getKey(), entry.getValue());
        }
        Persistable persistable1 = adapter.apply();
        PersistenceHelper.manager.save(persistable1);
        checkin((Workable) persistable1);
        return persistable1;
    }


    public Workable checkout(Workable workable) throws WTException, WTPropertyVetoException {
        WTCollection workables = checkout(CollectionsHelper.singletonWTList(workable));
        return (Workable) workables.persistableIterator().next();
    }


    public WTCollection checkout(WTCollection collection) throws WTException, WTPropertyVetoException {
        collection.inflate();
        Collection<Workable> workables = collection.subCollection(Workable.class, true).persistableCollection();
        WTList checkedOutList = new WTArrayList();
        WTList toCheckOutList = new WTArrayList();
        for (Workable workable : workables) {
            if (!WorkInProgressHelper.isCheckedOut(workable)) {
                toCheckOutList.add(workable);
            } else if (WorkInProgressHelper.isWorkingCopy(workable)) {
                checkedOutList.add(workable);
            } else {
                checkedOutList.add(WorkInProgressHelper.service.workingCopyOf(workable));
            }
            if (!toCheckOutList.isEmpty()) {
                WTCollection checkouted = WorkInProgressHelper.service.checkout(toCheckOutList, WorkInProgressHelper.service.getCheckoutFolder(), null);
                for (Object link : checkouted.persistableCollection()) {
                    if (link instanceof CheckoutLink) {
                        checkedOutList.add(((CheckoutLink) link).getWorkingCopy());
                    }
                }
            }
        }
        return checkedOutList;
    }


    public Workable checkin(Workable workable) throws WTException, WTPropertyVetoException {
        Workable res;

        if (WorkInProgressHelper.isWorkingCopy(workable)) {
            res = WorkInProgressHelper.service.checkin(workable, "");
        } else if (WorkInProgressHelper.isCheckedOut(workable)) {
            res = WorkInProgressHelper.service.checkin(WorkInProgressHelper.service.workingCopyOf(workable), "");
        } else {
            throw new WTException("Checkout object");
        }
        return res;
    }

    public WTPartUsageLink createPartUsageLink(WTPart parent, WTPart child) throws WTException, WTPropertyVetoException {
        WTPart workingParentPart = (WTPart) checkout(parent);
        WTPartMaster childMasterPart = child.getMaster();
        WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(workingParentPart, childMasterPart);
        PersistenceHelper.manager.save(workingParentPart);
        PersistenceHelper.manager.save(link);
        checkin(workingParentPart);
        return link;
    }


}
