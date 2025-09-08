package com.ikea.warehouse_command_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import com.ikea.warehouse_command_api.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticleController.class)
//@ActiveProfiles is avoided; instead disable mongo auto-configurations for slice tests
@org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration(enabled = true)
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        exclude = {
                org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class,
                org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class
        }
)
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.profiles.active=",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
        "spring.data.mongodb.repositories.enabled=false",
        "spring.data.mongodb.auditing.enabled=false"
})
class ArticleControllerTest {

    @org.springframework.boot.test.context.TestConfiguration
    static class TestMongoConfig {
        @org.springframework.context.annotation.Bean
        org.springframework.data.mongodb.core.mapping.MongoMappingContext mongoMappingContext() {
            return new org.springframework.data.mongodb.core.mapping.MongoMappingContext();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArticleService articleService;

    @MockitoBean
    private com.ikea.warehouse_command_api.repository.ArticleRepository articleRepository;

    @MockitoBean
    private com.ikea.warehouse_command_api.repository.ProductRepository productRepository;

    @Test
    void insert_shouldReturnCreated() throws Exception {
        ArticleCommandRequest request = new ArticleCommandRequest("1","leg", 5L);
        ArticleResponse response = ArticleResponse.builder().articleId("507f1f77bcf86cd799439011").name("leg").stock(5L).build();

        when(articleService.save(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/commands/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.articleId", is("507f1f77bcf86cd799439011")))
                .andExpect(jsonPath("$.name", is("leg")))
                .andExpect(jsonPath("$.stock", is(5)));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        String id = "507f1f77bcf86cd799439011";
        ArticleCommandRequest request = new ArticleCommandRequest("1","leg", 6L);
        ArticleResponse response = ArticleResponse.builder().articleId(id).name("leg").stock(6L).build();
        when(articleService.update(eq(id), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/commands/articles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId", is(id)))
                .andExpect(jsonPath("$.stock", is(6)));
    }

    @Test
    void decreaseStock_shouldReturnOk() throws Exception {
        String id = "507f1f77bcf86cd799439011";
        ArticleCommandRequest request = new ArticleCommandRequest("1","leg", 2L);

        mockMvc.perform(post("/api/v1/commands/articles/" + id + "/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(articleService).decreaseAmount(eq(id), any());
    }
}
