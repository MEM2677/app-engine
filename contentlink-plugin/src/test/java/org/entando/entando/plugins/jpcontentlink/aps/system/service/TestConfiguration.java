package org.entando.entando.plugins.jpcontentlink.aps.system.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.ArrayList;
import org.apache.commons.collections.map.HashedMap;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.ContentLinkConfig;
import org.entando.entando.plugins.jpcontentlink.aps.system.service.config.SingleMappingConfig;
import org.junit.jupiter.api.Test;

public class TestConfiguration {

    @Test
    public void testConfiguration() throws Throwable {
        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(getConfigurationObject());
        assertNotNull(xml);
    }

    private ContentLinkConfig getConfigurationObject() {
        ContentLinkConfig lk = new ContentLinkConfig();

        SingleMappingConfig sm = new SingleMappingConfig();
        sm.setActive(true);
        sm.setTargetContentType("EVN");
        sm.setLinkingAttribute("title");
        sm.setLinkedContentType("PPL");
        sm.setMapping(new HashedMap());
        sm.getMapping().put("targetTitle", "linkedTitle");
        sm.getMapping().put("targetDate", "linkedDate");
        sm.getMapping().put("targetImg", "linkedImg");

        lk.setContentTypes(new ArrayList<>());
        lk.getContentTypes().add(sm);
        lk.setEnabled(true);

        return lk;
    }

}
