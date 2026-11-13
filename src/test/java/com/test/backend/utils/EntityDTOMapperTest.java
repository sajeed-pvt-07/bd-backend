package com.test.backend.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntityDTOMapperTest {

    // Test DTO with only some fields
    static class PartialDTO {
        private String key;
        private String value;
        private String extraField;

        // Getters and Setters
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getExtraField() { return extraField; }
        public void setExtraField(String extraField) { this.extraField = extraField; }
    }

    // Test Entity with fewer fields
    static class PartialEntity {
        private String key;

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
    }

    @Test
    public void testMissingPropertiesInSource() {
        // Create source with only 'key' property
        PartialEntity source = new PartialEntity();
        source.setKey("testKey");

        // Convert to target which has more properties (key, value, extraField)
        PartialDTO target = EntityDTOMapper.toDTO(source, PartialDTO.class);

        // Verify results
        assertNotNull(target);
        assertEquals("testKey", target.getKey());  // ✅ Property exists in source, copied
        assertNull(target.getValue());              // ✅ Property missing in source, remains null
        assertNull(target.getExtraField());         // ✅ Property missing in source, remains null

        System.out.println("Test Result: YES, it works!");
        System.out.println("- Copied properties: " + target.getKey());
        System.out.println("- Missing properties remain: null");
    }

    @Test
    public void testNullSource() {
        // Test null source handling
        PartialDTO result = EntityDTOMapper.toDTO(null, PartialDTO.class);
        assertNull(result);
    }
}

