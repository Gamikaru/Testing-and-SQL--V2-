import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

public class ApiResponseDtoTest {

    @Test
    public void testJsonSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SomeData data = new SomeData("test");
        ApiResponseDto responseDto = new ApiResponseDto("Success", data);

        String json = objectMapper.writeValueAsString(responseDto);
        ApiResponseDto deserialized = objectMapper.readValue(json, ApiResponseDto.class);

        assertThat(deserialized.getMessage()).isEqualTo("Success");
        assertThat(deserialized.getData()).isInstanceOf(LinkedHashMap.class);
        LinkedHashMap<String, Object> deserializedData = (LinkedHashMap<String, Object>) deserialized.getData();
        assertThat(deserializedData.get("field")).isEqualTo("test");
    }

    private static class SomeData {
        private String field;

        public SomeData() {
        }

        public SomeData(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
