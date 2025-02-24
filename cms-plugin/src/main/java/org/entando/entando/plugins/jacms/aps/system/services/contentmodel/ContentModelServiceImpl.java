/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jacms.aps.system.services.contentmodel;

import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.SmallEntityType;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.dictionary.ContentModelDictionaryProvider;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.model.ContentModelDto;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.model.ContentModelReference;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.model.IEntityModelDictionary;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.component.ComponentUsage;
import org.entando.entando.aps.system.services.component.ComponentUsageEntity;
import org.entando.entando.aps.system.services.DtoBuilder;
import org.entando.entando.aps.system.services.component.IComponentDto;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.plugins.jacms.aps.system.services.content.ContentTypeServiceUtilizer;
import org.entando.entando.plugins.jacms.aps.system.services.security.VelocityNonceInjector;
import org.entando.entando.plugins.jacms.web.contentmodel.model.ContentModelReferenceDTO;
import org.entando.entando.plugins.jacms.web.contentmodel.validator.ContentModelValidator;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;

@Service
public class ContentModelServiceImpl implements ContentModelService, ContentTypeServiceUtilizer<ContentModelDto> {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());
    
    public static final String TYPE_CONTENT_TEMPLATE = ComponentUsageEntity.TYPE_CONTENT_TEMPLATE;
    private static final String CONTENT_MODEL_LABEL = "contentModel";
    
    private static final String MESSAGE_NO_CONTENT_MODEL_FOUND = "no contentModel found with id {}";
    private static final String MESSAGE_ERROR_SAVING_CONTENT_MODEL = "Error saving a content model";

    private final IContentManager contentManager;
    private final IContentModelManager contentModelManager;
    private final ContentModelDictionaryProvider dictionaryProvider;
    private final IDtoBuilder<ContentModel, ContentModelDto> dtoBuilder;

    @Autowired
    public ContentModelServiceImpl(IContentManager contentManager, IContentModelManager contentModelManager,
            ContentModelDictionaryProvider dictionaryProvider) {
        this.contentManager = contentManager;
        this.contentModelManager = contentModelManager;
        this.dictionaryProvider = dictionaryProvider;

        this.dtoBuilder = new DtoBuilder<ContentModel, ContentModelDto>() {

            @Override
            protected ContentModelDto toDto(ContentModel src) {
                ContentModelDto dto = new ContentModelDto();
                dto.setContentShape(src.getContentShape());
                dto.setContentType(src.getContentType());
                dto.setDescr(src.getDescription());
                dto.setId(src.getId());
                dto.setStylesheet(src.getStylesheet());
                return dto;
            }
        };
    }

    @Override
    public PagedMetadata<ContentModelDto> findMany(RestListRequest requestList) {
        List<ContentModel> contentModels = new ContentModelRequestListProcessor(
                requestList, this.contentModelManager.getContentModels())
                .filterAndSort().toList();
        //page
        List<ContentModel> subList = requestList.getSublist(contentModels);
        List<ContentModelDto> dtoSlice = this.dtoBuilder.convert(subList);

        SearcherDaoPaginatedResult<ContentModelDto> paginatedResult = new SearcherDaoPaginatedResult<>(
                contentModels.size(), dtoSlice);
        PagedMetadata<ContentModelDto> pagedMetadata = new PagedMetadata<>(requestList, paginatedResult);
        pagedMetadata.setBody(dtoSlice);
        return pagedMetadata;
    }

    @Override
    public ContentModelDto getContentModel(Long modelId) {
        ContentModel contentModel = this.contentModelManager.getContentModel(modelId);
        if (null == contentModel) {
            logger.warn(MESSAGE_NO_CONTENT_MODEL_FOUND, modelId);
            throw new ResourceNotFoundException(ContentModelValidator.ERRCODE_CONTENTMODEL_NOT_FOUND, CONTENT_MODEL_LABEL,
                    String.valueOf(modelId));
        }
        return this.dtoBuilder.convert(contentModel);
    }

    @Override
    public Optional<ContentModelDto> findById(Long modelId) {
        ContentModelDto dto = null;
        ContentModel contentModel = this.contentModelManager.getContentModel(modelId);
        if (contentModel != null) {
            dto = this.dtoBuilder.convert(contentModel);
        }
        return Optional.ofNullable(dto);
    }
    
    @Override
    public Optional<IComponentDto> getComponentDto(String code) {
        return Optional.ofNullable(this.contentModelManager.getContentModel(Long.valueOf(code)))
                .map(this.dtoBuilder::convert);
    }
    
    @Override
    public boolean exists(Long modelId) {
        return this.contentModelManager.getContentModel(modelId) != null;
    }

    @Override
    public ContentModelDto create(ContentModelDto entity) {
        try {
            ContentModel contentModel = this.createContentModel(entity);
            BeanPropertyBindingResult validationResult = this.validateForAdd(contentModel);
            if (validationResult.hasErrors()) {
                throw new ValidationConflictException(validationResult);
            }
            this.contentModelManager.addContentModel(contentModel);
            return this.dtoBuilder.convert(contentModel);
        } catch (EntException e) {
            logger.error(MESSAGE_ERROR_SAVING_CONTENT_MODEL, e);
            throw new RestServerError(MESSAGE_ERROR_SAVING_CONTENT_MODEL, e);
        }
    }

    @Override
    public ContentModelDto update(ContentModelDto entity) {
        try {
            long modelId = entity.getId();

            ContentModel contentModel = this.contentModelManager.getContentModel(entity.getId());
            if (null == contentModel) {
                logger.warn(MESSAGE_NO_CONTENT_MODEL_FOUND, modelId);
                throw new ResourceNotFoundException(ContentModelValidator.ERRCODE_CONTENTMODEL_NOT_FOUND,
                        CONTENT_MODEL_LABEL, String.valueOf(modelId));
            }

            BeanPropertyBindingResult validationResult = this.validateForUpdate(entity, contentModel);
            if (validationResult.hasErrors()) {
                throw new ValidationConflictException(validationResult);
            }
            this.copyProperties(entity, contentModel);

            this.contentModelManager.updateContentModel(contentModel);
            return this.dtoBuilder.convert(contentModel);
        } catch (EntException e) {
            logger.error(MESSAGE_ERROR_SAVING_CONTENT_MODEL, e);
            throw new RestServerError(MESSAGE_ERROR_SAVING_CONTENT_MODEL, e);
        }
    }

    @Override
    public void delete(Long modelId) {
        try {
            ContentModel contentModel = this.contentModelManager.getContentModel(modelId);
            if (null == contentModel) {
                logger.info(MESSAGE_NO_CONTENT_MODEL_FOUND, modelId);
                return;
            }
            BeanPropertyBindingResult validationResult = this.validateForDelete(contentModel);
            if (validationResult.hasErrors()) {
                throw new ValidationConflictException(validationResult);
            }
            this.contentModelManager.removeContentModel(contentModel);
        } catch (EntException e) {
            logger.error("Error in delete contentModel {}", modelId, e);
            throw new RestServerError("error in delete contentModel", e);
        }
    }

    @Override
    public ComponentUsage getComponentUsage(Long modelId) {
        final List<ContentModelReference> contentModelReferences = contentModelManager
                .getContentModelReferences(modelId, false);
        final long onlineCount = contentModelReferences.stream()
                .filter(ContentModelReference::isOnline).count();
        final long offlineCount = contentModelReferences.stream()
                .filter(f -> !f.isOnline()).count();
        final List<SmallEntityType> defaultContentTemplateUsedList = this.contentManager.getSmallEntityTypes().stream()
                .filter(
                        f -> {
                            final String defaultModel = contentManager.getDefaultModel(f.getCode());
                            return String.valueOf(modelId)
                                    .equals(defaultModel);
                        }
                ).collect(Collectors.toList());
        final List<SmallEntityType> defaultContentListTemplateUsedList = this.contentManager.getSmallEntityTypes()
                .stream()
                .filter(
                        f -> {
                            final String listModel = contentManager.getListModel(f.getCode());
                            return String.valueOf(modelId)
                                    .equals(listModel);
                        }
                ).collect(Collectors.toList());
        int countContentDefaultTemplateReferences = defaultContentTemplateUsedList.size();
        int countContentListDefaultTemplateReferences = defaultContentListTemplateUsedList.size();
        Integer usage = Math.toIntExact(onlineCount + offlineCount + countContentDefaultTemplateReferences
                + countContentListDefaultTemplateReferences);
        ComponentUsage componentUsage = new ComponentUsage();
        componentUsage.setType("contentTemplate");
        componentUsage.setCode(String.valueOf(modelId));
        componentUsage.setStatus("");
        componentUsage.setUsage(usage);
        return componentUsage;
    }
    
    @Override
    public PagedMetadata<ComponentUsageEntity> getComponentUsageDetails(String componentCode, RestListRequest restListRequest) {
        return this.getComponentUsageDetails(Long.parseLong(componentCode), restListRequest);
    }
    
    @Override
    public PagedMetadata<ComponentUsageEntity> getComponentUsageDetails(Long modelId, RestListRequest restListRequest) {
        final List<ContentModelReference> contentModelReferences = contentModelManager
                .getContentModelReferences(modelId, false);
        final List<ComponentUsageEntity> componentUsageDetails = contentModelReferences.stream()
                .map(f -> createToComponentUsageEntity(f.getPageCode(), Optional.of(getStatusString(f.isOnline())),
                        ComponentUsageEntity.TYPE_PAGE, Optional.of(f.isOnline()))).collect(Collectors.toList());

        final List<SmallEntityType> defaultContentTemplateUsedList = this.contentManager.getSmallEntityTypes().stream()
                .filter(
                        f -> {
                            final String defaultModel = contentManager.getDefaultModel(f.getCode());
                            final boolean defaultModelUsed = String.valueOf(modelId)
                                    .equals(defaultModel);
                            final String listModel = contentManager.getListModel(f.getCode());
                            final boolean listModelUsed = String.valueOf(modelId)
                                    .equals(listModel);
                            return (defaultModelUsed || listModelUsed);
                        }
                ).collect(Collectors.toList());

        final List<ComponentUsageEntity> contentTemplateUsageDetails = defaultContentTemplateUsedList.stream()
                .map(f -> createToComponentUsageEntity(f.getCode(), Optional.empty(),
                        ComponentUsageEntity.TYPE_CONTENT_TYPE, Optional.empty())).collect(Collectors.toList());

        componentUsageDetails.addAll(contentTemplateUsageDetails);

        final List<ComponentUsageEntity> contentModelUsageDetails = new ContentModelUsageDetailsRequestListProcessor(
                restListRequest, componentUsageDetails)
                .filterAndSort().toList();

        final List<ComponentUsageEntity> subList = restListRequest.getSublist(contentModelUsageDetails);
        SearcherDaoPaginatedResult<ComponentUsageEntity> paginatedResult = new SearcherDaoPaginatedResult<>(
                contentModelUsageDetails.size(), subList);

        PagedMetadata<ComponentUsageEntity> pagedMetadata = new PagedMetadata<>(restListRequest, paginatedResult);
        pagedMetadata.setBody(subList);
        return pagedMetadata;
    }

    private String getStatusString(boolean online) {
        if (online) {
            return "online";
        }
        return "offline";
    }

    private ComponentUsageEntity createToComponentUsageEntity(String code, Optional<String> status, String type, Optional<Boolean> online) {
        ComponentUsageEntity componentUsage = new ComponentUsageEntity();
        componentUsage.setCode(code);
        status.ifPresent(componentUsage::setStatus);
        componentUsage.setType(type);
        online.ifPresent(b -> componentUsage.getExtraProperties().put(ComponentUsageEntity.ONLINE_PROPERTY, b));
        return componentUsage;
    }

    @Override
    public PagedMetadata<ContentModelReferenceDTO> getContentModelReferences(Long modelId,
            RestListRequest requestList) {
        ContentModel contentModel = this.contentModelManager.getContentModel(modelId);
        if (null == contentModel) {
            logger.debug(MESSAGE_NO_CONTENT_MODEL_FOUND, modelId);
            throw new ResourceNotFoundException(ContentModelValidator.ERRCODE_CONTENTMODEL_NOT_FOUND, CONTENT_MODEL_LABEL,
                    String.valueOf(modelId));
        }

        final List<ContentModelReference> contentModelReferences = new ContentModelReferencesRequestListProcessor(
                requestList, this.contentModelManager
                .getContentModelReferences(modelId, true))
                .filterAndSort().toList();

        final List<ContentModelReference> subList = requestList.getSublist(contentModelReferences);
        final List<ContentModelReferenceDTO> contentModelReferenceDTOS = mapContentModelReferencesToDTOs(subList);
        SearcherDaoPaginatedResult<ContentModelReferenceDTO> paginatedResult = new SearcherDaoPaginatedResult<>(
                contentModelReferences.size(), contentModelReferenceDTOS);
        PagedMetadata<ContentModelReferenceDTO> pagedMetadata = new PagedMetadata<>(requestList, paginatedResult);
        pagedMetadata.setBody(contentModelReferenceDTOS);
        return pagedMetadata;
    }


    private List<ContentModelReferenceDTO> mapContentModelReferencesToDTOs(
            List<ContentModelReference> contentModelReferences) {
        return contentModelReferences.stream().map(this::mapContentModelReferenceToDTO
        ).collect(Collectors.toList());
    }

    private ContentModelReferenceDTO mapContentModelReferenceToDTO(ContentModelReference cmr) {
        ContentModelReferenceDTO contentModelReferenceDTO = new ContentModelReferenceDTO();
        contentModelReferenceDTO.setPageCode(cmr.getPageCode());
        contentModelReferenceDTO.setOnline(cmr.isOnline());
        contentModelReferenceDTO.setWidgetPosition(cmr.getWidgetPosition());
        return contentModelReferenceDTO;
    }

    @Override
    public IEntityModelDictionary getContentModelDictionary(String typeCode) {
        if (StringUtils.isBlank(typeCode)) {
            return this.dictionaryProvider.buildDictionary();
        }
        IApsEntity prototype = this.contentManager.getEntityPrototype(typeCode);
        if (null == prototype) {
            logger.warn(MESSAGE_NO_CONTENT_MODEL_FOUND, typeCode);
            throw new ResourceNotFoundException(ContentModelValidator.ERRCODE_CONTENTMODEL_TYPECODE_NOT_FOUND,
                    "contentType", typeCode);
        }
        return this.dictionaryProvider.buildDictionary(prototype);
    }

    protected ContentModel createContentModel(ContentModelDto src) {
        ContentModel contentModel = new ContentModel();
        this.copyProperties(src, contentModel);
        return contentModel;
    }

    protected void copyProperties(ContentModelDto src, ContentModel dest) {
        dest.setContentShape(VelocityNonceInjector.process(src.getContentShape()));
        dest.setContentType(src.getContentType());
        dest.setDescription(src.getDescr());
        dest.setId(src.getId());
        dest.setStylesheet(src.getStylesheet());
    }

    protected BeanPropertyBindingResult validateForAdd(ContentModel contentModel) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(contentModel, CONTENT_MODEL_LABEL);
        validateIdIsUnique(contentModel, errors);
        validateContentType(contentModel.getContentType(), errors);
        return errors;
    }

    protected BeanPropertyBindingResult validateForDelete(ContentModel contentModel) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(contentModel, CONTENT_MODEL_LABEL);
        List<ContentModelReference> references = this.contentModelManager
                .getContentModelReferences(contentModel.getId(), false);
        if (!references.isEmpty()) {
            errors.reject(ContentModelValidator.ERRCODE_CONTENTMODEL_REFERENCES, null, "contentmodel.page.references");
        }
        final List<SmallEntityType> defaultContentTemplateUsedList = this.contentManager.getSmallEntityTypes().stream()
                .filter(
                        f -> {
                            final String defaultModel = contentManager.getDefaultModel(f.getCode());
                            final boolean defaultModelUsed = String.valueOf(contentModel.getId())
                                    .equals(defaultModel);
                            final String listModel = contentManager.getListModel(f.getCode());
                            final boolean listModelUsed = String.valueOf(contentModel.getId())
                                    .equals(listModel);
                            return (defaultModelUsed || listModelUsed);
                        }
                ).collect(Collectors.toList());
        final String defListString = defaultContentTemplateUsedList.stream()
                .map(SmallEntityType::getCode).collect(Collectors.joining(", "));
        if (StringUtils.isNotEmpty(defListString)) {
            errors.reject(ContentModelValidator.ERRCODE_CONTENTMODEL_METADATA_REFERENCES, defListString.split(""),
                    "contentmodel.defaultMetadata.references");
        }
        return errors;
    }

    protected BeanPropertyBindingResult validateForUpdate(ContentModelDto request, ContentModel contentModel) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(contentModel, CONTENT_MODEL_LABEL);
        this.validateContentType(request.getContentType(), errors);
        return errors;
    }

    protected void validateIdIsUnique(ContentModel contentModel, BeanPropertyBindingResult errors) {
        long modelId = contentModel.getId();

        ContentModel dummyModel = this.contentModelManager.getContentModel(modelId);
        if (dummyModel != null) {
            Object[] args = {String.valueOf(modelId)};
            errors.reject(ContentModelValidator.ERRCODE_CONTENTMODEL_ALREADY_EXISTS, args,
                    "contentmodel.id.already.present");
        }
        SmallEntityType utilizer = this.contentModelManager.getDefaultUtilizer(modelId);
        if (null != utilizer && !utilizer.getCode().equals(contentModel.getContentType())) {
            Object[] args = {String.valueOf(modelId), utilizer.getDescription()};
            errors.reject(ContentModelValidator.ERRCODE_CONTENTMODEL_WRONG_UTILIZER, args,
                    "contentmodel.id.wrongUtilizer");
        }
    }

    protected void validateContentType(String contentType, BeanPropertyBindingResult errors) {
        if (!this.contentManager.getSmallContentTypesMap().containsKey(contentType)) {
            Object[] args = {contentType};
            errors.reject(ContentModelValidator.ERRCODE_CONTENTMODEL_TYPECODE_NOT_FOUND, args,
                    "contentmodel.contentType.notFound");
        }
    }

    @Override
    public String getManagerName() {
        return ((IManager) this.contentModelManager).getName();
    }

    @Override
    public List<ContentModelDto> getContentTypeUtilizer(String contentTypeCode) {
        List<ContentModel> models = this.contentModelManager.getModelsForContentType(contentTypeCode);
        return this.dtoBuilder.convert(models);
    }

    @Override
    public String getObjectType() {
        return TYPE_CONTENT_TEMPLATE;
    }

    @Override
    public Integer getComponentUsage(String componentCode) {
        return this.getComponentUsageDetails(componentCode, new RestListRequest()).getTotalItems();
    }
    
}
