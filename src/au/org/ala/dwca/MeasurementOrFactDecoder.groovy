package au.org.ala.dwca

import au.org.ala.dwca.model.MeasurementTypeTransformer
import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary

/**
 * Decode a line that contains a single measurement or fact.
 * <p>
 * This is translated into a new term for the vocabulary
 * and added as a single value into the record.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class MeasurementOrFactDecoder extends Decoder {
    String defaultName
    int keyIndex
    int typeIndex
    int idIndex
    int unitIndex
    int valueIndex
    int remarksIndex
    MeasurementTypeTransformer transformer

    /**
     * Generate a new occurrence decoder
     *
     * @param vocabulary The vocabulary to use
     * @param entry The entry to build the decoder for
     */
    MeasurementOrFactDecoder(Vocabulary vocabulary, Entry entry) {
        super(vocabulary, entry)
        defaultName = entry.entry.name
        transformer = MeasurementTypeTransformer.getStandard(vocabulary)
        buildIndexes()
    }

    protected def buildIndexes() {
        Term measurementType = vocabulary.get(URI.create("http://rs.tdwg.org/dwc/terms/measurementType"))
        Term measurementUnit = vocabulary.get(URI.create("http://rs.tdwg.org/dwc/terms/measurementUnit"))
        Term measurementValue = vocabulary.get(URI.create("http://rs.tdwg.org/dwc/terms/measurementValue"))
        Term measurementId = vocabulary.get(URI.create("http://rs.tdwg.org/dwc/terms/measurementID"))
        Term measurementRemarks = vocabulary.get(URI.create("http://rs.tdwg.org/dwc/terms/measurementRemarks"))

        keyIndex = terms.findIndexOf { term -> key }
        typeIndex = terms.findIndexOf { term -> term == measurementType }
        unitIndex = terms.findIndexOf { term -> term == measurementUnit }
        valueIndex = terms.findIndexOf { term -> term == measurementValue }
        idIndex = terms.findIndexOf { term -> term == measurementId }
        remarksIndex = terms.findIndexOf { term -> term == measurementRemarks }
    }

    /**
     * Decode a record working out the term that applies to the measurement
     * and then converting into a simple record.
     * <p>
     * Value fields are tried in this order:
     * <ol>
     *     <li>measurementValue</li>
     *     <li>measurementRemarks</li>
     *     <li>measurementID</li>
     * </ol>
     *
     * @param line The line of data
     *
     * @return The resulting record
     */
    @Override
    Record decode(String[] line) {
        Record record = new Record()
        def fromLine = { int index ->
            String val = index >= 0 && index < line.length ? line[index] : null
            val = val && val.isEmpty() ? null : val
            val
        }
        String keyValue = fromLine(keyIndex)
        String measurementType = fromLine(typeIndex) ?: defaultName
        String measurementUnit = fromLine(unitIndex)
        String measurementValue = fromLine(valueIndex)
        String measurementId = fromLine(idIndex)
        String measurementRemarks = fromLine(remarksIndex)
        Term term = transformer.deriveTerm(measurementType, measurementUnit)

        if (measurementValue)
            record.values[term] = measurementValue
        else if (measurementRemarks)
            record.values[term] = measurementRemarks
        else if (measurementId)
            record.values[term] = measurementId
        record.key = keyValue
        return record
    }
}
