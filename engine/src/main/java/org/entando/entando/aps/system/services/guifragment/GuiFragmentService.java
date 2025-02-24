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
package org.entando.entando.aps.system.services.guifragment;

import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.guifragment.model.GuiFragmentDto;
import org.entando.entando.aps.system.services.guifragment.model.GuiFragmentDtoSmall;
import org.entando.entando.aps.system.services.security.NonceInjector;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.assembler.PagedMetadataMapper;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.aps.system.services.component.ComponentUsageEntity;
import org.entando.entando.web.guifragment.model.GuiFragmentRequestBody;
import org.entando.entando.web.guifragment.validator.GuiFragmentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.services.component.IComponentDto;

public class GuiFragmentService implements IGuiFragmentService {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());
    
    public static final String TYPE_FRAGMENT = "fragment";

    @Autowired
    private IGuiFragmentManager guiFragmentManager;

    @Autowired
    private IDtoBuilder<GuiFragment, GuiFragmentDto> dtoBuilder;

    @Autowired
    private IDtoBuilder<GuiFragment, GuiFragmentDtoSmall> dtoSmallBuilder;

    @Autowired
    private PagedMetadataMapper pagedMetadataMapper;

    protected IGuiFragmentManager getGuiFragmentManager() {
        return guiFragmentManager;
    }

    public void setGuiFragmentManager(IGuiFragmentManager guiFragmentManager) {
        this.guiFragmentManager = guiFragmentManager;
    }

    protected IDtoBuilder<GuiFragment, GuiFragmentDto> getDtoBuilder() {
        return dtoBuilder;
    }

    public void setDtoBuilder(IDtoBuilder<GuiFragment, GuiFragmentDto> dtoBuilder) {
        this.dtoBuilder = dtoBuilder;
    }

    protected IDtoBuilder<GuiFragment, GuiFragmentDtoSmall> getDtoSmallBuilder() {
        return dtoSmallBuilder;
    }

    public void setDtoSmallBuilder(IDtoBuilder<GuiFragment, GuiFragmentDtoSmall> dtoSmallBuilder) {
        this.dtoSmallBuilder = dtoSmallBuilder;
    }

    @Override
    public PagedMetadata<GuiFragmentDtoSmall> getGuiFragments(RestListRequest restListReq) {
        PagedMetadata<GuiFragmentDtoSmall> pagedMetadata = null;
        try {
            SearcherDaoPaginatedResult<GuiFragment> fragments = this.getGuiFragmentManager().getGuiFragments(restListReq.buildFieldSearchFilters());
            List<GuiFragmentDtoSmall> dtoList = this.getDtoSmallBuilder().convert(fragments.getList());
            pagedMetadata = new PagedMetadata<>(restListReq, dtoList, fragments.getCount());
        } catch (Throwable t) {
            logger.error("error in search fragments", t);
            throw new RestServerError("error in search fragments", t);
        }
        return pagedMetadata;
    }

    @Override
    public GuiFragmentDto getGuiFragment(String code) {
        GuiFragment fragment = null;
        try {
            fragment = this.getGuiFragmentManager().getGuiFragment(code);
        } catch (Exception e) {
            logger.error("Error extracting fragment {}", code, e);
            throw new RestServerError("error extracting fragment", e);
        }
        if (null == fragment) {
            logger.warn("no fragment found with code {}", code);
            throw new ResourceNotFoundException(GuiFragmentValidator.ERRCODE_FRAGMENT_DOES_NOT_EXISTS, TYPE_FRAGMENT, code);
        }
        return this.getDtoBuilder().convert(fragment);
    }
    
    @Override
    public Optional<IComponentDto> getComponentDto(String code) throws EntException {
        return Optional.ofNullable(this.getGuiFragmentManager().getGuiFragment(code))
                .map(f -> this.getDtoBuilder().convert(f));
    }

    @Override
    public boolean exists(String code) throws EntException {
        return this.getGuiFragmentManager().getGuiFragment(code) != null;
    }

    @Override
    public GuiFragmentDto addGuiFragment(GuiFragmentRequestBody guiFragmentRequest) {
        try {
            GuiFragment fragment = this.createGuiFragment(guiFragmentRequest);
            this.getGuiFragmentManager().addGuiFragment(fragment);
            return this.getDtoBuilder().convert(fragment);
        } catch (EntException e) {
            logger.error("Error adding fragment", e);
            throw new RestServerError("error add fragment", e);
        }
    }

    @Override
    public GuiFragmentDto updateGuiFragment(GuiFragmentRequestBody guiFragmentRequest) {
        String code = guiFragmentRequest.getCode();
        try {
            GuiFragment fragment = this.getGuiFragmentManager().getGuiFragment(code);
            if (null == fragment) {
                throw new ResourceNotFoundException(GuiFragmentValidator.ERRCODE_FRAGMENT_DOES_NOT_EXISTS, TYPE_FRAGMENT, code);
            }
            fragment.setGui(NonceInjector.process(guiFragmentRequest.getGuiCode()));
            this.getGuiFragmentManager().updateGuiFragment(fragment);
            return this.getDtoBuilder().convert(fragment);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (EntException e) {
            logger.error("Error updating fragment {}", code, e);
            throw new RestServerError("error in update fragment", e);
        }
    }

    @Override
    public void removeGuiFragment(String guiFragmentCode) {
        try {
            GuiFragment fragment = this.getGuiFragmentManager().getGuiFragment(guiFragmentCode);
            if (null == fragment) {
                return;
            }
            GuiFragmentDto dto = this.getDtoBuilder().convert(fragment);
            BeanPropertyBindingResult validationResult = this.checkFragmentForDelete(fragment, dto);
            if (validationResult.hasErrors()) {
                throw new ValidationGenericException(validationResult);
            }
            this.getGuiFragmentManager().deleteGuiFragment(guiFragmentCode);
        } catch (EntException e) {
            logger.error("Error in delete guiFragmentCode {}", guiFragmentCode, e);
            throw new RestServerError("error in delete guiFragmentCode", e);
        }
    }

    @Override
    public List<String> getPluginCodes() {
        try {
            return this.getGuiFragmentManager().loadGuiFragmentPluginCodes();
        } catch (EntException e) {
            logger.error("Error loading plugin codes", e);
            throw new RestServerError("Error loading plugin codes", e);
        }
    }

    private GuiFragment createGuiFragment(GuiFragmentRequestBody guiFragmentRequest) {
        GuiFragment fragment = new GuiFragment();
        fragment.setCode(guiFragmentRequest.getCode());
        fragment.setGui(NonceInjector.process(guiFragmentRequest.getGuiCode()));
        return fragment;
    }

    protected BeanPropertyBindingResult checkFragmentForDelete(GuiFragment fragment, GuiFragmentDto dto) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(fragment, TYPE_FRAGMENT);
        if (null == fragment) {
            return bindingResult;
        }
        if (!dto.getFragments().isEmpty() || !dto.getPageModels().isEmpty()) {
            List<String> fragments = dto.getFragments().stream().map(GuiFragmentDto.FragmentRef::getCode)
                    .collect(Collectors.toList());
            List<String> pagemodels = dto.getPageModels().stream().map(GuiFragmentDto.PageModelRef::getCode)
                    .collect(Collectors.toList());
            bindingResult.reject(GuiFragmentValidator.ERRCODE_FRAGMENT_REFERENCES,
                    new Object[]{fragment.getCode(), fragments, pagemodels}, "guifragment.cannot.delete.references");
        }
        if (fragment.isLocked()) {
            bindingResult.reject(GuiFragmentValidator.ERRCODE_FRAGMENT_LOCKED, new Object[]{fragment.getCode()}, "guifragment.cannot.delete.locked");
        }
        return bindingResult;
    }

    @Override
    public Integer getComponentUsage(String componentCode) {
        try {
            return this.getGuiFragment(componentCode).getPageModels().size();
        } catch (ResourceNotFoundException e) {
            return 0;
        }
    }

    @Override
    public PagedMetadata<ComponentUsageEntity> getComponentUsageDetails(String componentCode, RestListRequest restListRequest) {

        List<ComponentUsageEntity> componentUsageEntityList = new ArrayList<>();

        GuiFragmentDto fragmentDto = this.getGuiFragment(componentCode);

        if (null != fragmentDto.getWidgetType()) {
            componentUsageEntityList.add(new ComponentUsageEntity(ComponentUsageEntity.TYPE_WIDGET, fragmentDto.getWidgetTypeCode()));
        }

        if (null != fragmentDto.getFragments()) {

            List<ComponentUsageEntity> fragmentList = fragmentDto.getFragments().stream()
                    .map(fragmentRef -> new ComponentUsageEntity(ComponentUsageEntity.TYPE_FRAGMENT, fragmentRef.getCode()))
                    .collect(Collectors.toList());
            componentUsageEntityList.addAll(fragmentList);
        }

        if (null != fragmentDto.getPageModels()) {

            List<ComponentUsageEntity> pageModelList = fragmentDto.getPageModels().stream()
                    .map(pageModelRef -> new ComponentUsageEntity(ComponentUsageEntity.TYPE_PAGE_TEMPLATE, pageModelRef.getCode()))
                    .collect(Collectors.toList());
            componentUsageEntityList.addAll(pageModelList);
        }

        return pagedMetadataMapper.getPagedResult(restListRequest, componentUsageEntityList);
    }

    @Override
    public String getObjectType() {
        return TYPE_FRAGMENT;
    }
    
}
