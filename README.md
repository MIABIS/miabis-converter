# MIABIS converter
MIABIS converter is a tool that aims to facilitate the migration of different formats to MIABIS sample exchange compliant XML. It is sample centered and its based on the [miabis-sample-exchange-format] (https://github.com/MIABIS/miabis-sample-exchange-format).

Additionally the tool is able to index TAB files following a given format into Elasticsearch. 

At the moment the tool supports the following commands:

```
usage: miabis-converter
 -c,--cluster <elastic search cluster>   with -i: elastic search cluster
                                         group. It defaults to
                                         localhost:9300
 -d,--delimiter <column delimiter>       with -t, -i: column delimiter. It
                                         defaults to TAB
 -h,--help                               print this message
 -i,--index <input file(s)>              indexes a set of files. If only
                                         one file is supplied it asumes is
                                         a MIABIS TAB file, else five
                                         files must be supplied (sample,
                                         biobank, saple collection, study,
                                         contact information). The list of
                                         files must be separated by a
                                         space.
 -m,--map <map file>                     with -t, -i: miabis mapping file.
 -t,--transform <input files>            transforms a set of files to
                                         MIABIS TAB. Five files must be
                                         supplied (sample, biobank, saple
                                         collection, study, contact
                                         information). The list of files
                                         must be separated by a space.
 ```
 
 ## Examples
 
 ### Indexing files
 
 How to index a single MIABIS TAB file:
 ```
 java -jar miabis-converter-1.0.0-SNAPSHOT.jar -i Miabis.tab
 ```
 
 How to index a set of files:
 ```
 java -jar miabis-converter-1.0.0-SNAPSHOT.jar -i sample.txt biobank.txt sampleCollection.txt study.txt contactInfo.txt -m example.mapping.properties
 ```
 
 ### Tranform a set of files into MIABIS TAB:
 ```
 java -jar miabis-converter-1.0.0-SNAPSHOT.jar -t sample.txt biobank.txt sampleCollection.txt study.txt contactInfo.txt -m example.mapping.properties -d ,
 ```
 
 
 

