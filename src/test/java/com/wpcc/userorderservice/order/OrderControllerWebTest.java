package com.wpcc.userorderservice.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.wpcc.userorderservice.TestMapperConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestMapperConfiguration.class)
class OrderControllerWebTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void rejectsInvalidCreateOrderRequest() throws Exception {
    mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "userId": null,
              "productId": null,
              "quantity": 0
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("请求参数校验失败"))
        .andExpect(jsonPath("$.fieldErrors.userId").exists())
        .andExpect(jsonPath("$.fieldErrors.productId").exists())
        .andExpect(jsonPath("$.fieldErrors.quantity").exists());
  }
}
