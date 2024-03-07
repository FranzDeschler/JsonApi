package cloud.codestore.jsonapi.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Objects;

/**
 * Represents a JSON:API errors document.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.1/#errors">jsonapi.org</a>
 */
public class ErrorDocument {
    private ErrorObject[] errors;

    /** Used internally for deserialization */
    @JsonCreator
    ErrorDocument() {}

    /**
     * Creates an {@link ErrorDocument} with a single {@link ErrorObject}.
     *
     * @param error an {@link ErrorObject}.
     * @throws NullPointerException if the error object is {@code null}.
     */
    public ErrorDocument(ErrorObject error) {
        Objects.requireNonNull(error);
        errors = new ErrorObject[]{error};
    }

    /**
     * Creates an {@link ErrorDocument} with multiple {@link ErrorObject}s.
     *
     * @param errors an array with one or more {@link ErrorObject}s.
     * @throws IllegalArgumentException if the given array is {@code null} or empty.
     */
    public ErrorDocument(ErrorObject... errors) {
        if (errors == null || errors.length == 0)
            throw new IllegalArgumentException("An error document must contain at least one error object.");

        this.errors = errors;
    }

    /**
     * @return an array which contains the {@link ErrorObject}s of this document.
     * The array is never {@code null}.
     */
    @JsonGetter("errors")
    public ErrorObject[] getErrors() {
        return errors;
    }

    /** Used internally for deserialization */
    @JsonSetter("errors")
    void setErrors(ErrorObject[] errors) {
        this.errors = errors;
    }

    /**
     * Convenient method to serialize this document.
     * Usually used for testing.
     *
     * @return this document as JSON string.
     * @throws JsonProcessingException if this object could not be serialized.
     */
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(this);
    }
}
