/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.agiletec.apsadmin.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Initialise the tracer @{@link AttributeTracer}.
 * @author S.Puddu
 */
public class TracerFactoryTag extends ComponentTagSupport {
	
	@Override
	public Component getBean(ValueStack valueStack, HttpServletRequest req,	HttpServletResponse res) {
		String langCode = this.getActualValue(this.getLang());
		ILangManager langManager = (ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, req);
		Lang lang = langManager.getLang(langCode);
		TracerFactory tracerFactory = new TracerFactory(valueStack, lang);
		valueStack.getContext().put(this.getVar(), tracerFactory.getAttributeTracer());
		return tracerFactory;
	}
	
	private String getActualValue(String value) {
		if (value.startsWith("%{") && value.endsWith("}")) {
			value = value.substring(2, value.length() - 1);
			return (String) this.getStack().findValue(value, String.class);
		}
		return null;
	}

	public void setLang(String lang) {
		this._lang = lang;
	}
	public String getLang() {
		return _lang;
	}

	/**
	 * Set the name used to reference the value pushed into the Value Stack.
	 * @param var The name of the variable
	 */
	public void setVar(String var) {
		this._var = var;
	}
	
	/**
	 * Get the name used to reference the value pushed into the Value Stack.
	 * @return The name of the variable
	 */
	public String getVar() {
		return _var;
	}

	private String _lang;
	private String _var;

}
