package au.org.ala.dwca.model

import au.org.ala.dwca.Record

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Transform a measurement type into a spelled out version of the measurement.
 * <p>
 * Units can be either part of the type, contained in brackets, or specified as part of a separate unit
 * term.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class MeasurementTypeTransformer {
    static final UNIT_IN_BRACKETS = Pattern.compile("(.+)\\s*\\(\\s*(.+)\\s*\\)")

    /** The vocabulary to use */
    Vocabulary<Term> vocabulary
    /** The unit vocabulary to use */
    Vocabulary<Unit> unitVocabulary
    /** The already mapped type/unit pairs */
    Map<String, Term> cache = [:]

    MeasurementTypeTransformer(Vocabulary<Term> vocabulary, Vocabulary<Unit> unitVocabulary) {
        this.vocabulary = vocabulary
        this.unitVocabulary = unitVocabulary
    }

    /**
     * Derive a term from a measurement type and optional measurement unit.
     * <p>
     * If the measurement unit is not null, that is used.
     * Otherwise, the unit is searched for in the type, using the {@link #UNIT_IN_BRACKETS} pattern.
     *
     * @param type The measurement type
     * @param unit The unit
     *
     * @return A constructed term
     */
    Term deriveTerm(String type, String unit) {
        String key = "${type}^^${unit}"
        Term term = cache[key]
        String resolvedType, resolvedUnit, name, unitName
        Unit actualUnit = null

        if (term != null)
            return term
        if (unit != null && !unit.isEmpty()) {
            // If we have a supplied unit, use that
            resolvedType = type
            resolvedUnit = unit
        } else {
            // Otherwise, see if we can find the unit encoded in the type
            Matcher matcher = UNIT_IN_BRACKETS.matcher(type)

            if (matcher.matches()) {
                resolvedType = matcher.group(1)
                resolvedUnit = matcher.group(2)
            } else {
                // Otherwise, assume no specific unit (a category term)
                resolvedType = type
                resolvedUnit = null
            }
        }
        if (resolvedUnit) {
            actualUnit = unitVocabulary.get(resolvedUnit)
            resolvedUnit = actualUnit?.descriptive ?: resolvedUnit
        }
        name = camelCase(actualUnit ? "${resolvedType} in ${resolvedUnit}" : resolvedType, false)
        term = vocabulary.get(name)
        if (term == null) {
            term = new Term(name: name, aliases: [key])
            term = vocabulary.add(term)
        }
        cache[key] = term
        return term
    }

    /**
     * Convert a phrase into camel case, suitable for using as a term.
     *
     * @param source The source string
     * @param firstUpper The first alpha character is upper case?
     *
     * @return A camel-cased string with non alphanumeric characters removed
     */
    String camelCase(String source, boolean firstUpper) {
        Reader reader = new StringReader(source)
        Writer writer = new StringWriter(source.length())
        boolean upper = firstUpper
        boolean word = false
        int ch

        while ((ch = reader.read()) >= 0) {
            if (Character.isLetterOrDigit(ch)) {
                if (upper) {
                    ch = Character.toUpperCase(ch)
                    upper = false
                } else
                    ch = Character.toLowerCase(ch)
                word = true
                writer.write(ch)
            } else {
                upper = upper || word
                word = false
            }
        }
        return writer.toString()
    }

    /**
     * Get a transformer suitable for the standard set of units.
     *
     * @param vocabulary The vocabulary to use
     *
     * @return A standard measurement type transformer
     */
    static MeasurementTypeTransformer getStandard(Vocabulary<Term> vocabulary) {
        Vocabulary<Unit> unitVocabulary = new Vocabulary<Unit>(Vocabulary.class.getResource("unit-vocabulary.json"), Unit.class)

        return new MeasurementTypeTransformer(vocabulary, unitVocabulary)
    }
}
