package au.org.ala.dwca.model

import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class UnitTests extends Specification {
    def testUnit1() {
        when:
        def config = [name: 'metre', 'title': 'Metre', 'description': 'SI unit of length', 'uri': 'http://localhost/test/meter']
        def unit = new Unit(config)
        then:
        unit.name == 'metre'
        unit.title == 'Metre'
        unit.description == 'SI unit of length'
        unit.uri == new URI('http://localhost/test/meter')
        unit.descriptive == "metre"
        unit.symbol == null
        unit.aliases == [] as Set
    }

    def testUnit2() {
        when:
        def config = ['uri': 'http://localhost/test/meters-per-second']
        def unit = new Unit(config)
        then:
        unit.name == 'meters-per-second'
        unit.title == null
        unit.description == null
        unit.uri == new URI('http://localhost/test/meters-per-second')
        unit.descriptive == "meters per second"
        unit.symbol == null
        unit.aliases == [] as Set
    }

    def testUnit3() {
        when:
        def config = ['uri': 'http://localhost/test/meters-per-second', 'symbol': 'm/s']
        def unit = new Unit(config)
        then:
        unit.name == 'meters-per-second'
        unit.title == null
        unit.description == null
        unit.uri == new URI('http://localhost/test/meters-per-second')
        unit.descriptive == "meters per second"
        unit.symbol == 'm/s'
        unit.aliases == [ 'm/s' ] as Set
    }

    def testUnit4() {
        when:
        def config = ['uri': 'http://localhost/test/meters-per-second', 'descriptive': 'mps']
        def unit = new Unit(config)
        then:
        unit.name == 'meters-per-second'
        unit.title == null
        unit.description == null
        unit.uri == new URI('http://localhost/test/meters-per-second')
        unit.descriptive == "mps"
        unit.symbol == null
        unit.aliases == [] as Set
    }

}
