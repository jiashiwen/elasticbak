# elasticbak

[简体中文](README_cn.md)

## Purpose

This programe is a command line tools for backup elasticsearch index to file.

# License

Apache License 2.0


## How Install


```
git clone https://github.com/jiashiwen/elasticbak 
```
```
cd elasticbak
```
```
gradle clean build
```

you should see 'build/libs/elastictbak-1.0.jar‘





###parameter declaration
```
   Usage: java -jar elasticbak.jar <exp/imp> [options] 
  Options:
    --backupdir
      Backup directory,default is current 
      Default: ./
    --backupindexes
      Indexes name split by ',',support wildcard character '*' and '?'
    --backupset
      The folder stored backup data include .meta file and some .data files 
    --cluster
      Elasticsearch cluster name,default is elasticsearch 
      Default: elasticsearch
    --exp
      export mode 
      Default: false
    --filesize
      Quantity docs per file,default is 500 
      Default: 500
    --help
      Default: false
    --host
      Elasticsearch cluster one of master ip address,default is '127.0.0.1'. 
      Default: 127.0.0.1
    --imp
      import mode 
      Default: false
    --metafile
      Restore Index from metadata,include sttings and mappings 
      Default: <empty string>
    --port
      Elasticsearch port,default is 9300 
      Default: 9300
    --restoreindex
      restored index name  
    --threads
      Threads for backup or restore,default is cpu max processors 
      Default: 4
    --type
      Transfor type value is [data,meta,force] and default value is 'meta'.If 
      value is 'metadata' try to create a new empty target index as 
      source;'data' copy source index documents to target index; 'force' 
      delete target index if exists and copy source index to target index. 
      Default: meta

```

##Use case

* backup indexes
```
java -jar elasticbak-0.1.jar \
--exp \
--cluster es-example \
--host 192.168.0.1 \
--filesize 1000 \
--backupdir ./backupesidx \
--backupindexes index1,index2,index3,idx_* \
--threads 4
```
* backup all indexes in the cluster
```
java -jar elasticbak-0.1.jar \
--exp \
--cluster es-example \
--host 192.168.0.1 \
--filesize 1000 \
--backupdir ./backupesidx \
--backupindexes "*" \
--threads 4
```
* restore indexe named idxexample
```
java -jar elasticbak-0.1.jar \
--imp \
--cluster es-example \
--host 192.168.0.1 \
--restoreindex idxexample \
--metafile backupset/idx/idx.meta \
--backupset backuspset/idx \
--threads 4
```