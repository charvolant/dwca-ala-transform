package au.org.ala.dwca.model

import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class VocabularyTests extends Specification {
    def testVocabulary1() {
        when:
        def vocabulary = new Vocabulary()
        then:
        vocabulary.terms != null
        vocabulary.terms.isEmpty()
    }

    def testVocabulary2() {
        when:
        def vocabulary = new Vocabulary(new StringReader('{ "terms": [ { "name": "term1" } ] }'))
        then:
        vocabulary.terms != null
        vocabulary.terms.size() == 1
        def term = vocabulary.get(new URI('urn:x-vocabulary:term:term1'))
        term != null
        term.name == 'term1'
    }


    def testVocabulary3() {
        when:
        def vocabulary = new Vocabulary(this.class.getResource("vocab-1.json"))
        then:
        vocabulary.name == 'vocab-1'
        vocabulary.description == 'A test vocabulary'
        vocabulary.terms != null
        vocabulary.terms.size() == 2
        def term1 = vocabulary.get(new URI('urn:x-vocabulary:term:term-1'))
        term1 != null
        term1.name == 'term-1'
        def term2 = vocabulary.get(new URI('urn:x-vocab:term:2'))
        term2 != null
        term2.name == 'term-2'
    }

    // Check the actual DwC vocabulary
    def testVocabulary4() {
        when:
        def vocabulary = new Vocabulary(this.class.getResource("darwin-core-vocabulary.json"))
        then:
        vocabulary.name == 'DwC'
        vocabulary.uri == new URI('http://rs.tdwg.org/dwc/terms/')
        vocabulary.terms != null
        vocabulary.terms.size() == 192
        def term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/associatedMedia'))
        term1 != null
        term1.name == 'associatedMedia'
        term1.title == 'Associated Media'
        def term2 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/vernacularName'))
        term2 != null
        term2.name == 'vernacularName'
        term2.title == 'Vernacular Name'
    }

    def testAdd1() {
        when:
        def vocabulary = new Vocabulary(this.class.getResource("darwin-core-vocabulary.json"))
        def term = new Term(uri: 'http://rs.tdwg.org/dwc/terms/vernacularName')
        def vterm = vocabulary.add(term)
        then:
        vterm.uri == term.uri
        !vterm.is(term)
    }

    def testAdd2() {
        when:
        def vocabulary = new Vocabulary(this.class.getResource("darwin-core-vocabulary.json"))
        def term = new Term(uri: 'http://rs.tdwg.org/dwc/terms/vernacularNameX')
        def vterm = vocabulary.add(term)
        then:
        vterm.uri == term.uri
        vterm.is(term)
    }

    def testAdd3() {
        when:
        def vocabulary = new Vocabulary(this.class.getResource("darwin-core-vocabulary.json"))
        def term = new Term(uri: 'http://rs.tdwg.org/dwc/terms/vernacularNameX')
        def vterm = vocabulary.add(term)
        def update = new Term(uri: 'http://rs.tdwg.org/dwc/terms/vernacularNameX', title: "A title", description: "A description")
        def vterm2 = vocabulary.add(update)
        then:
        vterm.uri == term.uri
        vterm.uri == vterm2.uri
        vterm.is(term)
        !vterm.is(update)
        vterm2.is(vterm)
        vterm.title == "A title"
        vterm.description == "A description"
    }

    def testGet1() {
        when:
        def vocabulary = new Vocabulary(this.class.getResource("darwin-core-vocabulary.json"))
        def term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/vernacularName'))
        def term2 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/vernacularNameX'))
        then:
        term1 != null
        term2 == null
    }

    def testGet2() {
        when:
        def vocabulary1 = new Vocabulary(this.class.getResource("darwin-core-vocabulary.json"))
        def vocabulary2 = new Vocabulary(this.class.getResource("vocab-1.json"))
        vocabulary2.imports << vocabulary1
        def term1 = vocabulary2.get(new URI('http://rs.tdwg.org/dwc/terms/vernacularName'))
        def term2 = vocabulary2.get(new URI('urn:x-vocabulary:term:term-1'))
        then:
        term1 != null
        term2 != null
    }

}