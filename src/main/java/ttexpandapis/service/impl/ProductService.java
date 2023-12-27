package ttexpandapis.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ttexpandapis.dto.ProductRequestDto;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final JdbcTemplate jdbcTemplate;


    @Transactional
    public ResponseEntity<String> saveProducts(ProductRequestDto product) {
        String tableName = product.table();
        for (Map<String,String> element: product.records()) {

            createTable(tableName, element);

            saveProduct(tableName, element);
        }
        return new ResponseEntity<>("Products added successfully", HttpStatus.CREATED);
    }

    public List<Map<String, Object>> getItems() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.queryForList(sql);
    }

    private void saveProduct(String tableName, Map<String, String> columns) {
        StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder valuesQuery = new StringBuilder(" VALUES (");

        for (Map.Entry<String, String> entry : columns.entrySet()) {
            String columnName = entry.getKey();
            Object columnValue = entry.getValue();

            insertQuery.append(columnName).append(",");
            valuesQuery.append("'").append(columnValue).append("',");

        }

        insertQuery.deleteCharAt(insertQuery.length() - 1).append(")");
        valuesQuery.deleteCharAt(valuesQuery.length() - 1).append(")");

        String finalQuery = insertQuery + valuesQuery.toString();

        jdbcTemplate.execute(finalQuery);
    }


    private void createTable(String tableName, Map<String, String> columns) {
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS "
                + tableName + " (id BIGINT AUTO_INCREMENT PRIMARY KEY, ");
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            createTableQuery.append(entry.getKey()).append(" VARCHAR(255), ");
        }

        createTableQuery.delete(createTableQuery.length() - 2, createTableQuery.length());

        createTableQuery.append(")");

        jdbcTemplate.execute(createTableQuery.toString());
    }

}
