package au.org.ala.dwca.model

/**
 * A Darwin Core term.
 * <p>
 * Terms can be either from the standard DwC vocabulary or additional terms.
 * <p>
 * Equality on terms is defined in terms of the term URI.
 * Two terms with the same URI are equal.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Term extends Named {
    /**
     * Construct from a configuration object
     *
     * @param config The configuration object (JSON, XML, Dictionary, etc)
     */
    Term(config) {
        fromConfig(config)
        if (uri == null && name != null)
            uri = new URI("urn:x-vocabulary:term:${name}")
        if (name == null && uri != null) {
            int si = uri.path.endsWith('/') ? uri.path.length() - 2 : uri.path.length() - 1
            int ti = uri.path.lastIndexOf('/', si)
            name = ti < 0 ? uri.path.substring(0, si + 1) : uri.path.substring(ti + 1, si + 1)
        }
        if (uri == null)
            throw new IllegalArgumentException("Term must have a URI")
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Term ${uri} must have a name")
    }

    /**
     * Generate a hashcode.
     *
     * @return A hashcode from the URI.
     */
    @Override
    int hashCode() {
        return uri.hashCode()
    }

    /**
     * Equality test.
     * <p>
     * Two terms are equal if their URIs are equal.
     *
     * @param obj The object to compare for equality.
     *
     * @return True if the object is a term and has the same URI, false otherwise
     */
    @Override
    boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Term))
            return false
        return uri.equals(((Term) obj).uri)
    }
}
