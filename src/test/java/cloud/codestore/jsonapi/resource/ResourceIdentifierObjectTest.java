package cloud.codestore.jsonapi.resource;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("A resource identifier object")
class ResourceIdentifierObjectTest
{
    private static final String TYPE = "snippet";
    private static final String ID = "12345";
    
    @Test
    @DisplayName("must have a type")
    void typeMandatory()
    {
        Assertions.assertThatThrownBy(() -> new ResourceIdentifierObject(null, ID))
                  .isInstanceOf(IllegalArgumentException.class);
    
        Assertions.assertThatThrownBy(() -> new ResourceIdentifierObject("", ID))
                  .isInstanceOf(IllegalArgumentException.class);
    
        Assertions.assertThatThrownBy(() -> new ResourceIdentifierObject(" ", ID))
                  .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("must have an id")
    void idMandatory()
    {
        Assertions.assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, null))
                  .isInstanceOf(IllegalArgumentException.class);
    
        Assertions.assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, ""))
                  .isInstanceOf(IllegalArgumentException.class);
    
        Assertions.assertThatThrownBy(() -> new ResourceIdentifierObject(TYPE, " "))
                  .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("can contain meta-information")
    void containsMeta()
    {
        String json = TestObjectWriter.write(
                new ResourceIdentifierObject(TYPE, ID).setMeta(new DummyMetaInformation())
        );
        Assertions.assertThat(json).isEqualTo("""
                {
                  "type" : "snippet",
                  "id" : "12345",
                  "meta" : {
                    "info" : "dummy meta info"
                  }
                }""");
    }
}