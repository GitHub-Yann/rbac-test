package xyz.yann.rbac.demo.security.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class PermissionCacheService {

    private static final Logger log = LoggerFactory.getLogger(PermissionCacheService.class);

    private final Cache<Long, PrincipalPermissionView> localCache;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final PermissionCacheProperties properties;

    public PermissionCacheService(@Nullable StringRedisTemplate redisTemplate,
                                  ObjectMapper objectMapper,
                                  PermissionCacheProperties properties) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.localCache = Caffeine.newBuilder()
                .maximumSize(properties.getLocal().getMaxSize())
                .expireAfterWrite(properties.getLocal().getTtl())
                .build();
    }

    public Optional<PrincipalPermissionView> getFromLocal(Long principalId) {
        return Optional.ofNullable(localCache.getIfPresent(principalId));
    }

    public Optional<PrincipalPermissionView> getFromRemote(Long principalId) {
        if (!properties.getRemote().isEnabled() || redisTemplate == null) {
            return Optional.empty();
        }
        String cacheKey = buildRemoteKey(principalId);
        String payload = redisTemplate.opsForValue().get(cacheKey);
        if (!StringUtils.hasText(payload)) {
            return Optional.empty();
        }
        try {
            PrincipalPermissionView view = objectMapper.readValue(payload, PrincipalPermissionView.class);
            return Optional.of(view);
        } catch (JsonProcessingException e) {
            log.warn("failed to deserialize redis cache, key={}", cacheKey, e);
            redisTemplate.delete(cacheKey);
            return Optional.empty();
        }
    }

    public void cache(Long principalId, PrincipalPermissionView view) {
        localCache.put(principalId, view);
        if (properties.getRemote().isEnabled() && redisTemplate != null) {
            try {
                String payload = objectMapper.writeValueAsString(view);
                String cacheKey = buildRemoteKey(principalId);
                redisTemplate.opsForValue().set(cacheKey, payload, properties.getRemote().getTtl());
            } catch (JsonProcessingException e) {
                log.warn("failed to serialize permission view principalId={}", principalId, e);
            }
        }
    }

    public void evict(Long principalId) {
        localCache.invalidate(principalId);
        if (properties.getRemote().isEnabled() && redisTemplate != null) {
            redisTemplate.delete(buildRemoteKey(principalId));
        }
    }

    public void evictAll() {
        localCache.invalidateAll();
        if (properties.getRemote().isEnabled() && redisTemplate != null) {
            String pattern = properties.getRemote().getKeyPrefix() + "*";
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }
    }

    private String buildRemoteKey(Long principalId) {
        return properties.getRemote().getKeyPrefix() + principalId;
    }
}
