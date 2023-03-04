package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.internal.JsonApiDeserializerModifier;
import cloud.codestore.jsonapi.internal.MetaInformationDeserializer;
import cloud.codestore.jsonapi.internal.ResourceObjectDeserializerModifier;
import cloud.codestore.jsonapi.internal.ResourceObjectSerializerModifier;
import cloud.codestore.jsonapi.meta.MetaDeserializer;
import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Jackson module that needs to be registered to serialize {@code JsonApiDocument}s.
 */
public class JsonApiModule extends SimpleModule {
    public JsonApiModule() {
        this(null);
    }

    public JsonApiModule(MetaDeserializer metaDeserializer) {
        super(JsonApiModule.class.getName());
        this.addDeserializer(MetaInformation.class, new MetaInformationDeserializer(metaDeserializer));
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new ResourceObjectSerializerModifier());
        context.addBeanDeserializerModifier(new ResourceObjectDeserializerModifier());
        context.addBeanDeserializerModifier(new JsonApiDeserializerModifier());
    }
}
