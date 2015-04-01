#Transform a Darwin Core Archive into a form suitable for upload to the Atlas of Living Australia

Darwin Core (DwC) can produce archives (DwCA) that contain a variety of linked data.
A DwCA can be structured to have a relational form, with a core file containing the main stream of
data and then extension files containing additional data with a (potential 1 to many relationship).
A common example is a core file containing Occurrence-type records with an additional file containing
MeasurementOrFact.
A description file, called `meta.xml`, describes each file in the archive.

The Atlas of Living Australia (ALA) uses DwC but tries to use a flat format for each occurrence for efficiency reasons.
Additional information, which would be in a related measurement or fact in the DwCA is added as separate terms

This code flattens a DwCA consisting of Occurrence/MeasurementOrFact entries by pulling apart the extension data
and constructing new terms for the data. The resulting output is a single flat file of records using DwC terms,
suitable for upload into the ALA.
