# FEK-log-controller

1）下载apache-flume-1.6.0-bin.tar.gz
官网下载地址：http://flume.apache.org/download.html

2）安装flume
1、将软件包拷贝到服务器上，如/root/yiran目录下；
2、解压软件包，命令：tar –xvf apache-flume-1.6.0-bin.tar.gz；
3、配置*.conf文件，如example.conf，放在/root/yiran/apache-flume-1.6.0-bin/conf目录下，内容示例如下：
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
# Describe/configure the source
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444
 
# Describe the sink
#a1.sinks.k1.type = logger
a1.sinks. k1.type = elasticsearch
a1.sinks. k1.hostNames =127.0.0.1:9300
a1.sinks. k1.indexName = test_index
a1.sinks. k1.indexType = test_type_1
a1.sinks. k1.clusterName = vie61_yanshi
a1.sinks. k1.batchSize = 10
a1.sinks. k1.ttl = 5d
a1.sinks. k1.serializer =org.apache.flume.sink.elasticsearch.ElasticSearchDynamicSerializer
 
# Use a channel which buffers eventsin memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity =100
 
# Bind the source and sink to thechannel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
4、配置flume-env.sh文件，位于/root/yiran/apache-flume-1.6.0-bin/conf目录下，操作如下：
mv flume-env.sh.template flume-env.sh
vi flume-env.sh
修改JAVA_HOME
JAVA_HOME=/usr/java/jdk1.7.0_71
JAVA_OPTS="-Xms512m–Xmx1024m-Dcom.sun.management.jmxremote"
5、从elasticsearch安装包下将写入elasticsearch需要的核心包拷贝到/root/yiran/apache-flume-1.6.0-bin/lib（当收集的日志需要写入es时才进行第5步操作，否则不用进行第5步）；
elasticsearch-1.6.2.jar；
lucene-core-4.10.4.jar；
 
3）启动flume
完成上述5个步骤后，则安装完成。然后可以启动flume，命令如下：
cd /root/yiran/apache-flume-1.6.0-bin;
./bin/flume-ng agent --conf conf--conf-file conf/example.conf --name a1 -Dflume.root.logger=INFO,console &;
说明：--conf指明配置文件目录名称；--conf-file指明要运行的配置文件；--name指明agent名称，保持与*.conf配置文件里面命名一致；-Dflume.root.logger指明日志打印级别；
 
2.2、Elasticsearch安装

1） 下载elasticsearch-1.6.2.tar.gz
官网下载地址：https://www.elastic.co/downloads/elasticsearch；
 
2）安装elasticsearch
1、将软件包拷贝到服务器上，如/root/yiran目录下；
2、解压软件包，命令：tar –xvf elasticsearch-1.6.2.tar.gz；
3、修改/root/yiran/elasticsearch-1.6.2/config下elasticsearch.yml文件：
如将node.name的值设置为“test-node”，表示当前这个es服务节点名字为test-node；
修改cluster.name的值为vie61_yanshi；
修改network.host为本机ip；
4、  根据需要可配置/root/yiran/elasticsearch-1.6.2/bin目录下elasticsearch.in.sh文件；
 
3）启动elasticsearch
完成上述4个步骤后，则安装完成。然后可以启动elasticsearch，命令如下：
nohup ./elasticsearch &
 
2.3、kibana安装

1）下载kibana-4.1.10-linux-x64.tar.gz
官网下载地址：https://www.elastic.co/downloads/past-releases
 
2）安装kibana
1、将软件包拷贝到服务器上，如/root/yiran目录下；
2、解压软件包，命令：tar –xvf kibana-4.1.10-linux-x64.tar.gz；
3、修改/root/yiran/kibana-4.1.10-linux-x64/config目录下kibana.yml文件；
修改host为本机ip;
修改elasticsearch_url为要访问的elasticsearch的地址，如：http://localhost:9200；
 
3）启动kibana
完成上述3个步骤后，则安装完成。然后可以启动kibana，命令如下：
nohup ./kibana &