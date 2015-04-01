package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary
import com.csvreader.CsvReader

import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * A darwin core archive.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class DwCA {
    /** The source archive file */
    ZipFile zip
    /** The core entry */
    Core core
    /** The extensions */
    List<Extension> extensions

    /**
     * Construct from an archive and a vocabulary.
     *
     * @param dwca The archive file
     */
    DwCA(ZipFile zip) {
        this.zip = zip
        extensions = []
        ZipEntry meta = zip.getEntry("meta.xml")
        InputStream ms = zip.getInputStream(meta)
        try {
            loadFromMeta(ms)
        } finally {
            ms.close()
        }
    }

    def loadFromMeta(InputStream is) {
        XmlSlurper slurper = new XmlSlurper()
        def meta = slurper.parse(is)

        core = new Core(this, meta.core)
        extensions = meta.extension.collect { ext -> new Extension(this, ext) }
    }

    /**
     * Create a dataset that corresponds to this archive.
     *
     * @param vocabulary The vocabulary to use
     *
     * @return The constructed dataset
     */
    Dataset createDataset(Vocabulary vocabulary) {
        Dataset dataset = new Dataset(vocabulary)

        // Make sure we have the core terms at the start
        dataset.terms = core.fields.collect { field ->
            def term = vocabulary.get(field)
            if (!term) {
                term = new Term(uri: field)
                vocabulary.add(term)
            }
            term
        }
        core.decode(vocabulary, dataset)
        extensions.each { ext -> ext.decode(vocabulary, dataset) }
        return dataset
    }
}

/**
 * An abstract entry in the DwCA file
 */
abstract class Entry {
    /** The source archive */
    DwCA dwca
    /** The file this corresponds to in the archive */
    ZipEntry entry
    /** The encoding used in the file */
    Charset encoding
    /** The type of entry */
    URI type
    /** The terms in the entry */
    List<URI> fields
    /** The field that acts as a key (possible foreign) */
    URI id
    /** The number of header lines */
    int headers
    /** The field separator character */
    char separator
    /** The line terminating character */
    char terminator
    /** The field enclosing character */
    char quote
    /** The escape character */
    char escape

    Entry(dwca, config) {
        this.dwca = dwca
        entry = dwca.zip.getEntry(config.files.location.text())
        encoding = config.@encoding ? Charset.forName(config.@encoding.text()) : Charset.defaultCharset()
        type = config.@rowType ? new URI(config.@rowType.text()) : null
        fields = config.field?.list()?.collect { field -> new URI(field.@term.text()) }
        headers = config.@ignoreHeaderLines?.toInteger() ?: 0
        separator = decodeSeparator(config.@fieldsTerminatedBy?.text() ?: ',')
        terminator = decodeSeparator(config.@linesTerminatedBy?.text() ?: '\n')
        quote = decodeSeparator(config.@fieldsEnclosedBy?.text() ?: '"')
        escape = 0
    }

    char decodeSeparator(String sep) {
        if (sep == null || sep.isEmpty())
            return (char) 0
        if (sep == '\\n')
            return (char) '\n'
        if (sep == '\\t')
            return (char) '\t'
        if (sep == '\\\\')
            return (char) '\\'
        return sep.charAt(0)
    }

    /**
     * Decode this entry into a dataset.
     *
     * @param vocabulary The vocabulary to use
     * @param dataset The dataset to decode into
     */
    def decode(Vocabulary vocabulary, Dataset dataset) {
        Decoder decoder = Decoder.forEntry(vocabulary, this)
        CsvReader reader = openCsv()
        String[] line

        for (int i = 0; i < headers; i++)
            reader.skipLine()
        while (reader.readRecord()) {
            line = reader.getValues()
            dataset.add(decoder.decode(line))
        }
    }

    CsvReader openCsv() {
        def reader = new CsvReader(dwca.zip.getInputStream(entry), separator, encoding)
        reader.escapeMode = CsvReader.ESCAPE_MODE_DOUBLED
        reader.textQualifier = quote
        reader.skipEmptyRecords = true
        return reader
    }
}

/**
 * The core file entry
 */
class Core extends Entry {
    Core(dwca, config) {
        super(dwca, config)
        id = config.id ? fields[config.id.@index.toInteger()] : null
    }
}

/**
 * An extension file entry that contains additional data associated with the core.
 */
class Extension extends Entry {
    Extension(dwca, config) {
        super(dwca, config)
        id = config.coreId ? fields[config.coreId.@index.toInteger()] : null
    }
}
