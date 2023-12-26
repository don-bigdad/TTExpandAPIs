package ttexpandapis.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public record ProductRequestDto(@NotBlank String table,
                                List<Map<String, String>> records) {
}
