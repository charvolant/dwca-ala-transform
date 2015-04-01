package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary

/**
 * Decode a line from a DwCA file into a record that
 * can be merged into a dataset.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
abstract class Decoder {
    /** The vocabulary to use when decoding */
    Vocabulary vocabulary
    /** The terms corresponding to each entry in the line */
    List<Term> terms
    /** The key term */
    Term key

    /**
     * Construct with a vocabulary, term list and index term
     *
     * @param vocabulary
     * @param entry The entry to constri
     * @param key The key term
     */
    Decoder(Vocabulary vocabulary, Entry entry) {
        this.vocabulary = vocabulary
        this.terms = entry.fields.collect { field -> vocabulary.get(field) }
        this.key = vocabulary.get(entry.id)
    }

    abstract Record decode(String[] line)

    /**
     * Create a decoder for this kind of entry.
     * <p>
     * This attempts to build a decoder based on row type.
     * If not specified, an a row type of http://rs.tdwg.org/dwc/terms/Occurrence is assumed.
     *
     * @param vocabulary The vocabulary to use
     * @param entry The entry to build a decoder for
     *
     * @return The new decoder
     */
    static Decoder forEntry(Vocabulary vocabulary, Entry entry) {
        if (entry.type == URI.create("http://rs.tdwg.org/dwc/terms/Occurrence"))
            return new OccurrenceDecoder(vocabulary, entry)
        if (entry.type == URI.create("http://rs.tdwg.org/dwc/terms/MeasurementOrFact"))
            return new MeasurementOrFactDecoder(vocabulary, entry)
        return new OccurrenceDecoder(vocabulary, entry)
    }
}
