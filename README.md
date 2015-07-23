# MIABIS converter
MIABIS converter is a tool that aims to facilitate the migration of different formats to MIABIS sample exchange compliant XML. It is sample centered and its based on the [miabis-sample-exchange-format] (https://github.com/MIABIS/miabis-sample-exchange-format).

Additionally the tool is able to index TAB files following a given format into Elasticsearch. 

At the moment the tool supports the following commands:

```
usage: miabis-converter
 -c,--cluster <elastic search cluster>   with -i: elastic search cluster
                                         group. It defaults to
                                         10.133.0.29:9300
 -h,--help                               print this message
-i,--index <input file>                 Index a file
 ```

