# BootCaT
A Simple tool to Bootstrap Corpora And Terms from the Web

BootCaT automates the process of finding reference texts on the web and collating them in a single corpus.

The pipeline allows varying levels of control. In the first step, users provide a list of single- or multi-word terms to be used as seeds for text collection. These are then combined into “tuples” of varying length and sent as queries to a search engine, which returns a list of potentially relevant URLs. At this point the user has the option of inspecting the URLs and trimming them; the actual web pages are then retrieved, converted to plain text and saved in "txt" format. The corpus can thus be interrogated using most concordancers.

Using BootCat one can build a relatively large quick-and-dirty corpus (typically of about 80 texts, with default parameters and no manual quality checks) in less than half an hour. This flexible approach to the task makes BootCaT a very useful tool for translators and translation students, which has been used in the translation and terminology classroom to build small DIY corpora of varying size and specialization.

## Binaries

You can download binaries packaged for Mac, Windows and Linux on the official web site at:

https://bootcat.dipintra.it/
