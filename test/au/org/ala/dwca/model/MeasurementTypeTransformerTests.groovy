package au.org.ala.dwca.model

import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class MeasurementTypeTransformerTests extends Specification {
    Vocabulary<Term> dwc = new Vocabulary<Term>(this.class.getResource("darwin-core-vocabulary.json"))
    Vocabulary<Unit> units = new Vocabulary<Unit>(this.class.getResource("unit-vocabulary.json"), Unit.class)
    MeasurementTypeTransformer transformer = new MeasurementTypeTransformer(dwc, units)

    def testCamelCase1() {
        expect:
        transformer.camelCase("Hello", false) == "hello"
        transformer.camelCase("hello", false) == "hello"
        transformer.camelCase("Hello", true) == "Hello"
        transformer.camelCase("hello", true) == "Hello"
    }

    def testCamelCase2() {
        expect:
        transformer.camelCase("This is a LINE OF text", false) == "thisIsALineOfText"
        transformer.camelCase("this Is A Line oF TeXt", false) == "thisIsALineOfText"
        transformer.camelCase("This is a LINE OF text", true) == "ThisIsALineOfText"
        transformer.camelCase("this Is A Line oF TeXt", true) == "ThisIsALineOfText"
    }

    def testCamelCase3() {
        expect:
        transformer.camelCase("Did you(put) the *cat out?", false) == "didYouPutTheCatOut"
        transformer.camelCase("&did You-put THE [cat] out!", false) == "didYouPutTheCatOut"
        transformer.camelCase("Did you(put) the *cat out?", true) == "DidYouPutTheCatOut"
        transformer.camelCase("&did You-put THE [cat] out!", true) == "DidYouPutTheCatOut"
    }

    def testCamelCase4() {
        expect:
        transformer.camelCase("   Surrounded-by-spaces   ", false) == "surroundedBySpaces"
        transformer.camelCase("   [Surrounded_by_spaces]   ", false) == "surroundedBySpaces"
        transformer.camelCase("   Surrounded-by-spaces   ", true) == "SurroundedBySpaces"
        transformer.camelCase("   [Surrounded_by_spaces]   ", true) == "SurroundedBySpaces"
    }

    def testUnitNameAsText1() {
        when:
        Term term = transformer.deriveTerm("something", null)
        then:
        term != null
        term.name == "something"
    }

    def testUnitNameAsText2() {
        when:
        Term term = transformer.deriveTerm("something (m)", null)
        then:
        term != null
        term.name == "somethingInMetre"
    }

    def testUnitNameAsText3() {
        when:
        Term term = transformer.deriveTerm("Dissolved Oxygen (mg/L)", null)
        then:
        term != null
        term.name == "dissolvedOxygenInMilligramsPerLitre"
    }

    def testUnitNameAsText4() {
        when:
        Term term = transformer.deriveTerm("Dissolved Oxygen (% sat)", null)
        then:
        term != null
        term.name == "dissolvedOxygenInPercentageSaturation"
    }

    def testUnitNameAsText5() {
        when:
        Term term = transformer.deriveTerm("Turbidity (NTUs)", null)
        then:
        term != null
        term.name == "turbidityInNtu"
    }

    def testUnitNameAsText6() {
        when:
        Term term = transformer.deriveTerm("Sampler group name *", null)
        then:
        term != null
        term.name == "samplerGroupName"
    }

    def testUnitNameAsText7() {
        when:
        Term term = transformer.deriveTerm("Scientific Name", null)
        then:
        term != null
        term.uri == URI.create('http://rs.tdwg.org/dwc/terms/scientificName')
    }

}