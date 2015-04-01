package au.org.ala.dwca

import au.org.ala.dwca.Record
import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary
import com.csvreader.CsvWriter

/**
 * A dataset consists of a collection of records.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Dataset {
    /** The (extensible) vocabulary associated with the dataset */
    Vocabulary vocabulary
    /** The terms used (in order) */
    List<Term> terms
    /** The records */
    List<Record> records
    /** The index map of keys onto records */
    Map<Object, Record> index

    Dataset(Vocabulary vocabulary) {
        this.vocabulary = vocabulary
        terms = []
        records = []
        index = [:]
    }

    /**
     * Add a record to the dataset.
     * <p>
     * Any terms that are new are added to the term list.
     * This way, additional terms appear at the end of the list
     * <p>
     * If a record with the same key already exists,
     * that record is updated with the new information.
     *
     * @param record The record to add
     *
     * @return The record that contains the additional information
     */
    Record add(Record record) {
        def existing = index[record.key]
        for (term in record.terms) {
            if (!terms.contains(term))
                terms.add(term)
        }
        if (existing) {
            existing.updateFrom(record)
            return existing
        }
        records << record
        index[record.key] = record
        return record
    }

    /**
     * Write the dataset to a writer
     *
     * @param writer The writer
     */
    def write(Writer writer) {
        CsvWriter csv = new CsvWriter(writer, (char)',')

        csv.escapeMode = CsvWriter.ESCAPE_MODE_BACKSLASH
        csv.textQualifier = (char) '"'
        csv.recordDelimiter = (char) '\n'
        csv.forceQualifier = true
        csv.useTextQualifier = true
        csv.writeRecord(terms.collect { term -> term.name } as String[])
        for (record in records) {
            if (record.values.size() < 5)
                System.out.println("Odd record ${record.key}")
            csv.writeRecord(terms.collect { term -> (record.values[term] ?: '').toString() } as String[])
        }
    }
}

