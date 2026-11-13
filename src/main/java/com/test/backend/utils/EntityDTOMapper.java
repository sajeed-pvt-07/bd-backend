package com.test.backend.utils;

import org.springframework.beans.BeanUtils;

public class EntityDTOMapper {

    /**
     * Generic method to convert any entity to DTO
     * Works by copying common properties using Spring's BeanUtils
     * @param source The source object (entity)
     * @param targetClass The target class (DTO)
     * @return An instance of targetClass with properties copied from source
     */
    public static <S, T> T toDTO(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert entity to DTO", e);
        }
    }

    /**
     * Generic method to convert any DTO to entity
     * Works by copying common properties using Spring's BeanUtils
     * @param source The source object (DTO)
     * @param targetClass The target class (entity)
     * @return An instance of targetClass with properties copied from source
     */
    public static <S, T> T toEntity(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert DTO to entity", e);
        }
    }

    /**
     * Custom converter interface for complex mappings
     */
    @FunctionalInterface
    public interface Converter<S, T> {
        T convert(S source);
    }

    /**
     * Generic method with custom converter for complex mappings
     * @param source The source object
     * @param converter Custom conversion logic
     * @return The converted object
     */
    public static <S, T> T map(S source, Converter<S, T> converter) {
        if (source == null) {
            return null;
        }
        return converter.convert(source);
    }
}
