package au.org.ala.dwca.model

/**
 * A named object.
 * <p>
 * This has a machine-readable name, unique within a context, along with possible
 * human-readable title, description and URI.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Named {
    /** The object URI, if one exists */
    URI uri
    /** The unique name */
    String name
    /** Any aliases for this object */
    Set<String> aliases = []
    /** The human-readable title */
    String title
    /** A description of the object */
    String description

    /**
     * Fill out a named object's fields from a configuration object (JSON, XML etc)
     *
     * @param config The configuration
     */
    def fromConfig(config) {
        uri = config.uri ? new URI(config.uri) : null
        name = config.name
        aliases = config.aliases as Set ?: []
        title = config.title
        description = config.description
    }

    /**
     * Update a named entity with more information.
     * <p>
     * If the title or description are not set, then they are updated
     *
     * @param other The other entity with the same URI
     */
    def updateFrom(Named other) {
        if (!uri.equals(other.uri))
            throw new IllegalArgumentException("Cannot update object with URI ${uri} from object with URI ${other.uri}")
        aliases.addAll(other.aliases)
        if (!title) title = other.title
        if (!description) description = other.description
    }
}
