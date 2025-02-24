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
package org.entando.entando.aps.system.services.category;

import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.CategoryUtilizer;
import com.agiletec.aps.system.services.category.ICategoryManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.DtoBuilder;
import org.entando.entando.aps.system.services.component.IComponentDto;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.category.model.CategoryDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.category.validator.CategoryValidator;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.aps.system.services.component.ComponentUsageEntity;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;

/**
 * @author E.Santoboni
 */
public class CategoryService implements ICategoryService, CategoryServiceUtilizer<CategoryDto> {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());

    private final ICategoryManager categoryManager;
    
    private final List<CategoryUtilizer> categoryUtilizers;
    
    private final List<? extends CategoryServiceUtilizer> categoryServiceUtilizers;
    
    private final CategoryValidator categoryValidator;
    
    @Autowired
    public CategoryService(ICategoryManager categoryManager, CategoryValidator categoryValidator, 
            List<CategoryUtilizer> categoryUtilizers, List<? extends CategoryServiceUtilizer> categoryServiceUtilizers) {
        this.categoryManager = categoryManager;
        this.categoryValidator = categoryValidator;
        this.categoryUtilizers = categoryUtilizers;
        this.categoryServiceUtilizers = categoryServiceUtilizers;
    }
    
    protected IDtoBuilder<Category, CategoryDto> getDtoBuilder() {
        CategoryDtoBuilder builder = new CategoryDtoBuilder();
        builder.setCategoryManager(this.categoryManager);
        return builder;
    }
    
    @Override
    public Integer getComponentUsage(String componentCode) {
        int totalCount = 0;
        try {
            if (null == this.getCategoryUtilizers()) {
                return totalCount;
            }
            for (CategoryUtilizer categoryUtilizer : this.getCategoryUtilizers()) {
                totalCount += categoryUtilizer.getCategoryUtilizers(componentCode).size();
            }
        } catch (EntException e) {
            totalCount = 0;
        }
        return totalCount;
    }

    @Override
    public PagedMetadata<ComponentUsageEntity> getComponentUsageDetails(String componentCode, RestListRequest restListRequest) {
        List<ComponentUsageEntity> components = new ArrayList<>();
        Optional.of(this.categoryServiceUtilizers).ifPresent(list -> {
            for (var utilizer : list) {
                List<IComponentDto> objects = utilizer.getCategoryUtilizer(componentCode);
                List<ComponentUsageEntity> utilizerForService = objects.stream()
                        .map(o -> o.buildUsageEntity()).collect(Collectors.toList());
                components.addAll(utilizerForService);
            }
        });
        List<CategoryDto> categories = this.getCategoryUtilizer(componentCode);
        components.addAll(categories.stream()
                        .map(o -> o.buildUsageEntity()).collect(Collectors.toList()));
        List<ComponentUsageEntity> sublist = restListRequest.getSublist(components);
        PagedMetadata<ComponentUsageEntity> usageEntries = new PagedMetadata<>(restListRequest, components.size());
        usageEntries.setBody(sublist);
        return usageEntries;
    }

    @Override
    public String getObjectType() {
        return ComponentUsageEntity.TYPE_CATEGORY;
    }

    @Override
    public String getManagerName() {
        return ((IManager) this.getCategoryManager()).getName();
    }

    @Override
    public List<CategoryDto> getCategoryUtilizer(String categoryCode) {
        if (null != this.getCategoryManager().getCategory(categoryCode)) {
            return this.getTree(categoryCode);
        }
        return new ArrayList<>();
    }

    public class CategoryDtoBuilder extends DtoBuilder<Category, CategoryDto> {
        private ICategoryManager categoryManager;
        @Override
        protected CategoryDto toDto(Category src) {
            return new CategoryDto(src, this.categoryManager);
        }
        public void setCategoryManager(ICategoryManager categoryManager) {
            this.categoryManager = categoryManager;
        }
    }

    @Override
    public List<CategoryDto> getTree(String parentCode) {
        List<CategoryDto> res = new ArrayList<>();
        Category parent = this.getCategoryManager().getCategory(parentCode);
        if (null == parent) {
            throw new ResourceNotFoundException(CategoryValidator.ERRCODE_PARENT_CATEGORY_NOT_FOUND, ComponentUsageEntity.TYPE_CATEGORY, parentCode);
        }
        Optional.ofNullable(parent.getChildrenCodes()).ifPresent(children -> Arrays.asList(children).forEach(childCode -> {
            Category child = this.getCategoryManager().getCategory(childCode);
            CategoryDto childDto = this.getDtoBuilder().convert(child);
            childDto.setChildren(Arrays.asList(child.getChildrenCodes()));
            res.add(childDto);
        }));
        return res;
    }

    @Override
    public CategoryDto getCategory(String categoryCode) {
        Category category = this.getCategoryManager().getCategory(categoryCode);
        if (null == category) {
            throw new ResourceNotFoundException(CategoryValidator.ERRCODE_CATEGORY_NOT_FOUND, ComponentUsageEntity.TYPE_CATEGORY, categoryCode);
        }
        CategoryDto dto = null;
        try {
            dto = this.getDtoBuilder().convert(category);
            if (null == this.getCategoryUtilizers()) {
                return dto;
            }
            for (CategoryUtilizer categoryUtilizer : this.getCategoryUtilizers()) {
                List references = categoryUtilizer.getCategoryUtilizers(categoryCode);
                dto.getReferences().put(((IManager) categoryUtilizer).getName(), (null != references && !references.isEmpty()));
            }
        } catch (Exception e) {
            logger.error("error extracting category " + categoryCode, e);
            throw new RestServerError("error extracting category " + categoryCode, e);
        }
        return dto;
    }

    @Override
    public Optional<IComponentDto> getComponentDto(String code) {
        return Optional.ofNullable(this.getCategoryManager().getCategory(code))
                .map(c -> this.getDtoBuilder().convert(c));
    }

    @Override
    public boolean exists(String categoryCode) {
        return this.getComponentDto(categoryCode).isPresent();
    }

    @Override
    public PagedMetadata<?> getCategoryReferences(String categoryCode, String managerName, RestListRequest restListRequest) {
        Category group = this.getCategoryManager().getCategory(categoryCode);
        if (null == group) {
            logger.warn("no category found with code {}", categoryCode);
            throw new ResourceNotFoundException(CategoryValidator.ERRCODE_CATEGORY_NOT_FOUND, ComponentUsageEntity.TYPE_CATEGORY, categoryCode);
        }
        CategoryServiceUtilizer<?> utilizer = this.getCategoryServiceUtilizer(managerName);
        if (null == utilizer) {
            logger.warn("no references found for {}", managerName);
            throw new ResourceNotFoundException(CategoryValidator.ERRCODE_CATEGORY_NO_REFERENCES, "reference", managerName);
        }
        List<?> dtoList = utilizer.getCategoryUtilizer(categoryCode);
        List<?> subList = restListRequest.getSublist(dtoList);
        SearcherDaoPaginatedResult<?> pagedResult = new SearcherDaoPaginatedResult<>(dtoList.size(), subList);
        PagedMetadata<Object> pagedMetadata = new PagedMetadata<>(restListRequest, pagedResult);
        pagedMetadata.setBody((List<Object>) subList);
        return pagedMetadata;
    }

    private CategoryServiceUtilizer<?> getCategoryServiceUtilizer(String managerName) {
        List<? extends CategoryServiceUtilizer> beans = this.categoryServiceUtilizers;
        if (null == beans) {
            return null;
        }
        Optional<? extends CategoryServiceUtilizer> defName = beans.stream()
                .filter(service -> service.getManagerName().equals(managerName)).findFirst();
        if (defName.isPresent()) {
            return defName.get();
        }
        return null;
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category parentCategory = this.getCategoryManager().getCategory(categoryDto.getParentCode());
        if (null == parentCategory) {
            throw new ResourceNotFoundException(CategoryValidator.ERRCODE_PARENT_CATEGORY_NOT_FOUND, "parent category", categoryDto.getParentCode());
        }
        return this.checkForExistenceOrThrowValidationConflictException(categoryDto)
                .map(this::saveNewCategory)
                .orElse(categoryDto);
    }

    /**
     *
     * @param categoryDto
     * @return
     */
    private CategoryDto saveNewCategory(CategoryDto categoryDto) {
        try {
            Category categoryToAdd = new Category();
            categoryToAdd.setCode(categoryDto.getCode());
            categoryToAdd.setParentCode(categoryDto.getParentCode());
            categoryToAdd.getTitles().putAll(categoryDto.getTitles());
            this.getCategoryManager().addCategory(categoryToAdd);
            return this.getDtoBuilder().convert(this.getCategoryManager().getCategory(categoryDto.getCode()));
        } catch (Exception e) {
            logger.error("error adding category " + categoryDto.getCode(), e);
            throw new RestServerError("error adding category " + categoryDto.getCode(), e);
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category parentCategory = this.getCategoryManager().getCategory(categoryDto.getParentCode());
        if (null == parentCategory) {
            throw new ResourceNotFoundException(CategoryValidator.ERRCODE_PARENT_CATEGORY_NOT_FOUND, "parent category", categoryDto.getParentCode());
        }
        Category category = this.getCategoryManager().getCategory(categoryDto.getCode());
        if (null == category) {
            throw new ResourceNotFoundException(CategoryValidator.ERRCODE_CATEGORY_NOT_FOUND, ComponentUsageEntity.TYPE_CATEGORY, categoryDto.getCode());
        }
        CategoryDto dto = null;
        try {
            category.setParentCode(categoryDto.getParentCode());
            category.getTitles().clear();
            category.getTitles().putAll(categoryDto.getTitles());
            this.getCategoryManager().updateCategory(category);
            dto = this.getDtoBuilder().convert(this.getCategoryManager().getCategory(categoryDto.getCode()));
        } catch (Exception e) {
            logger.error("error updating category " + categoryDto.getCode(), e);
            throw new RestServerError("error updating category " + categoryDto.getCode(), e);
        }
        return dto;
    }

    @Override
    public void deleteCategory(String categoryCode) {
        Category category = this.getCategoryManager().getCategory(categoryCode);
        if (null == category) {
            throw new ResourceNotFoundException("category", categoryCode);
        }
        if (categoryCode.equals(CategoryValidator.ROOT_CATEGORY)) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(category, ComponentUsageEntity.TYPE_CATEGORY);
            bindingResult.reject(CategoryValidator.ERRCODE_ROOT_CATEGORY_CANNOT_BE_DELETED, new String[]{categoryCode}, "category.cannot.delete.root");
            throw new ValidationGenericException(bindingResult);
        }
        if (category.getChildrenCodes().length > 0) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(category, ComponentUsageEntity.TYPE_CATEGORY);
            bindingResult.reject(CategoryValidator.ERRCODE_CATEGORY_HAS_CHILDREN, new String[]{categoryCode}, "category.cannot.delete.children");
            throw new ValidationGenericException(bindingResult);
        }
        try {
            if (null != this.getCategoryUtilizers()) {
                for (CategoryUtilizer categoryUtilizer : this.getCategoryUtilizers()) {
                    List references = categoryUtilizer.getCategoryUtilizers(categoryCode);
                    if (null != references && !references.isEmpty()) {
                        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(category, ComponentUsageEntity.TYPE_CATEGORY);
                        bindingResult.reject(CategoryValidator.ERRCODE_CATEGORY_REFERENCES, new String[]{categoryCode}, "category.cannot.delete.references");
                        throw new ValidationGenericException(bindingResult);
                    }
                }
            }
            this.getCategoryManager().deleteCategory(categoryCode);
        } catch (ValidationGenericException e) {
            throw e;
        } catch (Exception e) {
            logger.error("error deleting category " + categoryCode, e);
            throw new RestServerError("error deleting category " + categoryCode, e);
        }
    }

    protected ICategoryManager getCategoryManager() {
        return categoryManager;
    }

    protected List<CategoryUtilizer> getCategoryUtilizers() {
        return categoryUtilizers;
    }

    public CategoryValidator getCategoryValidator() {
        return categoryValidator;
    }

    /**
     * check if the received Category already exists if it exists with equal name but different description, it throws
     * ValidationConflictException if it exists completely equal, it will return an empty optional that means that the
     * group has NOT to be saved
     *
     * @param categoryDto the category to validate
     * @return the optional of the dto resulting from the validation, if empty the group has NOT to be saved
     */
    protected Optional<CategoryDto> checkForExistenceOrThrowValidationConflictException(CategoryDto categoryDto) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(categoryDto, ComponentUsageEntity.TYPE_CATEGORY);
        Category savedCategory = this.getCategoryManager().getCategory(categoryDto.getCode());
        // check for idempotemcy
        if (null != savedCategory && savedCategory.getCode().equals(categoryDto.getCode())) {
            CategoryDto savedCategoryDto = getDtoBuilder().convert(savedCategory);
            if (categoryValidator.areEquals(categoryDto, savedCategoryDto)) {
                return Optional.empty();
            } else {
                bindingResult
                        .reject(CategoryValidator.ERRCODE_CATEGORY_ALREADY_EXISTS_WITH_CONFLICTS, new String[]{categoryDto.getCode()},
                                "category.exists.conflict");
                throw new ValidationConflictException(bindingResult);
            }
        }
        return Optional.of(categoryDto);
    }
    
}
