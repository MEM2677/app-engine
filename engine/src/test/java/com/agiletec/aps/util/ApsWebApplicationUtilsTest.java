package com.agiletec.aps.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.entando.entando.aps.system.services.tenants.TenantManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.jsp.PageContext;

@ExtendWith(MockitoExtension.class)
class ApsWebApplicationUtilsTest {

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpSession httpSession;
    @Mock
    private ServletContext servletContext;
    @Mock
    private WebApplicationContext wac;
    @Mock
    private PageContext pageContext;

    @Test
    void shouldExtractBeanFromDifferentContextAndManageError(){
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {ApsWebApplicationUtils.getBean(TenantManager.class, servletContext);});

        Mockito.when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
                .thenReturn(wac);

        Mockito.when(wac.getBean(TenantManager.class))
                .thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {ApsWebApplicationUtils.getBean(TenantManager.class, servletContext);});

        Mockito.when(wac.getBean(TenantManager.class)).thenReturn(new TenantManager("{}", new ObjectMapper()));
        TenantManager tm = ApsWebApplicationUtils.getBean(TenantManager.class, servletContext);
        Assertions.assertNotNull(tm);

        Mockito.when(pageContext.getServletContext()).thenReturn(servletContext);
        tm = ApsWebApplicationUtils.getBean(TenantManager.class, pageContext);
        Assertions.assertNotNull(tm);

        Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);
        Mockito.when(httpSession.getServletContext()).thenReturn(servletContext);
        tm = ApsWebApplicationUtils.getBean(TenantManager.class, httpServletRequest);
        Assertions.assertNotNull(tm);


    }

}
