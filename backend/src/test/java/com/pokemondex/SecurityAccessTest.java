package com.pokemondex;

import com.pokemondex.domain.model.PokemonDetail;
import com.pokemondex.domain.port.PokemonCatalogPort;
import com.pokemondex.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PokemonCatalogPort catalog;

    @Test
    void seededCatalogIsPublicAndDemoUserCanSyncThenUpdate() throws Exception {
        mockMvc.perform(get("/api/v1/local-pokemon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"demo@pokemon.local","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized());

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"demo@pokemon.local","password":"Demo1234!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();
        String token = com.jayway.jsonpath.JsonPath.read(login.getResponse().getContentAsString(), "$.accessToken");

        PokemonDetail pikachu = TestSupport.pikachu();
        when(catalog.getByNameOrId("pikachu")).thenReturn(pikachu);
        MvcResult synced = mockMvc.perform(post("/api/v1/local-pokemon/sync/pikachu")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("Unassigned"))
                .andReturn();
        String id = com.jayway.jsonpath.JsonPath.read(synced.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(put("/api/v1/local-pokemon/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"localizedName":"Pikachu","region":"Lab","tags":["internal-electric"],"notes":"synced"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("Lab"))
                .andExpect(jsonPath("$.notes").value("synced"));

        mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
