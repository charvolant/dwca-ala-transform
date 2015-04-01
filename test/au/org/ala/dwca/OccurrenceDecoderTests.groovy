package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary
import spock.lang.Specification

import java.util.zip.ZipFile

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ZipFileMaker)
class OccurrenceDecoderTests extends Specification {
    Vocabulary<Term> dwc = new Vocabulary<Term>(this.class.getResource("model/darwin-core-vocabulary.json"))

    def testOccurrenceDecoder1() {
        when:
        def zf = makeZip(this.class.getResource("dwca1/meta.xml"), this.class.getResource("dwca1/occurrence.txt"))
        def dwca = new DwCA(zf)
        def decoder = new OccurrenceDecoder(dwc, dwca.core)
        then:
        decoder.vocabulary == dwc
        decoder.terms != null
        decoder.terms.size() == dwca.core.fields.size()
        decoder.terms[0].uri == dwca.core.fields[0]
        decoder.key != null
        decoder.key.uri == dwca.core.fields[0]
    }


    def testDecode1() {
        when:
        def zf = makeZip(this.class.getResource("dwca1/meta.xml"), this.class.getResource("dwca1/occurrence.txt"))
        def dwca = new DwCA(zf)
        def decoder = new OccurrenceDecoder(dwc, dwca.core)
        def record = decoder.decode(['100', 'Gastropod'] as String[])
        then:
        record != null
        record.key == '100'
        record.terms != null
        record.terms.size() == 2
        record.values != null
        record.values[dwc.get(dwca.core.fields[0])] == '100'
        record.values[dwc.get(dwca.core.fields[1])] == 'Gastropod'
    }
}