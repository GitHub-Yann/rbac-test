package xyz.yann.rbac.demo.repository.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yann.rbac.demo.domain.RoleGrant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class RoleGrantListConverter implements AttributeConverter<List<RoleGrant>, String> {

    private static final Logger log = LoggerFactory.getLogger(RoleGrantListConverter.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<RoleGrant>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<RoleGrant> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.warn("序列化角色权限失败，返回空数组", e);
            return "[]";
        }
    }

    @Override
    public List<RoleGrant> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, TYPE);
        } catch (IOException e) {
            log.warn("反序列化角色权限失败，返回空列表", e);
            return new ArrayList<>();
        }
    }
}
