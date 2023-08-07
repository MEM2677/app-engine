package org.entando.entando.plugins.jpcontentlink.aps.system.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.link.IContentLinkManager;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.link.config.ContentLinkConfig;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(
        locations = {
                "classpath*:spring/custom/**.xml"
        }
)
@WebAppConfiguration("")
public class TestContentLinkManager extends AbstractControllerIntegrationTest {

    @Autowired
    private IContentManager contentManager;

    @Autowired
    private IContentLinkManager contentLinkManager;

    @Test
    void testLoadedConfiguration() throws Throwable {
        ContentLinkConfig cfg = contentLinkManager.getConfiguration();
        assertNotNull(cfg);
        assertTrue(cfg.isEnabled());
    }

}
