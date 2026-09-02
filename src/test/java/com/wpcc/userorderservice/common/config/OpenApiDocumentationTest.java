package com.wpcc.userorderservice.common.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.wpcc.userorderservice.TestMapperConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestMapperConfiguration.class)
class OpenApiDocumentationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void exposesUserApiDocumentation() throws Exception {
    mockMvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.info.title").value("用户订单服务 API"))
        .andExpect(jsonPath("$.info.version").value("v1"))
        .andExpect(jsonPath("$.paths['/api/users']").exists())
        .andExpect(jsonPath("$.paths['/api/users/{id}']").exists());
  }
}
