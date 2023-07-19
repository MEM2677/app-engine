package org.entando.entando.plugins.jpcontentlink.aps.system.service.config;

import java.util.Map;

public class SingleMappingConfig {

    public String targetContentType;
    public String linkedContentType;
    public String linkingAttribute;
    public Boolean active;
    public Map<String, String> mapping;

    public SingleMappingConfig() {
    }

    public String getTargetContentType() {
        return this.targetContentType;
    }

    public String getLinkedContentType() {
        return this.linkedContentType;
    }

    public String getLinkingAttribute() {
        return this.linkingAttribute;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Map<String, String> getMapping() {
        return this.mapping;
    }

    public void setTargetContentType(String targetContentType) {
        this.targetContentType = targetContentType;
    }

    public void setLinkedContentType(String linkedContentType) {
        this.linkedContentType = linkedContentType;
    }

    public void setLinkingAttribute(String linkingAttribute) {
        this.linkingAttribute = linkingAttribute;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SingleMappingConfig)) {
            return false;
        }
        final SingleMappingConfig other = (SingleMappingConfig) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$targetContentType = this.getTargetContentType();
        final Object other$targetContentType = other.getTargetContentType();
        if (this$targetContentType == null ? other$targetContentType != null
                : !this$targetContentType.equals(other$targetContentType)) {
            return false;
        }
        final Object this$linkedContentType = this.getLinkedContentType();
        final Object other$linkedContentType = other.getLinkedContentType();
        if (this$linkedContentType == null ? other$linkedContentType != null
                : !this$linkedContentType.equals(other$linkedContentType)) {
            return false;
        }
        final Object this$linkingAttribute = this.getLinkingAttribute();
        final Object other$linkingAttribute = other.getLinkingAttribute();
        if (this$linkingAttribute == null ? other$linkingAttribute != null
                : !this$linkingAttribute.equals(other$linkingAttribute)) {
            return false;
        }
        final Object this$active = this.getActive();
        final Object other$active = other.getActive();
        if (this$active == null ? other$active != null : !this$active.equals(other$active)) {
            return false;
        }
        final Object this$mapping = this.getMapping();
        final Object other$mapping = other.getMapping();
        if (this$mapping == null ? other$mapping != null : !this$mapping.equals(other$mapping)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SingleMappingConfig;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $targetContentType = this.getTargetContentType();
        result = result * PRIME + ($targetContentType == null ? 43 : $targetContentType.hashCode());
        final Object $linkedContentType = this.getLinkedContentType();
        result = result * PRIME + ($linkedContentType == null ? 43 : $linkedContentType.hashCode());
        final Object $linkingAttribute = this.getLinkingAttribute();
        result = result * PRIME + ($linkingAttribute == null ? 43 : $linkingAttribute.hashCode());
        final Object $active = this.getActive();
        result = result * PRIME + ($active == null ? 43 : $active.hashCode());
        final Object $mapping = this.getMapping();
        result = result * PRIME + ($mapping == null ? 43 : $mapping.hashCode());
        return result;
    }

    public String toString() {
        return "SingleMappingConfig(targetContentType=" + this.getTargetContentType() + ", linkedContentType="
                + this.getLinkedContentType() + ", linkingAttribute=" + this.getLinkingAttribute() + ", active="
                + this.getActive() + ", mapping=" + this.getMapping() + ")";
    }
}
