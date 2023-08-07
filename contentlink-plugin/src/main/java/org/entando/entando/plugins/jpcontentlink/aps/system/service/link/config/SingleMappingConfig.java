package org.entando.entando.plugins.jpcontentlink.aps.system.service.link.config;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@Data
@ToString
public class SingleMappingConfig {

    private String targetContentType;
    private String linkedContentType;
    private String linkingAttribute;
    private boolean active;
    private Map<String, String> mapping;
    private String targetList;

}
