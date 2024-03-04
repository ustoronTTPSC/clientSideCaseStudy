<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:initializeItem operation="${createBean.edit}"
baseTypeName="wt.part.WTPart|pl.ttpsc.SMS"/>

<jca:wizard buttonList="CustomWizardButtonsCensor" title="Create Wizard with Common Steps">
    <jca:wizardStep action="editAttributesWizStep" type="object"/>
</jca:wizard>

<form id="mainform" action="http://akademia.ttpsc.pl:1080/Windchill/ptc1/action?u8=1&wizardActionClass=com.sg.form.processors.CreateSMSGatewayFormProcessor&unique_page_number=58251261913560_4&context=comp%24folderbrowser_table%24OR%3Awt.folder.Cabinet%3A263117%24&tableID=table__folderbrowser_OrgSite_TABLE&portlet=poppedup&wt.reqGrp=-1bmf98pchnvvg%3Blt5ro7g1%3B5908%3Bdzblzm%3B2814&ContainerOid=OR%3Awt.inf.container.OrgContainer%3A263065&oid=OR%3Awt.folder.Cabinet%3A263117&AjaxEnabled=component&actionName=createSMS" method="post">
    <input type="hidden" id="clickedButton" name="clickedButton" value="">
</form>

<script>
    function setClickedButton(buttonName) {
        document.getElementById('clickedButton').value = buttonName;
        if (buttonName === 'ext-gen35') {
            onSubmit();
        } else if (buttonName === 'ext-gen37') {
            onEditSubmit('saveButton');
        }
    }

    // Function to handle click event for button with id "ext-gen35", "ext-gen37"
    window.onload = function() {
        var buttonOK = document.getElementById('ext-gen35');
        var buttonDraft = document.getElementById('ext-gen37');
        if (buttonOK) {
            buttonOK.addEventListener('click', function() {
                setClickedButton('ext-gen35');
            });
        } else {
            console.log("Button OK not found");
        }
        if (buttonDraft) {
            buttonDraft.addEventListener('click', function() {
            setClickedButton('ext-gen37');
            });
        } else {
            console.log("Button Draft not found");
        }
    };
</script>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>