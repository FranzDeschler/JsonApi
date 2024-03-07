package cloud.codestore.jsonapi;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * JSONAssert wrapper
 */
public class JsonAssertion {
    public static void assertEquals(String expected, Object object) {
        assertEquals(expected, TestObjectWriter.write(object));
    }

    public static void assertEquals(String expected, String actual) {
        try {
            //System.out.println(actual);
            JSONAssert.assertEquals(expected, actual, true);
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
    }
}
