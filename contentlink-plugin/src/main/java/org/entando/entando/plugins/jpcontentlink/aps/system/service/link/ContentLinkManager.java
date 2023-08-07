package org.entando.entando.plugins.jpcontentlink.aps.system.service.link;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.event.PublicContentChangedEvent;
import com.agiletec.plugins.jacms.aps.system.services.content.event.PublicContentChangedObserver;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentDto;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.List;
import java.util.Optional;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.plugins.jacms.aps.system.services.content.ContentServiceUtilizer;
import org.entando.entando.plugins.jacms.aps.system.services.content.IContentService;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.link.config.ContentLinkConfig;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.link.config.SingleMappingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
<ContentLinkConfig>
	<enabled>true</enabled>
	<contentTypes>
		<contentTypes>
			<targetContentType>EVN</targetContentType>
			<linkedContentType>PPL</linkedContentType>
			<linkingAttribute>reflist</linkingAttribute>
			<targetList>tgtlist</targetList>
			<active>true</active>
			<mapping>
				<tgttitle>lkdtitle</tgttitle>
				<tgtimg>lkdimg</tgtimg>
				<tgtdate>lkddate</tgtdate>
				<tgtnumber>lkdnumber</tgtnumber>
				<tgtmntxt>lkdmntxt</tgtmntxt>
			</mapping>
		</contentTypes>
	</contentTypes>
</ContentLinkConfig>
 */

public class ContentLinkManager extends AbstractService implements IContentLinkManager, PublicContentChangedObserver {

    private ConfigInterface configManager;

    private ContentLinkConfig config;

    private static final Logger logger = LoggerFactory.getLogger(ContentLinkManager.class);
    private IContentService contentService;
    private ICacheInfoManager cacheInfoManager;

    public ContentLinkManager() {
    }

    @Override
    public void init() throws Exception {
        try {
            loadConfig();
            if (config.isEnabled()) {
                config.getContentTypes()
                        .stream()
                        .filter(mapping -> mapping.isActive())
                        .forEach(mapping -> logger.info(" content type linking enabled for type '{}'", mapping.getTargetContentType()));

            } else {
                logger.warn("** Content link plugin disabled **");
            }
        } catch (Throwable t) {
            ContentLinkConfig defaultCfg = new ContentLinkConfig();
            defaultCfg.setEnabled(false);
            setConfig(defaultCfg);
            logger.error("Error loading configuration: " + t.getMessage());
        }
    }

    protected void loadConfig() throws Throwable {
        XmlMapper xmlMapper = new XmlMapper();
        String configDbString = configManager.getConfigItem(IContentLinkManager.CONFIG_ITEM);
        ContentLinkConfig config
                = xmlMapper.readValue(configDbString, ContentLinkConfig.class);
        setConfig(config);
    }


    @Override
    public Optional<SingleMappingConfig> getMappingConfigurationByReferencingContentType(String type) {
        return config != null ? config.getContentTypes()
                .stream()
                .filter(types -> StringUtils.isNotBlank(types.getTargetContentType())
                        && types.getTargetContentType().equals(type))
                .findFirst() : Optional.of(null);
    }

    @Override
    public Optional<SingleMappingConfig> getMappingConfigurationByReferencedContentType(String type) {
        return config != null ? config.getContentTypes()
                .stream()
                .filter(types -> StringUtils.isNotBlank(types.getLinkedContentType())
                        && types.getLinkedContentType().equals(type))
                .findFirst() : Optional.of(null);
    }

    @Override
    public ContentLinkConfig getConfiguration() throws Throwable {
        ContentLinkConfig export = new ContentLinkConfig();
        BeanUtils.copyProperties(export, getConfig());
        return export;
    }

    @Override
    public void updateFromPublicContentChanged(PublicContentChangedEvent event) {
        switch (event.getOperationCode()) {
            case PublicContentChangedEvent.INSERT_OPERATION_CODE:
            case PublicContentChangedEvent.UPDATE_OPERATION_CODE:
                checkIfReferencedContent(event.getContent().getId(), event.getContent().getTypeCode());
                break;
        }
    }

    private void checkIfReferencedContent(String contentId, String contentType) {
        getMappingConfigurationByReferencedContentType(contentType)
                .ifPresent(cfg -> {
                    final List<ContentDto> utilizers =
                            ((ContentServiceUtilizer) this.getContentService()).getContentUtilizer(contentId);

                    if (utilizers != null && !utilizers.isEmpty()) {
                        utilizers.forEach(utilizer -> {
                            evictContent(utilizer.getId());
                        });
                    }
                });
    }

    protected void evictContent(String contentId) {
        logger.debug("evicting content {} from cache", contentId);

        this.getCacheInfoManager().flushGroup(
                ICacheInfoManager.DEFAULT_CACHE_NAME, JacmsSystemConstants.CONTENT_CACHE_GROUP_PREFIX + contentId);
//        this.getCacheInfoManager().flushGroup(ICacheInfoManager.DEFAULT_CACHE_NAME,
//                JacmsSystemConstants.CONTENTS_ID_CACHE_GROUP_PREFIX + content.getTypeCode());
        this.getCacheInfoManager().flushEntry(ICacheInfoManager.DEFAULT_CACHE_NAME,
                JacmsSystemConstants.CONTENT_CACHE_PREFIX + contentId);
    }

    public ConfigInterface getConfigManager() {
        return this.configManager;
    }

    public ContentLinkConfig getConfig() {
        return this.config;
    }

    public void setConfigManager(ConfigInterface configManager) {
        this.configManager = configManager;
    }

    public void setConfig(ContentLinkConfig config) {
        this.config = config;
    }

    public IContentService getContentService() {
        return contentService;
    }

    public void setContentService(IContentService contentService) {
        this.contentService = contentService;
    }

    public ICacheInfoManager getCacheInfoManager() {
        return cacheInfoManager;
    }

    public void setCacheInfoManager(ICacheInfoManager cacheInfoManager) {
        this.cacheInfoManager = cacheInfoManager;
    }

}
