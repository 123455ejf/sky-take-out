package com.sky.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StringRedisTemplate redisTemplate;

    @Test
    void shouldCompleteEmployeeWorkflow() throws Exception {
        ValueOperations<String, String> valueOperations = mockValueOperations();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "username", "admin",
                "password", "123456"
        ));
        MvcResult loginResult = mockMvc.perform(post("/admin/employee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andReturn();

        JsonNode loginData = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("data");
        String token = loginData.get("token").asText();

        String createBody = objectMapper.writeValueAsString(Map.of(
                "username", "zhangsan",
                "name", "张三",
                "phone", "13800138001",
                "sex", "男",
                "idNumber", "110101199001011234"
        ));
        mockMvc.perform(post("/admin/employee")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        MvcResult pageResult = mockMvc.perform(get("/admin/employee/page")
                        .header("token", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("name", "张三"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].username").value("zhangsan"))
                .andReturn();

        JsonNode createdEmployee = objectMapper.readTree(pageResult.getResponse().getContentAsString())
                .path("data").path("records").get(0);
        long employeeId = createdEmployee.path("id").asLong();
        String createdUpdateTime = createdEmployee.path("updateTime").asText();
        Thread.sleep(20);

        String updateBody = objectMapper.writeValueAsString(Map.of(
                "id", employeeId,
                "username", "zhangsan",
                "name", "张三丰",
                "phone", "13900139000",
                "sex", "男",
                "idNumber", "110101199001011234"
        ));
        mockMvc.perform(put("/admin/employee")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(put("/admin/employee/status/0")
                        .header("token", token)
                        .param("id", String.valueOf(employeeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        MvcResult detailResult = mockMvc.perform(get("/admin/employee/" + employeeId)
                        .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("张三丰"))
                .andExpect(jsonPath("$.data.phone").value("13900139000"))
                .andExpect(jsonPath("$.data.status").value(0))
                .andReturn();
        String updatedUpdateTime = objectMapper.readTree(detailResult.getResponse().getContentAsString())
                .path("data").path("updateTime").asText();
        assertNotEquals(createdUpdateTime, updatedUpdateTime);

        mockMvc.perform(post("/admin/employee/logout")
                        .header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @SuppressWarnings("unchecked")
    private ValueOperations<String, String> mockValueOperations() {
        return org.mockito.Mockito.mock(ValueOperations.class);
    }
}
