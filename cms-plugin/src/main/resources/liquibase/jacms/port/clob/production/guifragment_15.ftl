<#assign wp=JspTaglibs["/aps-core"]>
<#assign formFieldNameVar = userFilterOptionVar.formFieldNames[0] >
<#assign formFieldValue = userFilterOptionVar.getFormFieldValue(formFieldNameVar) >
<#assign i18n_Attribute_Key = userFilterOptionVar.attribute.name >
<fieldset>
<legend><@wp.i18n key="${i18n_Attribute_Key}" /></legend>
<@wp.fragment code="jacms_content_viewer_list_userfilter_ent_Bool_io" escapeXml=false />
<div class="control-group">
	<div class="controls">
		<label for="true_${formFieldNameVar}" class="radio">
		<input name="${formFieldNameVar}" id="true_${formFieldNameVar}" <#if (formFieldValue??) && (formFieldValue == "true")>checked="checked"</#if> value="true" type="radio" />
		<@wp.i18n key="YES"/></label>
		<label for="false_${formFieldNameVar}" class="radio">
		<input name="${formFieldNameVar}" id="false_${formFieldNameVar}" <#if (formFieldValue??) && (formFieldValue == "false")>checked="checked"</#if> value="false" type="radio" />
		<@wp.i18n key="NO"/></label>
		<label for="both_${formFieldNameVar}" class="radio">
		<input name="${formFieldNameVar}" id="both_${formFieldNameVar}" <#if (formFieldValue??) && (formFieldValue == "both")>checked="checked"</#if> value="both" type="radio" />
		<@wp.i18n key="BOTH"/></label>
	</div>
</div>
</fieldset>