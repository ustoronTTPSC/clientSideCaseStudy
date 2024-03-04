<%@ include file="/netmarkets/jsp/util/beginPopup.jspf" %>

<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="wt.part.WTPart" %>


<%
    List selectedInOpener = commandBean.getSelectedOidForPopup();
    NmOid selectedNmOid = (NmOid) selectedInOpener.get(0);
    WTPart part = (WTPart) selectedNmOid.getRefObject();

    String showIdentity = part.getNumber() + ", " + part.getName();
%>

Hello <%=showIdentity%>!
<%@ include file="/netmarkets/jsp/util/end.jspf"%>