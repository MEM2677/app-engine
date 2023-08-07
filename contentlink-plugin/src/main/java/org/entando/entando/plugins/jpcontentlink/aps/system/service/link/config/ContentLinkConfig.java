package org.entando.entando.plugins.jpcontentlink.aps.system.service.link.config;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ContentLinkConfig {

    private boolean enabled;
    private List<SingleMappingConfig> contentTypes;

}
