/*
 * Copyright 2023-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.component;

import java.util.List;
import java.util.Map;

public interface IComponentService {
    
    public static final String REFERENCE_TYPE_PROPERTY = "type";
    public static final String REFERENCE_CODE_PROPERTY = "code";
    public static final String REFERENCE_ONLINE_PROPERTY = "online";

    public List<ComponentUsageDetails> extractComponentUsageDetails(List<Map<String, String>> components);
    
}
