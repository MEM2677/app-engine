package org.entando.entando.plugins.jpcontentlink.aps.system.service.config;

import java.util.List;

public class ContentLinkConfig {

    public boolean active;
    public List<SingleMappingConfig> contentTypes;

    public ContentLinkConfig() {
    }

    public boolean isActive() {
        return this.active;
    }

    public List<SingleMappingConfig> getContentTypes() {
        return this.contentTypes;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setContentTypes(List<SingleMappingConfig> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ContentLinkConfig)) {
            return false;
        }
        final ContentLinkConfig other = (ContentLinkConfig) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        if (this.isActive() != other.isActive()) {
            return false;
        }
        final Object this$contentTypes = this.getContentTypes();
        final Object other$contentTypes = other.getContentTypes();
        if (this$contentTypes == null ? other$contentTypes != null : !this$contentTypes.equals(other$contentTypes)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ContentLinkConfig;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isActive() ? 79 : 97);
        final Object $contentTypes = this.getContentTypes();
        result = result * PRIME + ($contentTypes == null ? 43 : $contentTypes.hashCode());
        return result;
    }

    public String toString() {
        return "ContentLinkConfig(active=" + this.isActive() + ", contentTypes=" + this.getContentTypes() + ")";
    }
}
