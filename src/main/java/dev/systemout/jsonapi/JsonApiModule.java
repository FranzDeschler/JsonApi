package dev.systemout.jsonapi;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.systemout.jsonapi.internal.*;
import dev.systemout.jsonapi.meta.MetaDeserializer;
import dev.systemout.jsonapi.meta.MetaInformation;

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
        context.addBeanSerializerModifier(new LinkSerializerModifier());
        context.addBeanDeserializerModifier(new ResourceObjectDeserializerModifier());
        context.addBeanDeserializerModifier(new JsonApiDeserializerModifier());
        context.addBeanDeserializerModifier(new RelationshipDeserializerModifier());
    }
}
