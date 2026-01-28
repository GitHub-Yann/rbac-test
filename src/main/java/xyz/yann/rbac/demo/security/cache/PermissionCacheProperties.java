package xyz.yann.rbac.demo.security.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "rbac.cache")
public class PermissionCacheProperties {

    private final Local local = new Local();
    private final Remote remote = new Remote();

    public Local getLocal() {
        return local;
    }

    public Remote getRemote() {
        return remote;
    }

    public static class Local {
        private long maxSize = 2000L;
        private Duration ttl = Duration.ofSeconds(30);

        public long getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(long maxSize) {
            this.maxSize = maxSize;
        }

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
    }

    public static class Remote {
        private boolean enabled = false;
        private Duration ttl = Duration.ofMinutes(5);
        private String keyPrefix = "rbac:permissions:";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }
    }
}
