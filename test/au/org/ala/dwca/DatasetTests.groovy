package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class DatasetTests extends Specification {
    Vocabulary dwc = new Vocabulary(this.class.getResource("model/darwin-core-vocabulary.json"))
    Vocabulary vocabulary = new Vocabulary(dwc)

    def testDataset1() {
        when:
        Dataset dataset = new Dataset(vocabulary)
        then:
        dataset.index.isEmpty()
        dataset.records.isEmpty()
        dataset.terms.isEmpty()
    }

    def testAdd1() {
        when:
        Dataset dataset = new Dataset(vocabulary)
        Term term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/vernacularName'))
        Record record = new Record()
        record.values[term1] = 'Fred'
        record.key = 'Fred'
        dataset.add(record)
        then:
        dataset.index.size() == 1
        dataset.index['Fred'] == record
        dataset.records.size() == 1
        dataset.records[0] == record
        dataset.terms == [term1]
    }

    def testAdd2() {
        when:
        Dataset dataset = new Dataset(vocabulary)
        Term term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/vernacularName'))
        Term term2 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/catalogNumber'))
        Record record = new Record()
        record.values[term1] = 'Fred'
        record.values[term2] = '100'
        record.key = '100'
        dataset.add(record)
        then:
        dataset.index.size() == 1
        dataset.index['100'] == record
        dataset.records.size() == 1
        dataset.records[0] == record
        dataset.terms == [term1, term2]
    }

    def testAdd3() {
        when:
        Dataset dataset = new Dataset(vocabulary)
        Term term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/catalogNumber'))
        Record record1 = new Record()
        Record record2 = new Record()
        record1.values[term1] = 'X'
        record1.key = 'X'
        record2.values[term1] = '100'
        record2.key = '100'
        dataset.add(record1)
        dataset.add(record2)
        then:
        dataset.index.size() == 2
        dataset.index['X'] == record1
        dataset.index['100'] == record2
        dataset.records.size() == 2
        dataset.records[0] == record1
        dataset.records[1] == record2
        dataset.terms == [term1]
    }

    def testAdd4() {
        when:
        Dataset dataset = new Dataset(vocabulary)
        Term term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/catalogNumber'))
        Term term2 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/scientificName'))
        Record record1 = new Record()
        Record record2 = new Record()
        record1.values[term1] = 'X'
        record1.values[term2] = 'Gastropod'
        record1.key = 'X'
        record2.values[term1] = '100'
        record2.values[term2] = 'Gastropod'
        record2.key = '100'
        dataset.add(record1)
        dataset.add(record2)
        then:
        dataset.index.size() == 2
        dataset.index['X'] == record1
        dataset.index['100'] == record2
        dataset.records.size() == 2
        dataset.records[0] == record1
        dataset.records[1] == record2
        dataset.terms == [term1, term2]
    }


    def testWrite1() {
        when:
        Dataset dataset = new Dataset(vocabulary)
        Term term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/catalogNumber'))
        Term term2 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/scientificName'))
        Record record1 = new Record()
        record1.values[term1] = 'X'
        record1.values[term2] = 'Gastropod'
        record1.key = 'X'
        dataset.add(record1)
        StringWriter sw = new StringWriter()
        dataset.write(sw)
        then:
        sw.toString() == '"catalogNumber","scientificName"\n' +
                '"X","Gastropod"\n'
    }

    def testWrite2() {
        when:
        Dataset dataset = new Dataset(vocabulary)
        Term term1 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/catalogNumber'))
        Term term2 = vocabulary.get(new URI('http://rs.tdwg.org/dwc/terms/scientificName'))
        Record record1 = new Record()
        record1.values[term1] = 'X'
        record1.values[term2] = null
        record1.key = 'X'
        dataset.add(record1)
        StringWriter sw = new StringWriter()
        dataset.write(sw)
        then:
        sw.toString() == '"catalogNumber","scientificName"\n' +
                '"X",""\n'
    }

}