# elasticbak

[English](README.md)

## 目标

backup是elasticsearch索引离线备份工具，用于将索引备份索引元数据（setting、mapping），并将索引数据导出为json文档.

# License

Apache License 2.0


## Build & Install


```
git clone https://github.com/jiashiwen/elasticbak 
```

```
cd elasticbak
```

```
gradle clean build
```

生成文件 'build/libs/elastictbak-1.0.jar‘

###参数说明
```
    --backupdir
      备份目录，该目录下生成索引备份集（以索引名称命名的目录），该备份集包括索引元数据备份文件（indexname.meta）和数据备份文件（indexname_num.data） 
      Default: ./
    --backupindexes
      备份索引。可以用','分割，支持通配符，如果备份集群中有索引则用'"*"',注意必须用双引号
    --backupset
      恢复索引时需要指定的备份集路径 
    --cluster
      Elasticsearch 集群名称
      Default: elasticsearch
    --exp
      备份模式，与--imp不同时出现
      Default: false
    --filesize
      每个data文件的文档数量，也是每次es批量去除数据的数量,默认 500 
      Default: 500
    --help
      Default: false
    --host
      Elasticsearch cluster 中masterip地址,默认为 '127.0.0.1'. 
      Default: 127.0.0.1
    --imp
      恢复模式 ，与--exp不同时出现
      Default: false
    --metafile
      恢复索引时需要指定的meta文件，用于重建索引setting、mapping
      Default: <empty string>
    --port
      Elasticsearch 端口,默认 9300 
      Default: 9300
    --restoreindex
      需要恢复的索引名称，可以与备份索引名称不同 
    --threads
      并发线程数
      Default: 2
  

```

## 使用方法

* 备份索引
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
*备份集群中所有索引
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
*恢复名为idxexample的索引
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