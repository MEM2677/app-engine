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
package org.entando.entando.plugins.jpcontentlink.aps.system.service.content;

import static org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils.AttributeHelper.copyDateAttribute;
import static org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils.AttributeHelper.copyEnumeratorAttribute;
import static org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils.AttributeHelper.copyHyperTextAttribute;
import static org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils.AttributeHelper.copyImageAttribute;
import static org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils.AttributeHelper.copyMonoTextAttribute;
import static org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils.AttributeHelper.copyNumberAttribute;
import static org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils.AttributeHelper.copyTextAttribute;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.EnumeratorAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.HypertextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ImageAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.LinkAttribute;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.link.IContentLinkManager;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.link.config.SingleMappingConfig;

/**
 * Contents manager. This implements all the methods needed to create and manage
 * the contents.
 * TODO - check whether there's the need to declare in advance the referenced content type
 * TODO - multiple configuration for a single content type
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
                    .getMappingConfigurationByReferencingContentType(content.getTypeCode())
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
                    final SymbolicLink link = ((LinkAttribute) targetAttribute).getSymbolicLink();
                    final String referencedContentId = link.getContentDestination();

                    if (StringUtils.isNotBlank(referencedContentId)
                            && referencedContentId.startsWith(mapping.getLinkedContentType())) {
                        importFromReferencedContentLink(content, referencedContentId, mapping);
                    } else {
                        logger.debug("unexpected content type for content {}, ignoring", referencedContentId);
                    }
                } else if (targetAttribute instanceof MonoListAttribute) {
                    final List<AttributeInterface> linkList = ((MonoListAttribute) targetAttribute).getAttributes();
                    final String targetListAttributeName = mapping.getTargetList();
                    final AttributeInterface targetList = content.getAttribute(targetListAttributeName);

                    if (linkList == null || targetList == null) {
                        logger.warn("the list of link OR the list of composite (or both!) is null");
                        return content;
                    }
                    if (!(targetList instanceof MonoListAttribute)) {
                        logger.warn("invalid attribute for {}, monolist expected", targetListAttributeName);
                        return content;
                    }

                    // clean the monolist of composite before proceeding
                    ((MonoListAttribute)targetList).getAttributes().clear();

                    linkList.stream()
                            .filter(item -> item instanceof LinkAttribute)
                            .forEach(link -> {
                                final SymbolicLink symbolicLink = ((LinkAttribute) link).getSymbolicLink();
                                final String referencedContentId = symbolicLink.getContentDestination();

                                logger.debug("handling reference to {}", referencedContentId);
                                importFromReferencedContentList(targetList, referencedContentId, mapping);
                            });
                } else {
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
            logger.error("Error while processing content id '" + id + "' for linking", e);
        }
        return content;
    }

    private void importFromReferencedContentList(AttributeInterface dstMonolistAttribute, String referencedContentId, SingleMappingConfig config) {
        if (dstMonolistAttribute instanceof MonoListAttribute
                && StringUtils.isNotBlank(referencedContentId) && config != null) {
            final MonoListAttribute monolist = (MonoListAttribute) dstMonolistAttribute;
            final Content srcContent;

            if (monolist == null) {
                logger.warn("monolist attribute where to copy values into not found");
                return;
            }

            try {
                srcContent = getContentSimple(referencedContentId, true);
            } catch (EntException e) {
                throw new RuntimeException(e);
            }

            if (srcContent == null) {
                logger.warn("cannot proceed, content {} not found ", referencedContentId);
                return;
            }

            // get new element
            AttributeInterface newMonolistItem = monolist.addAttribute();

            if (newMonolistItem instanceof CompositeAttribute) {
//                ((CompositeAttribute)newMonolistItem)
//                        .getAttributes()
//                        .forEach(compattr -> System.out.println("~~~ " + compattr.getName()));
                CompositeAttribute compositeAttribute = (CompositeAttribute) newMonolistItem;

                // cycle the attributes as defined in the mapping
                config.getMapping().forEach((dstAttributeName, srcAttributeName) -> {
                    final AttributeInterface dstAttribute = compositeAttribute.getAttribute(dstAttributeName);
                    final AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);

                    if (srcAttribute != null
                            && dstAttribute != null
                            && srcAttribute.getType().equals(dstAttribute.getType())) {

                        if (srcAttribute instanceof ImageAttribute) {
                            copyImageAttribute(dstAttribute, srcAttribute);
                        } else if (srcAttribute instanceof EnumeratorAttribute) {
                            copyEnumeratorAttribute(dstAttribute, srcAttribute);
                        } else if (srcAttribute instanceof TextAttribute) {
                            copyTextAttribute(dstAttribute, srcAttribute);
                        } else if (srcAttribute instanceof DateAttribute) {
                            copyDateAttribute(dstAttribute, srcAttribute);
                        } else if (srcAttribute instanceof NumberAttribute) {
                            copyNumberAttribute(dstAttribute, srcAttribute);
                        } else if (srcAttribute instanceof  MonoTextAttribute) {
                            copyMonoTextAttribute(dstAttribute, srcAttribute);
                        } else if (srcAttribute instanceof HypertextAttribute) {
                            copyHyperTextAttribute(dstAttribute, srcAttribute);
                        } else {
                            logger.warn("unsupported attribute type for linking '{}'", dstAttribute.getType());
                        }
                    } else {
                        logger.debug("cannot proceed!");

                        if (dstAttribute == null) {
                            logger.warn("destination attribute in composite is null: " + dstAttributeName);
                        }
                        if (srcAttribute == null) {
                            logger.warn("source attribute is noll: " + srcAttributeName);
                        }
                    }
                });
            } else {
                logger.debug("expected composite attribute '{}' in monolist, cannot proceed", dstMonolistAttribute.getName());
            }
        } else {
            if (dstMonolistAttribute == null) {
                logger.debug("cannot proceed, null attribute");
            }
            if (StringUtils.isBlank(referencedContentId)) {
                logger.debug("cannot proceed, null content ID ");
            }
            if (config == null) {
                logger.debug("cannot proceed, null mapping");
            }
        }
    }

    private Content importFromReferencedContentLink(Content dstContent, String srcId, SingleMappingConfig mapping)
            throws EntException {
        if (StringUtils.isNotBlank(srcId) && mapping != null) {
            logger.info("loading content '{}'", srcId);
            Content srcContent = getContentSimple(srcId, true);
            logger.info("loaded content '{}'", srcContent.getId());

            // process attributes
            mapping.getMapping().forEach( (dstAttributeName, srcAttributeName) -> {
                logger.debug("attribute '{}' of content type {}",
                        dstAttributeName, srcContent.getTypeCode());
                // get the attribute to fill
                AttributeInterface dstAttribute = dstContent.getAttribute(dstAttributeName);
                // does it exist?
                if (dstAttribute != null) {
                    if (dstAttribute instanceof ImageAttribute) {
                        processImageAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof EnumeratorAttribute) {
                        processEnumeratorAttribute(srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof MonoTextAttribute) {
                        processMonoTextAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof TextAttribute) {
                        processTextAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof DateAttribute) {
                        processDateAttribute(dstContent, srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof NumberAttribute) {
                        processNumberAttribute(srcContent, dstAttribute, srcAttributeName);
                    } else if (dstAttribute instanceof HypertextAttribute) {
                        processHypertextAttribute(srcContent, dstAttribute, srcAttributeName);
                    } else {
                        // attribute not supported
                        logger.warn("unsupported attribute type for linking '{}'", dstAttribute.getType());
                    }
                } else {
                    logger.debug("attribute '{}' does not exist for content type '{}'",
                            dstAttributeName, dstContent.getTypeCode());
                }
            });
        } else if (mapping == null) {
            logger.info("no config found for content '{}' ", dstContent.getId());
        } else {
            logger.warn("no content referenced through attribute '{}' of content '{}' ",
                    mapping.getLinkingAttribute(),
                    dstContent.getId());
        }
        return dstContent;
    }

    private void processEnumeratorAttribute(Content srcContent, AttributeInterface dstAttribute, String srcAttributeName) {
        logger.debug("copying ENUMERATOR from attribute '{}' to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            copyEnumeratorAttribute(dstAttribute, srcAttribute);
        } else {
            logger.debug("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private void processTextAttribute(Content dstContent, Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying TEXT from attribute '{}' to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            copyTextAttribute(dstAttribute, srcAttribute);
        } else {
            logger.debug("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private void processHypertextAttribute(Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying HYPERTEXT from attribute '{}' to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            copyHyperTextAttribute(dstAttribute, srcAttribute);
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
            copyMonoTextAttribute(dstAttribute, srcAttribute);
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
            copyImageAttribute(dstAttribute, srcAttribute);
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
            copyDateAttribute(dstAttribute, srcAttribute);
        } else {
            logger.debug("attribute '{}' not present in content type '{}'", srcAttribute, srcContent.getTypeCode());
        }
    }

    private void processNumberAttribute(Content srcContent, AttributeInterface dstAttribute,
            String srcAttributeName) {
        logger.debug("copying NUMBER from '{}' attribute to '{}'", srcAttributeName,
                dstAttribute.getName());
        // get the src attribute to take value from
        AttributeInterface srcAttribute = srcContent.getAttribute(srcAttributeName);
        // does the src attribute exist?
        if (srcAttribute != null) {
            copyNumberAttribute(dstAttribute, srcAttribute);
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
