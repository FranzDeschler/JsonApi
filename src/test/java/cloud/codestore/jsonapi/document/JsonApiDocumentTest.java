package cloud.codestore.jsonapi.document;

import cloud.codestore.jsonapi.DummyMetaInformation;
import cloud.codestore.jsonapi.TestObjectWriter;
import cloud.codestore.jsonapi.meta.MetaInformation;
import cloud.codestore.jsonapi.resource.ResourceObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A JSON:API document")
public class JsonApiDocumentTest
{
    private static final String HREF = "http://localhost:8080";

    @Test
    @DisplayName("does not allow null as resource")
    void resourceNotNull()
    {
        assertThatThrownBy(() -> JsonApiDocument.of((ResourceObject) null))
                  .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("can contain a json api object")
    void includeJsonApiObject()
    {
        String json = TestObjectWriter.write(
                JsonApiDocument.of(new TestResourceObject("test", "1"))
                               .setJsonapiObject(new JsonApiObject())
        );
        assertThat(json).startsWith("""
                {
                  "jsonapi" : {
                    "version" : "1.1"
                  },
                """);
    }

    @Test
    @DisplayName("can contain meta-information")
    void containsMeta()
    {
        String json = TestObjectWriter.write(
                JsonApiDocument.of(new DummyMetaInformation())
        );
        assertThat(json).isEqualTo("""
                {
                  "meta" : {
                    "info" : "dummy meta info"
                  }
                }""");
    }

    @Test
    @DisplayName("can contain a \"self\" link")
    void containsSelfLink()
    {
        String json = TestObjectWriter.write(
                JsonApiDocument.of(new DummyMetaInformation()).setSelfLink(HREF)
        );
        assertThat(json).isEqualTo("""
                {
                  "links" : {
                    "self" : "http://localhost:8080"
                  },
                  "meta" : {
                    "info" : "dummy meta info"
                  }
                }""");
    }

    @Test
    @DisplayName("sets itself as parent of primary data")
    void setsParentOnResource()
    {
        ResourceObject resourceObject = new TestResourceObject("test", "1");
        JsonApiDocument jsonApiDocument = JsonApiDocument.of(resourceObject);
        assertThat(resourceObject.getParent()).isSameAs(jsonApiDocument);
    }

    @Test
    @DisplayName("sets itself as parent of primary data array")
    void setsParentOnResources()
    {
        ResourceObject resourceObject1 = new TestResourceObject("test", "1");
        ResourceObject resourceObject2 = new TestResourceObject("test", "2");
        ResourceObject resourceObject3 = new TestResourceObject("test", "3");

        JsonApiDocument jsonApiDocument = JsonApiDocument.of(new ResourceObject[]{
                resourceObject1,
                resourceObject2,
                resourceObject3
        });

        assertThat(resourceObject1.getParent()).isSameAs(jsonApiDocument);
        assertThat(resourceObject2.getParent()).isSameAs(jsonApiDocument);
        assertThat(resourceObject3.getParent()).isSameAs(jsonApiDocument);
    }

    @Test
    @DisplayName("does not include resources by default")
    void emptyIncludes()
    {
        assertThat(new SingleResourceDocument().getIncludedResources()).isEmpty();
    }

    @Test
    @DisplayName("does not allow including null resource objects")
    void includeNotNull()
    {
        JsonApiDocument document = JsonApiDocument.of(new MetaInformation() {});

        assertThatThrownBy(() -> document.include(null))
                  .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> document.include(new ResourceObject[]{null}))
                  .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("ignores included duplicates")
    void ignoreIncludedDuplicates()
    {
        ResourceObject resourceObject1 = new TestResourceObject("tag", "TagA");
        ResourceObject resourceObject2 = new TestResourceObject("tag", "TagB");
        ResourceObject resourceObject3 = new TestResourceObject("tag", "TagA");
        ResourceObject resourceObject4 = new TestResourceObject("tag", "TagC");
        ResourceObject resourceObject5 = new TestResourceObject("test", "TagA");

        JsonApiDocument document = JsonApiDocument.of(new MetaInformation() {});
        document.include(resourceObject1, resourceObject2, resourceObject3, resourceObject4, resourceObject5);

        assertThat(document.getIncludedResources())
                  .containsExactlyInAnyOrder(resourceObject1, resourceObject2, resourceObject4, resourceObject5);
    }

    public static class TestResourceObject extends ResourceObject
    {
        TestResourceObject(String type, String id)
        {
            super(type, id);
        }
    }
}
