/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpsolr.aps.system.solr;

import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.util.ApsTenantApplicationUtils;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.IIndexerDAO;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.ISearcherDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpsolr.SolrEnvironmentVariables;

/**
 * Classe factory degli elementi ad uso del SearchEngine.
 *
 * @author E.Santoboni
 */
public class SolrSearchEngineDAOFactory implements ISolrSearchEngineDAOFactory {

    private static final String SOLR_ADDRESS_TENANT_PARAM = "solrAddress";
    private static final String SOLR_CORE_TENANT_PARAM = "solrCore";

    private final ILangManager langManager;
    private final ICategoryManager categoryManager;
    private final ITenantManager tenantManager;

    public SolrSearchEngineDAOFactory(ILangManager langManager, ICategoryManager categoryManager,
            ITenantManager tenantManager) {
        this.langManager = langManager;
        this.categoryManager = categoryManager;
        this.tenantManager = tenantManager;
    }

    private SolrTenantResources primaryResources;
    private final Map<String, SolrTenantResources> tenantResources = new ConcurrentHashMap<>();

    @Override
    public void init() throws Exception {
        this.primaryResources = newSolrDAOResources();
    }

    private SolrTenantResources newSolrDAOResources() {
        return new SolrTenantResources(this.getSolrAddress(), this.getSolrCore(), this.langManager, this.categoryManager);
    }

    @Override
    public void close() throws IOException {
        for (SolrTenantResources resources : this.getAllSolrTenantsResources()) {
            resources.close();
        }
    }

    @Override
    public boolean checkCurrentSubfolder() throws EntException {
        // nothing to do
        return true;
    }

    @Override
    public ISolrIndexerDAO getIndexer() throws EntException {
        return getSolrTenantResources().getIndexerDAO();
    }

    @Override
    public ISolrSearcherDAO getSearcher() throws EntException {
        return getSolrTenantResources().getSearcherDAO();
    }

    @Override
    public ISolrSchemaDAO getSolrSchemaDao() {
        return getSolrTenantResources().getSolrSchemaDAO();
    }

    @Override
    public SolrTenantResources getSolrTenantResources() {
        return ApsTenantApplicationUtils.getTenant()
                .map(tenantCode -> tenantResources
                        .computeIfAbsent(tenantCode, t -> newSolrDAOResources()))
                .orElse(primaryResources);
    }

    @Override
    public List<SolrTenantResources> getAllSolrTenantsResources() {
        List<SolrTenantResources> allTenantResources = new ArrayList<>(tenantResources.values());
        allTenantResources.add(0, primaryResources);
        return allTenantResources;
    }

    @Override
    public IIndexerDAO getIndexer(String subDir) throws EntException {
        return this.getIndexer();
    }

    @Override
    public ISearcherDAO getSearcher(String subDir) throws EntException {
        return this.getSearcher();
    }

    @Override
    public void updateSubDir(String newSubDirectory) throws EntException {
        // nothing to do
    }

    @Override
    public void deleteSubDirectory(String subDirectory) {
        // nothing to do
    }

    public String getSolrAddress() {
        return this.getTenantParameter(SOLR_ADDRESS_TENANT_PARAM, SolrEnvironmentVariables.solrAddress());
    }

    public String getSolrCore() {
        return this.getTenantParameter(SOLR_CORE_TENANT_PARAM, SolrEnvironmentVariables.solrCore());
    }

    private String getTenantParameter(String paramName, String defaultValue) {
        return ApsTenantApplicationUtils.getTenant()
                .flatMap(tenantManager::getConfig)
                .flatMap(tenantConfig -> tenantConfig.getProperty(paramName))
                .orElse(defaultValue);
    }
}
