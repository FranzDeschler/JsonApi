package cloud.codestore.jsonapi.internal;

import cloud.codestore.jsonapi.relationship.Relationship;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds a list of all deserialized relationships that refer to an included resource object.
 */
public class RelationshipHolder {
    private static final ThreadLocal<List<Relationship>> threadLocal = new ThreadLocal<>();

    /**
     * Resets the list of relationships.
     */
    public static void reset() {
        threadLocal.set(new LinkedList<>());
    }

    /**
     * @return the list of deserialized relationships.
     */
    public static List<Relationship> getRelationships() {
        return threadLocal.get();
    }
}
