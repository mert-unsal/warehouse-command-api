package com.ikea.warehouse_command_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikea.warehouse_command_api.data.dto.ArticleAmount;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.request.ProductCommandRequest;
import com.ikea.warehouse_command_api.service.ProductService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
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
class ProductControllerTest {

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
    private ProductService productService;

    // Mock repositories to prevent Mongo infrastructure from being required in web-slice tests
    @MockitoBean
    private com.ikea.warehouse_command_api.repository.ArticleRepository articleRepository;
    @MockitoBean
    private com.ikea.warehouse_command_api.repository.ProductRepository productRepository;

    @Test
    void save_shouldReturnCreated() throws Exception {
        ProductCommandRequest request = new ProductCommandRequest("Chair", List.of(new ArticleAmount("art1", 2L)));
        ProductResponse response = new ProductResponse(new ObjectId().toString(), "Chair", request.containArticles(), null, null, null, null, null);

        when(productService.save(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/commands/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Chair")))
                .andExpect(jsonPath("$.containArticles[0].art_id", is("art1")))
                .andExpect(jsonPath("$.containArticles[0].amount_of", is(2)));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        String id = new ObjectId().toString();
        ProductCommandRequest request = new ProductCommandRequest("Chair 2", List.of(new ArticleAmount("art1", 3L)));
        ProductResponse response = new ProductResponse(id, "Chair 2", request.containArticles(), null, null, null, null, null);
        when(productService.update(eq(id), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/commands/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("Chair 2")))
                .andExpect(jsonPath("$.containArticles[0].amount_of", is(3)));
    }
}
