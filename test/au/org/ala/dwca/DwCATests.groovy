package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary
import com.csvreader.CsvReader
import spock.lang.Specification

import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ZipFileMaker)
class DwCATests extends Specification {
    def testDwCA1() {
        when:
        ZipFile zf = makeZip(this.class.getResource("dwca1/meta.xml"), this.class.getResource("dwca1/occurrence.txt"))
        DwCA dwca = new DwCA(zf)
        then:
        dwca.core != null
        dwca.core.entry != null
        dwca.core.entry.name == 'occurrence.txt'
        dwca.core.encoding == Charset.forName("UTF-8")
        dwca.extensions != null
        dwca.extensions.isEmpty()
    }

    def testOpenCSV1() {
        when:
        ZipFile zf = makeZip(this.class.getResource("dwca1/meta.xml"), this.class.getResource("dwca1/occurrence.txt"))
        DwCA dwca = new DwCA(zf)
        CsvReader reader = dwca.core.openCsv()
        then:
        reader != null
        reader.readRecord() == true
        def line1 = reader.getValues()
        line1.length == 2
        line1[0] == '100'
        line1[1] == 'Gastropoda'
        reader.readRecord() == false
    }

    def testDecode1() {
        when:
        Vocabulary dwc = new Vocabulary(this.class.getResource("model/darwin-core-vocabulary.json"))
        ZipFile zf = makeZip(this.class.getResource("dwca1/meta.xml"), this.class.getResource("dwca1/occurrence.txt"))
        DwCA dwca = new DwCA(zf)
        Dataset dataset = dwca.createDataset(dwc)
        then:
        dataset != null
        dataset.index.size() == 1
        dataset.records.size() == 1
        dataset.index['100'] != null
        dataset.records[0].key == '100'
        dataset.records[0].values.size() == 2

    }
    def testDecode2() {
        when:
        Vocabulary<Term> dwc = new Vocabulary<Term>(this.class.getResource("model/darwin-core-vocabulary.json"))
        Vocabulary<Term> voc = new Vocabulary<Term>(dwc)
        ZipFile zf = makeZip(this.class.getResource("dwca2/meta.xml"), this.class.getResource("dwca2/occurrence.txt"), this.class.getResource("dwca2/measurement.txt"))
        DwCA dwca = new DwCA(zf)
        Dataset dataset = dwca.createDataset(voc)
        then:
        voc.terms.size() == 14
        dataset != null
        dataset.index.size() == 1
        dataset.records.size() == 1
        dataset.index['233553'] != null
        dataset.records[0].key == '233553'
        dataset.records[0].values.size() == 16
        def term = voc.get("numberInSamplerGroup")
        term != null
        dataset.records[0].values[term] == '1'

    }
}