package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary

/**
 * A single record containing terms.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Record {
    /** The key */
    Comparable key
    /** The collection of values */
    Map<Term, Object> values

    /**
     * Construct an empty record
     */
    Record() {
        values = [:]
    }

    /**
     * Get the terms specified by this record.
     *
     * @return The set of terms contained in this record
     */
    Set<Term> getTerms() {
        return values.keySet()
    }

    /**
     * Update this record from another record.
     * <p>
     * New values overwrite old terms and new terms are added.
     *
     * @param record The record to update from
     */
    void updateFrom(Record record) {
        if (!key) key = record.key
        for (entry in record.values)
            values[entry.key] = entry.value
    }
}
