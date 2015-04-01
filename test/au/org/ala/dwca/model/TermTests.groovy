package au.org.ala.dwca.model

import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TermTests extends Specification {
    def testTerm1() {
        when:
        def config = [name: 'term1', 'title': 'A title', 'description': 'I am a description', 'uri': 'http://localhost/test/term1']
        def term = new Term(config)
        then:
        term.name == 'term1'
        term.title == 'A title'
        term.description == 'I am a description'
        term.uri == new URI('http://localhost/test/term1')
    }

    def testTerm2() {
        when:
        def config = [name: 'term1']
        def term = new Term(config)
        then:
        term.name == 'term1'
        term.title == null
        term.description == null
        term.uri == new URI('urn:x-vocabulary:term:term1')
    }

    def testTerm3() {
        when:
        def config = [uri: 'http://localhost/test/term1']
        def term = new Term(config)
        then:
        term.name == 'term1'
        term.title == null
        term.description == null
        term.uri == new URI('http://localhost/test/term1')
    }

    def testTerm4() {
        when:
        def config = [uri: 'http://localhost/test/term1/']
        def term = new Term(config)
        then:
        term.name == 'term1'
        term.title == null
        term.description == null
        term.uri == new URI('http://localhost/test/term1/')
    }

    def testHashCode1() {
        when:
        def term1 = new Term(uri: 'http://localhost/test/term1/', name: 'term-1')
        def term2 = new Term(uri: 'http://localhost/test/term1/', name: 'term:1')
        then:
        !term1.is(term2)
        term1.hashCode() == term2.hashCode()
    }

    def testHashCode2() {
        when:
        def term1 = new Term(uri: 'http://localhost/test/term1/', name: 'term-1')
        def term2 = new Term(uri: 'http://localhost/test/term2/', name: 'term-1')
        then:
        !term1.is(term2)
        term1.hashCode() != term2.hashCode()
    }

    def testEquals1() {
        when:
        def term1 = new Term(uri: 'http://localhost/test/term1/', name: 'term-1')
        def term2 = new Term(uri: 'http://localhost/test/term1/', name: 'term:1')
        then:
        !term1.is(term2)
        term1 == term2
    }

    def testEquals2() {
        when:
        def term1 = new Term(uri: 'http://localhost/test/term1/', name: 'term-1')
        def term2 = new Term(uri: 'http://localhost/test/term2/', name: 'term-1')
        then:
        !term1.is(term2)
        term1 != term2
    }


    def testUpdateFrom1() {
        when:
        def term1 = new Term(uri: 'http://localhost/test/term1/', name: 'term-1')
        def term2 = new Term(uri: 'http://localhost/test/term1/', name: 'term:1', title: 'Title 1', description: 'Description 1')
        term1.updateFrom(term2)
        then:
        term1.name == 'term-1'
        term1.title == 'Title 1'
        term1.description == 'Description 1'
    }

    def testUpdateFrom2() {
        when:
        def term1 = new Term(uri: 'http://localhost/test/term1/', name: 'term-1')
        def term2 = new Term(uri: 'http://localhost/test/term2/', name: 'term:1', title: 'Title 1', description: 'Description 1')
        term1.updateFrom(term2)
        then:
        thrown(IllegalArgumentException)
    }

}
