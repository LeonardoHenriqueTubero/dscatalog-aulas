package br.com.leonardo.dscatalog.controllers;

import br.com.leonardo.dscatalog.dto.ProductDTO;
import br.com.leonardo.dscatalog.tests.Factory;
import br.com.leonardo.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private String username, password, bearerToken;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;

        username = "maria@gmail.com";
        password = "123456";

        bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
    }

    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$.totalElements").value(countTotalProducts),
                        MockMvcResultMatchers.jsonPath("$.content").exists(),
                        MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"),
                        MockMvcResultMatchers.jsonPath("$.content[1].name").value("PC Gamer"),
                        MockMvcResultMatchers.jsonPath("$.content[2].name").value("PC Gamer Alfa")
        );
    }

    @Test
    public void updateShouldReturnProductWhenIdExists() throws Exception{
        ProductDTO productDTO = Factory.createProductDTO();

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expetedDescription = productDTO.getDescription();

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/products/{id}", existingId)
                                .header("Authorization", "Bearer " + bearerToken)
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$.id").value(existingId),
                        MockMvcResultMatchers.jsonPath("$.name").value(expectedName),
                        MockMvcResultMatchers.jsonPath("$.description").value(expetedDescription)
                );
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
        ProductDTO productDTO = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                                .header("Authorization", "Bearer " + bearerToken)
                                .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
