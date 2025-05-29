package com.teamtacles.teamtacles_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    // verifica a criação do usuário
    @Test
    @DisplayName("Should Register an User")
    void testRegisterAnUser_ShouldReturnCreated() throws Exception {
        String json ="""
            {
                "userName": "Caio da Silva",
                "email": "Caio.silva@example.com",
                "password": "senhaSegura123",
                "passwordConfirm": "senhaSegura123"
            }
        """;

        mockMvc.perform(post("/api/user/register")
            .contentType("application/json")
			.content(json))
		 	.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().string(not(containsString("senhaSegura123")))); // diferente pq codifica
    }

    // Mudança de Role
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should update user role when admin")
    void testExchangePaperUser_WhenAdmin_ShouldReturn200() throws Exception {
        Long userId = 1L;
        String json = """
            {
                "role": "ADMIN"
            }
        """;

        mockMvc.perform(patch("/api/user/{id_user}/exchangepaper", userId)
            .contentType("application/json")
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roles").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should return paged list of users")
    void testGetAllUsers_WhenAdmin_ShouldReturnPagedUsers() throws Exception {
        mockMvc.perform(get("/api/user")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").exists())
            .andExpect(jsonPath("$.totalPages").exists());
    }
}
