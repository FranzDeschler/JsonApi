package cloud.codestore.jsonapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;

public class TestObjectWriter
{
    private static final ObjectMapper INSTANCE = new JsonApiObjectMapper();

    public static String write(Object object)
    {
        try
        {
            return INSTANCE.writeValueAsString(object)
                           .replaceAll("\r", ""); // avoids \r in test strings
        }
        catch(JsonProcessingException e)
        {
            Assertions.fail("JSON creation failed!", e);
        }
        
        return "";
    }
}
