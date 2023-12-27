package ttexpandapis.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ttexpandapis.dto.ProductRequestDto;
import ttexpandapis.service.impl.ProductService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private ProductService productService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/clear-product-table.sql"));
        }
    }

    @Test
    @WithMockUser(username = "user1")
    void testAddProducts() throws Exception {
        List<Map<String,String>> records = List.of(
                Map.of(
                "productName1","name1",
                "productPrice1","price1"),
                Map.of(
                        "productName2","name2",
                        "productPrice2","price2"));
        ProductRequestDto requestDto = new ProductRequestDto("table1",records);
        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1")
    @Sql(scripts = {
            "classpath:database/create-table-product.sql",
            "classpath:database/add-product.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetItems() {
        List<Map<String, Object>> expectedItems = createTestData();

        ProductService productService = new ProductService(jdbcTemplate);

        List<Map<String, Object>> actualItems = productService.getItems();

        Assertions.assertEquals(expectedItems.size(), actualItems.size());
        for (int i = 0; i < expectedItems.size(); i++) {
            String expectedName = (String) expectedItems.get(i).get("name");
            String actualName = (String) actualItems.get(i).get("name");
            Assertions.assertEquals(expectedName, actualName);
        }
    }


    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Map<String, Object>> createTestData() {
        List<Map<String, Object>> testDataList = new ArrayList<>();

        Map<String, Object> testData1 = new HashMap<>();
        testData1.put("id", "1");
        testData1.put("name", "Product 1");
        testData1.put("price", "10.99");
        testDataList.add(testData1);

        Map<String, Object> testData2 = new HashMap<>();
        testData2.put("id", "2");
        testData2.put("name", "Product 2");
        testData2.put("price", "20.49");
        testDataList.add(testData2);

        return testDataList;
    }
}
