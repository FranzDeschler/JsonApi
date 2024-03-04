package cloud.codestore.jsonapi.error;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Represents the source of an error.
 * <br/>
 * See <a href="https://jsonapi.org/format/1.1/#error-objects">jsonapi.org</a>
 */
public class ErrorSource {
    private String pointer;
    private String parameter;
    private String header;

    /**
     * @return a JSON Pointer [<a href="https://www.rfc-editor.org/rfc/rfc6901">RFC6901</a>] to the associated entity in the request document which caused the error.
     * May be {@code null}.
     */
    @JsonGetter("pointer")
    public String getPointer() {
        return pointer;
    }

    /**
     * @param pointer a JSON Pointer [<a href="https://www.rfc-editor.org/rfc/rfc6901">RFC6901</a>] to the associated entity in the request document which caused the error.
     * @return this object.
     */
    @JsonSetter("pointer")
    public ErrorSource setPointer(String pointer) {
        this.pointer = pointer;
        return this;
    }

    /**
     * @return a string indicating which URI query parameter caused the error.
     */
    @JsonGetter("parameter")
    public String getParameter() {
        return parameter;
    }

    /**
     * @param parameter a string indicating which URI query parameter caused the error.
     * @return this object.
     */
    @JsonSetter("parameter")
    public ErrorSource setParameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    /**
     * @return the name of a single request header which caused the error.
     */
    @JsonGetter("header")
    public String getHeader() {
        return header;
    }

    /**
     * @param header the name of a single request header which caused the error.
     * @return this object.
     */
    @JsonSetter("header")
    public ErrorSource setHeader(String header) {
        this.header = header;
        return this;
    }
}
