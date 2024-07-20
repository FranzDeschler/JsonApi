package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.link.Link;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Dynamically serializes {@link Link}s.
 * Links, that only contain the URI, are serialized as string.
 * Otherwise, links are serialized as object.
 */
public class LinkSerializerModifier extends BeanSerializerModifier {
    @Override
    @SuppressWarnings("unchecked")
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (isLink(beanDesc.getBeanClass())) {
            return new LinkSerializer((JsonSerializer<Link>) serializer);
        }

        return serializer;
    }

    /**
     * @param type a class type.
     * @return {@code true}, if the given type is a subtype of {@link Link}.
     */
    private boolean isLink(Class<?> type) {
        return Link.class.isAssignableFrom(type);
    }

    /**
     * A custom Jackson serializer to serialize {@link Link}s.
     */
    private static class LinkSerializer extends StdSerializer<Link> {
        private final JsonSerializer<Link> defaultSerializer;

        LinkSerializer(JsonSerializer<Link> defaultSerializer) {
            super(Link.class);
            this.defaultSerializer = defaultSerializer;
        }

        @Override
        public void serialize(Link link, JsonGenerator json, SerializerProvider serializerProvider) throws IOException {
            if (containsOnlyHref(link)) {
                json.writeString(link.getHref());
            } else {
                defaultSerializer.serialize(link, json, serializerProvider);
            }
        }

        private boolean containsOnlyHref(Link link) {
            return link.getMeta() == null &&
                   link.getRelation() == null &&
                   link.getTitle() == null &&
                   link.getDescribedby() == null &&
                   link.getType() == null &&
                   link.getHreflang() == null;
        }
    }
}
