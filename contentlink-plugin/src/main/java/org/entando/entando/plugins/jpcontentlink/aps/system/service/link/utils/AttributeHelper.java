package org.entando.entando.plugins.jpcontentlink.aps.system.service.link.utils;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.EnumeratorAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.HypertextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ImageAttribute;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

public class AttributeHelper {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(AttributeHelper.class);

    public static void copyEnumeratorAttribute(AttributeInterface dstAttribute, AttributeInterface srcAttribute) {
        if (srcAttribute instanceof EnumeratorAttribute) {
            final String[] items = ((EnumeratorAttribute) srcAttribute).getItems();
            final String separator = ((EnumeratorAttribute) srcAttribute).getCustomSeparator();
            final String value = ((EnumeratorAttribute) srcAttribute).getText();

            ((EnumeratorAttribute)dstAttribute).setItems(items);
            ((EnumeratorAttribute)dstAttribute).setCustomSeparator(separator);
            ((EnumeratorAttribute)dstAttribute).setText(value);
            logger.debug("ENUMERATOR copy completed successfully");
        } else {
            logger.error("attribute '{}' is not of the same type of attribute '{}'", srcAttribute,
                    dstAttribute.getName());
        }
    }

    public static void copyTextAttribute(AttributeInterface dstAttribute, AttributeInterface srcAttribute) {
        if (srcAttribute instanceof TextAttribute) {
            // get the text for every language
            HashMap<String, String> testMap = new HashMap<>(((TextAttribute) srcAttribute).getTextMap());
            // update the attribute
            ((TextAttribute) dstAttribute).setTextMap(testMap);
            logger.debug("TEXT copy completed successfully");
        } else {
            logger.error("attribute '{}' is not of the same type of attribute '{}'", srcAttribute,
                    dstAttribute.getName());
        }
    }


    public static void copyHyperTextAttribute(AttributeInterface dstAttribute, AttributeInterface srcAttribute) {
        if (srcAttribute instanceof TextAttribute) {
            // get the text for every language
            HashMap<String, String> testMap = new HashMap<>(((HypertextAttribute) srcAttribute).getTextMap());
            // update the attribute
            ((HypertextAttribute) dstAttribute).setTextMap(testMap);
            logger.debug("HYPERTEXT copy completed successfully");
        } else {
            logger.error("attribute '{}' is not of the same type of attribute '{}'", srcAttribute,
                    dstAttribute.getName());
        }
    }

    public static void copyMonoTextAttribute(AttributeInterface dstAttribute, AttributeInterface srcAttribute) {
        if (srcAttribute instanceof MonoTextAttribute) {
            // get the text for the single language
            String tmpText = ((MonoTextAttribute) srcAttribute).getText();
            // update the attribute
            ((MonoTextAttribute) dstAttribute).setText(tmpText);
            logger.debug("MONOTEXT copy completed successfully");
        } else {
            logger.debug("attribute '{}' is not of the same type of attribute '{}'", srcAttribute,
                    dstAttribute.getName());
        }
    }

    public static void copyImageAttribute(AttributeInterface dstAttribute, AttributeInterface srcAttribute) {
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

            Map<String, String> textMap = ((ImageAttribute) srcAttribute).getTextMap();
            ((ImageAttribute)dstAttribute).setTextMap(textMap);

            logger.debug("IMAGE copy completed successfully");
        } else {
            logger.debug("attribute '{}' is not of the same type of attribute '{}'", srcAttribute,
                    dstAttribute.getName());
        }
    }

    public static void copyDateAttribute(AttributeInterface dstAttribute, AttributeInterface srcAttribute) {
        if (srcAttribute instanceof DateAttribute) {
            Date tmpDate = ((DateAttribute) srcAttribute).getDate();

            ((DateAttribute) dstAttribute).setDate(tmpDate);
            logger.debug("DATE copy completed successfully");
        } else {
            logger.debug("attribute '{}' is not of the same type of attribute '{}'", srcAttribute,
                    dstAttribute.getName());
        }
    }


    public static void copyNumberAttribute(AttributeInterface dstAttribute, AttributeInterface srcAttribute) {
        if (srcAttribute instanceof NumberAttribute) {
            BigDecimal tmpNumber = ((NumberAttribute) srcAttribute).getValue();

            ((NumberAttribute) dstAttribute).setValue(tmpNumber);
            logger.debug("NUMBER copy completed successfully");
        } else {
            logger.debug("attribute '{}' is not of the same type of attribute '{}'", srcAttribute,
                    dstAttribute.getName());
        }
    }

}
