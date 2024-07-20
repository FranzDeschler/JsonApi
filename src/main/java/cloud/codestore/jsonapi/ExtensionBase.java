package cloud.codestore.jsonapi;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The base class for all objects that may contain extension members.
 * @param <T> the type of the direct subclass. Used for return values.
 */
public abstract class ExtensionBase<T extends ExtensionBase> {
    private Map<String, Object> extensionMembers = new HashMap<>();

    /**
     * @param extensionMembers the extension members to set on this JSON:API document.
     * @throws NullPointerException     if {@code extensionMembers} is {@code null}.
     * @throws IllegalArgumentException if the name of one or more members is invalid.
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    public T setExtensionMembers(Map<String, Object> extensionMembers) {
        Objects.requireNonNull(extensionMembers);
        this.extensionMembers.clear();
        for (var entry : extensionMembers.entrySet()) {
            setExtensionMember(entry.getKey(), entry.getValue());
        }

        return (T) this;
    }

    /**
     * Adds an extension member to this JSON:API document.
     *
     * @param memberName the full name of the extension member including the extension-namespace. Must not be {@code null}.
     * @param value      the corresponding value of the member.
     * @throws NullPointerException     if {@code memberName} is {@code null}.
     * @throws IllegalArgumentException if {@code memberName} is invalid.
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    public T setExtensionMember(String memberName, Object value) {
        Objects.requireNonNull(memberName);
        if (!memberName.contains(":")) {
            throw new IllegalArgumentException("Invalid extension member '" + memberName + "'. " +
                                               "Extension member names must follow the pattern <namespace>:<name>");
        }

        extensionMembers.put(memberName, value);
        return (T) this;
    }

    /**
     * Returns the value of the given extension member name.
     *
     * @param memberName the full name of the extension member including the extension-namespace. Must not be {@code null}.
     * @return the associated value or {@code null}.
     * @throws NullPointerException if {@code memberName} is {@code null}.
     * @since 1.1
     */
    @JsonIgnore
    public Object getExtensionMember(String memberName) {
        Objects.requireNonNull(memberName);
        return extensionMembers.get(memberName);
    }

    /**
     * Used to serialize extension members.
     */
    @JsonAnyGetter
    protected Map<String, Object> getExtensionMembers() {
        return Collections.unmodifiableMap(extensionMembers);
    }

    /**
     * Adds any top level property as extension member if the key is a valid extension member name.
     *
     * @param key   a valid extension member name.
     * @param value the corresponding value.
     */
    @JsonAnySetter
    private void setDeserializedExtensionMember(String key, Object value) {
        if (!key.startsWith("@") && key.contains(":")) {
            extensionMembers.put(key, value);
        }
    }
}
