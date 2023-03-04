package cloud.codestore.jsonapi.link;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LinkTest
{
    private static final String HREF = "http://localhost:8080";
    
    @Nested
    @DisplayName("A JSON:API Link")
    class GeneralLinkTest
    {
        @Test
        @DisplayName("can be a string")
        void asString()
        {
            LinksObject linksObject = new LinksObject().add(new Link(Link.SELF, HREF));
            String jsonString = TestObjectWriter.write(linksObject);
            Assertions.assertThat(jsonString).isEqualTo("""
                    {
                      "self" : "http://localhost:8080"
                    }""");
        }
    
        @Test
        @DisplayName("can be an object with meta information")
        void asObject()
        {
            // given
            Link link = new Link(Link.SELF, HREF, new DummyMetaInformation());
            LinksObject linksObject = new LinksObject().add(link);
        
            // when
            String jsonString = TestObjectWriter.write(linksObject);
        
            // then
            Assertions.assertThat(jsonString).isEqualTo("""
                    {
                      "self" : {
                        "href" : "http://localhost:8080",
                        "meta" : {
                          "info" : "dummy meta info"
                        }
                      }
                    }""");
        }
    
        @Test
        @DisplayName("can contain a custom relation")
        void customRelation()
        {
            LinksObject linksObject = new LinksObject().add(new Link("customRelation", HREF));
            String jsonString = TestObjectWriter.write(linksObject);
            Assertions.assertThat(jsonString).isEqualTo("""
                    {
                      "customRelation" : "http://localhost:8080"
                    }""");
        }
    
        @Test
        @DisplayName("must have a relation")
        void relationMandatory()
        {
            Assertions.assertThatThrownBy(() -> new Link(null, HREF))
                    .isInstanceOf(IllegalArgumentException.class);
            
            Assertions.assertThatThrownBy(() -> new Link("", HREF))
                      .isInstanceOf(IllegalArgumentException.class);
    
            Assertions.assertThatThrownBy(() -> new Link(" ", HREF))
                      .isInstanceOf(IllegalArgumentException.class);
        }
    
        @Test
        @DisplayName("must have a href")
        void hrefMandatory()
        {
            Assertions.assertThatThrownBy(() -> new Link("relation", (String) null))
                      .isInstanceOf(IllegalArgumentException.class);
        
            Assertions.assertThatThrownBy(() -> new Link("relation", ""))
                      .isInstanceOf(IllegalArgumentException.class);
    
            Assertions.assertThatThrownBy(() -> new Link("relation", " "))
                      .isInstanceOf(IllegalArgumentException.class);
        }
    }
}