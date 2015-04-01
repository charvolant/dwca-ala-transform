package au.org.ala.dwca

import au.org.ala.dwca.model.Term
import au.org.ala.dwca.model.Vocabulary

import java.util.zip.ZipFile

/**
 * Convert a dataset in a DwCA to an ALA loadable dataset
 * <p>
 * Arguments:
 * <ol>
 *     <li>The DwCA file</li>
 *     <li>The output file</li>
 * </ol>
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */

def dwc = new Vocabulary<Term>(Vocabulary.class.getResource("darwin-core-vocabulary.json"))
def voc = new Vocabulary<Term>(dwc)
def dwcaFile = new File(args[0])
def ZipFile zf = new ZipFile(dwcaFile, ZipFile.OPEN_READ)
def dwca = new DwCA(zf)
def dataset = dwca.createDataset(dwc)
def writer = new FileWriter(args[1])
dataset.write(writer)
writer.close()