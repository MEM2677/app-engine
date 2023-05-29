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
package com.agiletec.apsadmin.system;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.baseconfig.FileUploadUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.FileUploadInterceptor;

/**
 * Extension of default FileUploadInterceptor.
 *
 * @author E.Santoboni
 */
public class ApsFileUploadInterceptor extends FileUploadInterceptor {

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (null == super.maximumSize || super.maximumSize == 0) {
            ConfigInterface configManager = (ConfigInterface) ApsWebApplicationUtils.getBean(
                    SystemConstants.BASE_CONFIG_MANAGER, ServletActionContext.getRequest());
            this.setMaximumSize(FileUploadUtils.getFileUploadMaxSize(configManager));
        }
        return super.intercept(invocation);
    }
}
