package au.org.ala.dwca.model

import groovy.json.JsonSlurper

/**
 * A consistent vocabulary of terms.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class Vocabulary<T extends Term> extends Named {
    /** A list of imported vocabularies */
    List<Vocabulary<T>> imports = []
    /** The map of URIs onto terms */
    Map<URI, T> terms = [:]
    /** The map of URIs onto names */
    Map<String, T> names = [:]

    /**
     * Construct an empty vocabulary
     */
    Vocabulary() {
    }

    /**
     * Construct a vocabulary with some parents
     *
     * @param parents The parent vocabularies
     */
    Vocabulary(Vocabulary<T>... parents) {
        imports = parents as List<Vocabulary>
    }

    /**
     * Construct a vocabulary from a source in a URL.
     *
     * @param url The url
     * @param termType The type of term this vocabulary uses (defaults to {@link Term})
     */
    Vocabulary(URL url, Class<T> termType = Term.class) {
        load(url, termType)
    }

    /**
     * Construct a vocabulary from a source reader.
     *
     * @param reader The source of the vocabulary
     * @param termType The type of term this vocabulary uses (defaults to {@link Term})
     */
    Vocabulary(Reader reader, Class<T> termType = Term.class) {
        load(reader, termType)
    }

    /**
     * Add a new term to the vocabulary
     * <p>
     * If there is already a term in this vocabulary, then the existing term
     * is updated with any new information available.
     * Otherwise the new term is added.
     * <p>
     * The term returned should be used
     *
     * @param term The new term
     *
     * @return The existing term if one is there or the new term
     */
    T add(T term) {
        T existing = terms.get(term.uri)

        if (existing != null) {
            existing.updateFrom(term)
            return existing
        }
        terms[term.uri] = term
        if (term.name != null) {
            if (names.containsKey(term.name))
                throw new IllegalArgumentException("Vocabulary has non-unique name ${term.name}")
            names[term.name] = term
        }
        for (alias in term.aliases) {
            if (names.containsKey(alias))
                throw new IllegalArgumentException("Vocabulary has non-unique alias ${alias}")
            names[alias] = term
        }
        return term
    }

    /**
     * Get a term from the vocabulary by URI.
     * <p>
     * The vocabulary is searched first, followed by imports, in order.
     *
     * @param uri The term URI
     *
     * @return The matching term or null for not found
     */
    T get(URI uri) {
        T term = terms[uri]

        if (term != null)
            return term
        for (v in imports) {
            if ((term = v.get(uri)) != null)
                return term
        }
        return null;
    }

    /**
     * Get a term from the vocabulary by name
     * <p>
     * The vocabulary is searched first, followed by imports, in order.
     *
     * @param name The term name
     *
     * @return The matching term or null for not found
     */
    T get(String name) {
        T term = names[name]

        if (term != null)
            return term
        for (v in imports) {
            if ((term = v.get(name)) != null)
                return term
        }
        return null;
    }

    /**
     * Load a vocabulary from a URL
     *
     * @param url The url
     * @param termType The type of term to load
     */
    def load(URL url, Class<Term> termType) {
        def is = url.openStream()
        try {
            return load(new InputStreamReader(is), termType)
        } finally {
            is.close()
        }
    }

    /**
     * Load a vocabulary from a reader
     *
     * @param reader The reader
     * @param termType The type of term to load
     */
    def load(Reader reader, Class<Term> termType) {
        JsonSlurper slurper = new JsonSlurper()
        def config = slurper.parse(reader)

        fromConfig(config)
        for (t in config.terms) {
            def term = termType.newInstance(t)
            if (terms.containsKey(term.uri))
                throw new IllegalArgumentException("Vocabulary has non-unique term ${term.uri}")
            add(term)
        }
    }
}
