package com.test.backend.service;

import com.test.backend.dto.ConfigDTO;
import com.test.backend.entity.Config;
import com.test.backend.repository.ConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @Mock
    private ConfigRepository configRepository;

    @InjectMocks
    private ConfigService configService;

    @Test
    void createsConfigsInBulk() {
        when(configRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        List<ConfigDTO> values = List.of(
                config("birthday-title", "Happy 25th Birthday!"),
                config("birthday-message", "Cheers to 25 years!")
        );

        List<ConfigDTO> saved = configService.createConfigs(values);

        assertEquals(2, saved.size());
        assertEquals("birthday-title", saved.get(0).getKey());
        assertEquals("Happy 25th Birthday!", saved.get(0).getValue());
        assertEquals("birthday-message", saved.get(1).getKey());
        verify(configRepository).saveAll(anyList());
    }

    @Test
    void updatesAnExistingConfigInsteadOfCreatingADuplicate() {
        Config existing = new Config();
        existing.setId("existing-id");
        existing.setKey("thank-you-heading");
        existing.setValue("Old heading");
        when(configRepository.findFirstByKeyOrderByIdDesc("thank-you-heading"))
                .thenReturn(java.util.Optional.of(existing));
        when(configRepository.save(any(Config.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfigDTO updated = configService.createConfig(config("thank-you-heading", "Thank You"));

        assertEquals("existing-id", existing.getId());
        assertEquals("Thank You", updated.getValue());
        verify(configRepository, never()).saveAll(anyList());
        verify(configRepository).save(existing);
    }

    private ConfigDTO config(String key, String value) {
        ConfigDTO config = new ConfigDTO();
        config.setKey(key);
        config.setValue(value);
        return config;
    }
}
