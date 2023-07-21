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
package org.entando.entando.plugins.jpcontentlink.aps.system.service;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ImageAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.LinkAttribute;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.SingleMappingConfig;

/**
 * Contents manager. This implements all the methods needed to create and manage
 * the contents.
 */
public class ExtContentManager extends ContentManager {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(ExtContentManager.class);

    private IContentLinkManager contentlinkManager;

    /**
     * Return a complete content given its ID; it is possible to choose to
     * return the published -unmodifiable!- content or the working copy. It also
     * returns the data in the form of XML.
     *
     * @param id The ID of the content
     * @param onLine Specifies the type of the content to return: 'true'
     * references the published content, 'false' the freely modifiable one.
     * @return The requested content.
     * @throws EntException In case of error.
     */
    @Override
    public Content loadContent(String id, boolean onLine) throws EntException {
        Content content;

        content = getContentSimple(id, onLine);

        try {
            SingleMappingConfig mapping = contentlinkManager
                    .getMappingByContentType(content.getTypeCode())
                    .orElse(null);

            if (contentlinkManager.getConfiguration().isEnabled()
                    && mapping != null && mapping.isActive()) {

                // look for the link attribute...
                AttributeInterface targetAttribute = content.getAttribute(
                        mapping.getLinkingAttribute());
                // is it valid? If not, leave!
                if (targetAttribute == null) {
                    logger.debug("nothing to do, attribute not preset '{}' in '{}'", mapping.getLinkingAttribute(),
                            mapping.getTargetContentType());
                    return content;
                }
                logger.debug("attribute '{}' found in '{}'", mapping.getLinkingAttribute(),
                        content.getId());
                // inspect the type of the linking attribute
                if (targetAttribute instanceof LinkAttribute) {
                    SymbolicLink link = ((LinkAttribute) targetAttribute).getSymbolicLink();
                    String referencedContentId = link.getContentDestination();

                    if (StringUtils.isNotBlank(referencedContentId)
                            && referencedContentId.startsWith(mapping.getLinkedContentType())) {
                        importReferencedContentAttributes(content, referencedContentId, mapping);
                    } else {
                        logger.debug("unexpected content type for content {}, ignoring", referencedContentId);
                    }
                } else {
                    // TODO add monolist of link support
                    throw new RuntimeException("Unsupported link attribute type "
                            + targetAttribute.getClass().getCanonicalName());
                }
            } else if (!contentlinkManager.getConfiguration().isEnabled()) {
                logger.debug("content linking disables as requested");
            } else if (mapping == null) {
                logger.debug("no mapping found for '{}'", content.getTypeCode());
            } else if (!mapping.isActive()) {
                logger.debug("content type linking disabled for type '{}'", content.getTypeCode());
            } else {
                logger.debug("ignoring content type '{}' for linking", content.getTypeCode());
            }
        } catch (Throwable e) {
            logger.error("Error while processing content id '{}' for linking", id, e);
        }
        return content;
    }

    private Content importReferencedContentAttributes(Content dstContent, String srcId, SingleMappingConfig config)
            throws EntException {
        if (StringUtils.isNotBlank(srcId) && config != null) {
            logger.info("loading content '{}'", srcId);
            Content srcContent = getContentSimple(srcId, true);
            logger.info("loaded content '{}'", srcContent.getId());

            // process attributes
            config.getMapping().forEach( (dstAttributeName, srcAttributeName) -> {
                logger.debug("attribute '{}' of content type {}",
                        dstAttributeName, srcContent.getTypeCode());
                // get the attribute to fill
                AttributeInterface dstAttribute = dstContent.getAttribute(dstAttributeName);
                // does it exist?
                if (dstAttribute != null) {
                    if (dstAttribute instanceof ImageAttribute) {
                        processImageAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    } if (dstAttribute instanceof MonoTextAttribute) {
                        processMonoTextAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    }   else if (dstAttribute instanceof TextAttribute) {
                        processTextAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof DateAttribute) {
                        processDateAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof NumberAttribute) {
                        processNumberAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    }else {
                        // attribute not supported
                        logger.warn("unsupported attribute type for linking '{}'", dstAttribute.getType());
                    }
                } else {
                    logger.debug("attribute '{}' does not exist for content type '{}'",
                            dstAttributeName, dstContent.getTypeCode());
                }
            });


        } else if (config == null) {
            logger.info("no config found for content {} ", dstContent.getId());
        } else {
            logger.warn("no content referenced through attribute '{}' of content '{}' ",
                    config.getLinkingAttribute(),
                    dstContent.getId());
        }
        return dstContent;
    }

    private void processTextAttribute(Content dstContent, Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying TEXT from attribute '{}' to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            if (srcAttribute instanceof TextAttribute) {
                // get the text for every language
                HashMap<String, String> testMap = new HashMap<>(((TextAttribute) srcAttribute).getTextMap());
                // update the attribute
                ((TextAttribute)dstAttribute).setTextMap(testMap);
                logger.debug("TEXT copy completed successfully");
            } else {
                logger.error("attribute '{}' is not of the same type of attribute {} in content type '{}'", srcAttribute,
                        dstAttribute.getName(), srcContent.getTypeCode());
            }
        } else {
            logger.debug("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private void processMonoTextAttribute(Content dstContent, Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying MONOTEXT from attribute '{}' to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            if (srcAttribute instanceof MonoTextAttribute) {
                // get the text for the single language
                String tmpText = ((MonoTextAttribute) srcAttribute).getText();
                // update the attribute
                ((MonoTextAttribute)dstAttribute).setText(tmpText);
                logger.debug("MONOTEXT copy completed successfully");
            } else {
                logger.debug("attribute '{}' is not of the same type of attribute {} in content type '{}'", srcAttribute,
                        dstAttribute.getName(), srcContent.getTypeCode());
            }
        } else {
            logger.debug("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private void processImageAttribute(Content dstContent, Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying IMAGE from '{}' attribute to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            if (srcAttribute instanceof ImageAttribute) {

                ((ImageAttribute) srcAttribute).getResources().forEach((k, v) -> ((ImageAttribute) dstAttribute).setResource(v, k));

                Map<String, String> altMap = new HashMap<>(((ImageAttribute) srcAttribute).getResourceAltMap());
                ((ImageAttribute) dstAttribute).setMetadataMap("alt", altMap);

                Map<String, String> descrMap = new HashMap<>(
                        ((ImageAttribute) srcAttribute).getResourceDescriptionMap());
                ((ImageAttribute) dstAttribute).setMetadataMap("description", descrMap);

                Map<String, String> legendMap = new HashMap<>(((ImageAttribute) srcAttribute).getResourceLegendMap());
                ((ImageAttribute) dstAttribute).setMetadataMap("legend", legendMap);

                Map<String, String> titleMap = new HashMap<>(((ImageAttribute) srcAttribute).getResourceTitleMap());
                ((ImageAttribute) dstAttribute).setMetadataMap("title", titleMap);

                logger.debug("IMAGE copy completed successfully");
            } else {
                logger.debug("attribute '{}' is not of the same type of attribute {} in content type '{}'", srcAttribute,
                        dstAttribute.getName(), srcContent.getTypeCode());
            }
        } else {
            logger.error("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private void processDateAttribute(Content dstContent, Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying DATE from '{}' attribute to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            if (srcAttribute instanceof DateAttribute) {
                Date tmpDate = ((DateAttribute) srcAttribute).getDate();

                ((DateAttribute) dstAttribute).setDate(tmpDate);
            logger.debug("DATE copy completed successfully");
            } else {
                logger.debug("attribute '{}' is not of the same type of attribute {} in content type '{}'", srcAttribute,
                        dstAttribute.getName(), srcContent.getTypeCode());
            }
        } else {
            logger.debug("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private void processNumberAttribute(Content dstContent, Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying NUMBER from '{}' attribute to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            if (srcAttribute instanceof NumberAttribute) {
                BigDecimal tmpNumber = ((NumberAttribute) srcAttribute).getValue();

                ((NumberAttribute) dstAttribute).setValue(tmpNumber);
            logger.debug("NUMBER copy completed successfully");
            } else {
                logger.debug("attribute '{}' is not of the same type of attribute {} in content type '{}'", srcAttribute,
                        dstAttribute.getName(), srcContent.getTypeCode());
            }
        } else {
            logger.debug("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private Content getContentSimple(String id, boolean onLine) throws EntException {
        try {
            ContentRecordVO contentVo = this.loadContentVO(id);
            return this.createContent(contentVo, onLine);
        } catch (EntException e) {
            logger.error("Error while loading content : id {}", id, e);
            throw new EntException("Error while loading content : id " + id, e);
        }
    }


    public void setContentlinkManager(IContentLinkManager contentlinkManager) {
        this.contentlinkManager = contentlinkManager;
    }

    public IContentLinkManager getContentlinkManager() {
        return contentlinkManager;
    }
}
