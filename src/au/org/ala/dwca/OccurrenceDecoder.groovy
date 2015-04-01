package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary

/**
 * Decode a line
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class OccurrenceDecoder extends Decoder {
    /**
     * Generate a new occurrence decoder
     *
     * @param vocabulary The vocabulary to use
     * @param entry The entry to build the decoder for
     */
    OccurrenceDecoder(Vocabulary vocabulary, Entry entry) {
        super(vocabulary, entry)
    }

    /**
     * Decode a record by mapping each entry of the line onto a term.
     * <p>
     * If the line is shorter than the list of terms, the term map is truncated.
     * If the line is longer, an exception is raised.
     *
     * @param line The line of data
     *
     * @return The resulting record
     */
    @Override
    Record decode(String[] line) {
        Record record = new Record()

        if (line.length > terms.size())
            throw new IllegalArgumentException("Line ${line} too big for term list")
        for (int i = 0; i < line.length; i++) {
            def value = line[i]

            if (value != null && !value.isEmpty())
                record.values[terms[i]] = value
        }
        record.key = record.values[key]
        return record
    }
}
