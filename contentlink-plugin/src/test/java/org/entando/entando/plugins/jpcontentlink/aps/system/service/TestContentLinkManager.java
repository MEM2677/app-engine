package org.entando.entando.plugins.jpcontentlink.aps.system.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.ContentLinkConfig;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TestContentLinkManager extends AbstractControllerIntegrationTest {

    @Autowired
    private IContentManager contentManager;

    @Autowired
    private IContentLinkManager contentLinkManager;

    @Test
    void testLoadedConfiguration() throws Throwable {
        ContentLinkConfig cfg = contentLinkManager.getConfiguration();
        assertNotNull(cfg);
        assertTrue(cfg.isActive());
    }
    @Test
    void testManager() throws  Throwable {
        Content content = contentManager.loadContent("ART102", false);
        System.out.println(">>> " + content.getId());
    }

}
