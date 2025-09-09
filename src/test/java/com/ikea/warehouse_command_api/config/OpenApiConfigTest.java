package com.ikea.warehouse_command_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void customOpenAPI_shouldProduceInfoAndServers() {
        OpenApiConfig cfg = new OpenApiConfig();
        // manually set the serverPort field via reflection since @Value isn't processed here
        try {
            java.lang.reflect.Field f = OpenApiConfig.class.getDeclaredField("serverPort");
            f.setAccessible(true);
            f.set(cfg, "8080");
        } catch (Exception e) {
            fail("Failed to set serverPort for test: " + e.getMessage());
        }

        OpenAPI api = cfg.customOpenAPI();
        assertNotNull(api);
        Info info = api.getInfo();
        assertNotNull(info);
        assertEquals("Warehouse Command API", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
        assertNotNull(info.getContact());
        List<Server> servers = api.getServers();
        assertNotNull(servers);
        assertTrue(servers.stream().anyMatch(s -> s.getUrl().contains("http://localhost:8080")));
    }
}
