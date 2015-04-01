package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary
import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ZipFileMaker)
class MeasurementOrFactDecoderTests extends Specification {
    Vocabulary<Term> dwc = new Vocabulary<Term>(this.class.getResource("model/darwin-core-vocabulary.json"))

    def testMeasurementOrFactDecoder1() {
        when:
        def zf = makeZip(this.class.getResource("dwca2/meta.xml"), this.class.getResource("dwca2/occurrence.txt"), this.class.getResource("dwca2/measurement.txt"))
        def dwca = new DwCA(zf)
        def decoder = new MeasurementOrFactDecoder(dwc, dwca.extensions[0])
        then:
        decoder.vocabulary == dwc
        decoder.terms != null
        decoder.terms.size() == dwca.extensions[0].fields.size()
        decoder.terms[0].uri == dwca.extensions[0].fields[0]
        decoder.key != null
        decoder.key.uri == dwca.extensions[0].fields[0]
        decoder.defaultName == "measurement.txt"
        decoder.idIndex == 1
        decoder.keyIndex == 0
        decoder.remarksIndex == 5
        decoder.typeIndex == 2
        decoder.valueIndex == 3
    }

    def testDecode1() {
        when:
        def zf = makeZip(this.class.getResource("dwca2/meta.xml"), this.class.getResource("dwca2/occurrence.txt"), this.class.getResource("dwca2/measurement.txt"))
        def dwca = new DwCA(zf)
        def decoder = new MeasurementOrFactDecoder(dwc, dwca.extensions[0])
        def record = decoder.decode(['233553', '6221', 'Electrical Conductivity (µS/cm)', '3000.000000000000','','survey: Water Quality Data - Hunter (Hunter Region)'] as String[])
        then:
        record.key == '233553'
        record.values.size() == 1
        def term = record.values.keySet().iterator().next()
        term.name == "electricalConductivityInMicroSiemensPerCentimetre"
        def value = record.values.values().iterator().next()
        value == '3000.000000000000'
    }

    def testDecode2() {
        when:
        def zf = makeZip(this.class.getResource("dwca2/meta.xml"), this.class.getResource("dwca2/occurrence.txt"), this.class.getResource("dwca2/measurement.txt"))
        def dwca = new DwCA(zf)
        def decoder = new MeasurementOrFactDecoder(dwc, dwca.extensions[0])
        def record = decoder.decode(['233553', '6221', 'Electrical Conductivity (µS/cm)', '','','survey: Water Quality Data - Hunter (Hunter Region)'] as String[])
        then:
        record.key == '233553'
        record.values.size() == 1
        def term = record.values.keySet().iterator().next()
        term.name == "electricalConductivityInMicroSiemensPerCentimetre"
        def value = record.values.values().iterator().next()
        value == 'survey: Water Quality Data - Hunter (Hunter Region)'
    }

    def testDecode3() {
        when:
        def zf = makeZip(this.class.getResource("dwca2/meta.xml"), this.class.getResource("dwca2/occurrence.txt"), this.class.getResource("dwca2/measurement.txt"))
        def dwca = new DwCA(zf)
        def decoder = new MeasurementOrFactDecoder(dwc, dwca.extensions[0])
        def record = decoder.decode(['233553', '6221', 'Electrical Conductivity (µS/cm)', '','',''] as String[])
        then:
        record.key == '233553'
        record.values.size() == 1
        def term = record.values.keySet().iterator().next()
        term.name == "electricalConductivityInMicroSiemensPerCentimetre"
        def value = record.values.values().iterator().next()
        value == '6221'
    }


    def testDecode4() {
        when:
        def zf = makeZip(this.class.getResource("dwca2/meta.xml"), this.class.getResource("dwca2/occurrence.txt"), this.class.getResource("dwca2/measurement.txt"))
        def dwca = new DwCA(zf)
        def decoder = new MeasurementOrFactDecoder(dwc, dwca.extensions[0])
        def record = decoder.decode(['233553', '6221', 'Rainfall in last 7 days', 'Light','',''] as String[])
        then:
        record.key == '233553'
        record.values.size() == 1
        def term = record.values.keySet().iterator().next()
        term.name == "rainfallInLast7Days"
        def value = record.values.values().iterator().next()
        value == 'Light'
    }

}