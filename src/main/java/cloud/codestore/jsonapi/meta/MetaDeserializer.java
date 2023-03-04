package cloud.codestore.jsonapi.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents an application specific deserializer for deserializing {@link MetaInformation} objects.
 */
public interface MetaDeserializer {
    /**
     * Returns the class of the {@link MetaInformation} object associated with the given JSON pointer.
     * <br/><br/>
     * If the result is {@code null}, the {@link #deserialize} method will be called
     * to parse the meta information object from the corresponding {@link ObjectNode} of the response document.
     * <br/>
     * Otherwise, the meta information object will be deserialized using the returned class.
     *
     * @param pointer a JSON Pointer to the meta information object in the response document.
     * @return the class of the associated meta information object or {@code null}.
     */
    Class<? extends MetaInformation> getClass(String pointer);

    /**
     * Deserializes a {@link MetaInformation} object from the given {@link ObjectNode} of the response document.
     * This method can be used to dynamically parse application specific meta information objects.
     *
     * @param pointer a JSON Pointer to the meta information object in the response document.
     * @param node the {@link ObjectNode} that represents the meta information object.
     * @return the deserialized instance of the {@link MetaInformation} object.
     * @throws Exception if the meta information object could not be deserialized.
     */
    default MetaInformation deserialize(String pointer, ObjectNode node) throws Exception {
        return null;
    }
}
