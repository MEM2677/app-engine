package org.entando.entando.plugins.jpcontentlink.aps.system.service;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.LinkAttribute;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Optional;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.ContentLinkConfig;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.SingleMappingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

/*
<?xml version="1.0" encoding="UTF-8"?>
<ContentLinkConfig>
   <active>true</active>
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

@Aspect
public class ContentLinkManager extends AbstractService implements IContentLinkManager {

    private ContentManager contentManager;

    private ConfigInterface configManager;

    private ContentLinkConfig config;

    private static final Logger logger = LoggerFactory.getLogger(ContentLinkManager.class);

    public ContentLinkManager() {
    }

    @Override
    public void init() throws Exception {
        try {
            loadConfig();
        } catch (Throwable t) {
            ContentLinkConfig defaultCfg = new ContentLinkConfig();
            defaultCfg.setActive(false);
            setConfig(defaultCfg);
            logger.error("Error loading configuration: " + t.getMessage());
        }
    }

    protected void loadConfig() throws Throwable {
        XmlMapper xmlMapper = new XmlMapper();
//        String configDbString = configManager.getConfigItem(IContentLinkManager.CONFIG_ITEM);

        String configDbString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<ContentLinkConfig>\n"
                + "   <active>true</active>\n"
                + "   <contentTypes>\n"
                + "      <contentTypes>\n"
                + "         <targetContentType>EVN</targetContentType>\n"
                + "         <linkedContentType>PPL</linkedContentType>\n"
                + "         <linkingAttribute>reflink</linkingAttribute>\n"
                + "         <active>true</active>\n"
                + "         <mapping>\n"
                + "            <tgttitle>lkdtitle</tgttitle>\n"
                + "            <tgtimg>lkdimg</tgtimg>\n"
                + "            <tgtdate>lkddate</tgtdate>\n"
                + "         </mapping>\n"
                + "      </contentTypes>\n"
                + "   </contentTypes>\n"
                + "</ContentLinkConfig>";

        ContentLinkConfig config
                = xmlMapper.readValue(configDbString, ContentLinkConfig.class);
        setConfig(config);
    }

    //            "&& !within(org.entando.entando.plugins.jpcontentlink..*) " +
    @Around("execution(* com.agiletec.plugins.jacms.aps.system.services.content.ContentManager.loadContent(..))  " +
            "&& args(id, online)")
    @Order(999)
    public Content modifyContentOnLoading(ProceedingJoinPoint joinPoint, String id, boolean online) throws Throwable {
        long start = System.currentTimeMillis();
        Content content = (Content) joinPoint.proceed();
        boolean isWatched = isContentWatched(content);

        logger.info("analysing loading request of content {}", content.getId());
        if ((online || true) && isWatched) {

            System.out.println("#1# " + content.getId());
            System.out.println("#2# " + content.getTypeCode());
            System.out.println("#3# " + id);
            System.out.println("#4# " + online);

            processContent(content);

//            content.setId("MUCCI");

        } else if (!online) {
            logger.info("ignoring draft version of content {} ", content.getId());
        } else { // if (!isWatched) {
            logger.info("ignoring content type not watched {}", content.getTypeCode());
        }

        long executionTime = System.currentTimeMillis() - start;

        System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");
        return content;
    }

    private boolean isContentWatched(Content content) {
        if (getConfig().isActive()
                && content != null) {
            return config.getContentTypes()
                    .stream()
                    .filter(types -> StringUtils.isNotBlank(types.targetContentType)
                            && types.targetContentType.equals(content.getTypeCode()))
                    .findFirst()
                    .isPresent();
        }
        return false;
    }

    private Optional<SingleMappingConfig> getMappingByContentType(String type) {
        return config.getContentTypes()
                .stream()
                .filter(types -> StringUtils.isNotBlank(types.targetContentType)
                        && types.targetContentType.equals(type))
                .findFirst();
    }

    @Override
    public Content processContent(Content content) throws EntException {
        Optional<SingleMappingConfig> opt = getMappingByContentType(content.getTypeCode());
        SingleMappingConfig linkConfig = opt
                .orElseThrow(() -> new RuntimeException(
                        "expected configuration for this content: " + content.getTypeCode()));
        // look for the link attribute
        AttributeInterface targetAttribute = content.getAttribute(
                linkConfig.getLinkingAttribute());
        if (targetAttribute == null) {
            logger.info("nothing to do, attribute not preset '{}' in '{}'", linkConfig.getLinkingAttribute(),
                    linkConfig.getTargetContentType());
            return content;
        }
        logger.info("attribute '{}' found in '{}'", linkConfig.getLinkingAttribute(),
                content.getId());
        if (targetAttribute instanceof LinkAttribute) {
            SymbolicLink link = ((LinkAttribute) targetAttribute).getSymbolicLink();
            String referencedContentId = link.getContentDestination();

            System.out.println("§§§ " + link.getContentDestination());

            importReferencedContentAttributes(content, referencedContentId, linkConfig);
        } else {
            // TODO add monolist of link support
            throw new RuntimeException("Unsupported link attribute type "
                    + targetAttribute.getClass().getCanonicalName());
        }
        return content;
    }

    private Content importReferencedContentAttributes(Content destContent, String srcId, SingleMappingConfig mapping)
            throws EntException {
        if (StringUtils.isNotBlank(srcId) && mapping != null) {
            logger.info("loading content '{}'", srcId);
            Content content = contentManager.loadContent(srcId, true);
            logger.info("loaded content '{}'", content.getId());

            System.out.println("!!! " + content.getId());

        } else if (mapping == null) {
            logger.info("no mapping found for content {} ", destContent.getId());
        } else {
            logger.warn("no content referenced through attribute '{}' of content '{}' ",
                    mapping.getLinkingAttribute(),
                    destContent.getId());
        }
        return destContent;
    }

    @Override
    public ContentLinkConfig getConfiguration() throws Throwable {
        ContentLinkConfig export = new ContentLinkConfig();
        BeanUtils.copyProperties(export, getConfig());
        return export;
    }

    public ContentManager getContentManager() {
        return this.contentManager;
    }

    public ConfigInterface getConfigManager() {
        return this.configManager;
    }

    public ContentLinkConfig getConfig() {
        return this.config;
    }

    public void setContentManager(ContentManager contentManager) {
        this.contentManager = contentManager;
    }

    public void setConfigManager(ConfigInterface configManager) {
        this.configManager = configManager;
    }

    public void setConfig(ContentLinkConfig config) {
        this.config = config;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ContentLinkManager)) {
            return false;
        }
        final ContentLinkManager other = (ContentLinkManager) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$contentManager = this.getContentManager();
        final Object other$contentManager = other.getContentManager();
        if (this$contentManager == null ? other$contentManager != null
                : !this$contentManager.equals(other$contentManager)) {
            return false;
        }
        final Object this$configManager = this.getConfigManager();
        final Object other$configManager = other.getConfigManager();
        if (this$configManager == null ? other$configManager != null
                : !this$configManager.equals(other$configManager)) {
            return false;
        }
        final Object this$config = this.getConfig();
        final Object other$config = other.getConfig();
        if (this$config == null ? other$config != null : !this$config.equals(other$config)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ContentLinkManager;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $contentManager = this.getContentManager();
        result = result * PRIME + ($contentManager == null ? 43 : $contentManager.hashCode());
        final Object $configManager = this.getConfigManager();
        result = result * PRIME + ($configManager == null ? 43 : $configManager.hashCode());
        final Object $config = this.getConfig();
        result = result * PRIME + ($config == null ? 43 : $config.hashCode());
        return result;
    }

    public String toString() {
        return "ContentLinkManager(contentManager=" + this.getContentManager() + ", configManager="
                + this.getConfigManager() + ", config=" + this.getConfig() + ")";
    }
}
