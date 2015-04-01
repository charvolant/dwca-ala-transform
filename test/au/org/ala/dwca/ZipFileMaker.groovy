package au.org.ala.dwca

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class ZipFileMaker {
    def makeZip(URL... sources) {
        File zfn = File.createTempFile("test", ".dwca")
        ZipOutputStream os = new ZipOutputStream(new FileOutputStream(zfn))
        byte[] buffer = new byte[1024]
        int n

        for (s in sources) {
            def name = s.path
            def si = name.lastIndexOf('/')
            name = si < 0 ? name : name.substring(si + 1)
            ZipEntry entry = new ZipEntry(name: name)
            InputStream is = s.openStream()
            os.putNextEntry(entry)
            while ((n = is.read(buffer)) >= 0) {
                os.write(buffer, 0, n)
            }
            os.closeEntry()
            is.close()
        }
        os.close()
        return new ZipFile(zfn, ZipFile.OPEN_READ)
    }
}

