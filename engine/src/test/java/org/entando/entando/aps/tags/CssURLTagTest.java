/*
 * Copyright 2022-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.tags;

import static javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE;
import static javax.servlet.jsp.tagext.Tag.EVAL_PAGE;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CssURLTagTest {


    @Mock
    private ConfigInterface configSerivce;
    @Mock
    private IStorageManager storageManager;
    @Mock
    private PageContext pageContext;
    @Mock
    private ServletRequest servletRequest;
    @Mock
    private RequestContext reqCtx;
    @Mock
    private JspWriter writer;
    @InjectMocks
    private CssURLTag cssURLTag;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.lenient().when(pageContext.getRequest()).thenReturn(this.servletRequest);
        Mockito.lenient().when(servletRequest.getAttribute(RequestContext.REQCTX)).thenReturn(this.reqCtx);
        Mockito.lenient().when(pageContext.getOut()).thenReturn(this.writer);
    }

    @Test
    void testTag() throws Exception {
        try(MockedStatic<ApsWebApplicationUtils> apsWebApplicationUtils = Mockito.mockStatic(ApsWebApplicationUtils.class)){

            apsWebApplicationUtils.when(() -> ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER,
                    ConfigInterface.class,
                    this.pageContext)).thenReturn(configSerivce);
            Mockito.when(configSerivce.getParam(SystemConstants.PAR_RESOURCES_ROOT_URL)).thenReturn("/test1");
            cssURLTag.setIgnoreTenant(true);
            Assertions.assertEquals(EVAL_BODY_INCLUDE, cssURLTag.doStartTag());
            Assertions.assertEquals(EVAL_PAGE, cssURLTag.doEndTag());
            Assertions.assertEquals("static/css/", cssURLTag.getFolder());



        }
    }
}
