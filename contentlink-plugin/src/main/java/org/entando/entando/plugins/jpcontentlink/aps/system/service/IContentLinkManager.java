package org.entando.entando.plugins.jpcontentlink.aps.system.service;

import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.ContentLinkConfig;

public interface IContentLinkManager {
    String BEAN_ID = "jpcontentlinkManager";
    String CONFIG_ITEM = "jpcontentlink_config";

    Content processContent(Content content) throws EntException;

    /**
     * Export the configuration
     * @return the copy of the current configuration
     * @throws Throwable in case of error
     */
    ContentLinkConfig getConfiguration() throws Throwable;
}
