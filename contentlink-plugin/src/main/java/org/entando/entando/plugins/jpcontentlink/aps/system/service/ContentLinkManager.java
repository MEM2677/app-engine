package org.entando.entando.plugins.jpcontentlink.aps.system.service;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Optional;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.ContentLinkConfig;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.SingleMappingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
<?xml version="1.0" encoding="UTF-8"?>
<ContentLinkConfig>
   <enabled>true</enabled>
   <contentTypes>
      <contentTypes>
         <targetContentType>EVN</targetContentType>
         <linkedContentType>PPL</linkedContentType>
         <linkingAttribute>title</linkingAttribute>
         <active>true</active>
         <mapping>
            <targetTitle>linkedTitle</targetTitle>
            <targetImg>linkedImg</targetImg>
            <targetDate>linkedDate</targetDate>
         </mapping>
      </contentTypes>
   </contentTypes>
</ContentLinkConfig>
 */

public class ContentLinkManager extends AbstractService implements IContentLinkManager {

    private ConfigInterface configManager;

    private ContentLinkConfig config;

    private static final Logger logger = LoggerFactory.getLogger(ContentLinkManager.class);

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
    public Optional<SingleMappingConfig> getMappingByContentType(String type) {
        return config != null ? config.getContentTypes()
                .stream()
                .filter(types -> StringUtils.isNotBlank(types.getTargetContentType())
                        && types.getTargetContentType().equals(type))
                .findFirst() : Optional.of(null);
    }

    @Override
    public ContentLinkConfig getConfiguration() throws Throwable {
        ContentLinkConfig export = new ContentLinkConfig();
        BeanUtils.copyProperties(export, getConfig());
        return export;
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

}
