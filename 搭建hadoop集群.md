## 搭建hadoop集群

使用软件:

1. ssh远程连接工具:putty
2. 虚拟机:virtualbox
3. jdk版本:jdk-8u141-linux-x64.tar.gz
4. hadoop版本:hadoop-2.6.5.tar.gz

前期准备: 4台虚拟机 作用分别如下

|  主机名称  |   作用   | 版本号  |     ip地址     |
| :--------: | :------: | :-----: | :------------: |
| centos7-01 | datanode | centos7 | 192.168.41.222 |
| centos7-02 | datanode | centos7 | 192.168.41.223 |
| centos7-03 | datanode | centos7 | 192.168.41.224 |
| centos6-04 | namenode | centos6 | 192.168.41.225 |

由于datenode的安装一致所以我在此仅用centos7-02做演示

首先先安装`jdk`和`ssh`

linux装好以后首先先将jdk上传至服务器

![1565310099480](C:\Users\root\AppData\Roaming\Typora\typora-user-images\1565310099480.png)

将jdk上传到/tmp目录下

![1565310150515](C:\Users\root\AppData\Roaming\Typora\typora-user-images\1565310150515.png)

其次解压jdk并配置环境变量 我写了一个简单的脚本:

```bash
#!/bin/bash
javahome=$2
echo ${javahome}
if test $# -ne 2
then
    echo "usage: $0  filename  jdk_dir"
    exit 1
else
    if test -f $1
      then mkdir -p ${javahome} && tar -zxf $1  --strip-components 1 -C ${javahome}
      echo "export JAVA_HOME=\$\{javahome\} " >> /etc/profile
			echo "export PATH=\$PATH:\$JAVA_HOME/bin" >> /etc/profile
			echo "export CLASSPATH=.:\$JAVA_HOME/lib/dt.jar:\$JAVA_HOME/lib/tools.jar" >> /etc/profile
      source /etc/profile
      jps
      if test $? -eq 0
        then echo java install ok..
        else echo java install error..
      fi
    fi
fi
#centos7下用此命令修改hostname
hostnamectl set-hostname node03

echo 192.168.41.225 node01 >> /etc/hosts
echo 192.168.41.222 node02 >> /etc/hosts
echo 192.168.41.223 node03 >> /etc/hosts
echo 192.168.41.224 node04 >> /etc/hosts

```

至此datanode基本安装完毕 



开始安装namenode 同样需要安装jdk以及ssh 然后上传hadoop-2.6.5.tar.gz到namenode的服务器上

![1565313461131](C:\Users\root\AppData\Roaming\Typora\typora-user-images\1565313461131.png)

解压hadoop并且配置环境变量 需要注意的是安装hadoop之前先需要安装jdk

![1565313589814](C:\Users\root\AppData\Roaming\Typora\typora-user-images\1565313589814.png)

解压完后配置环境变量 

```shell
export HADOOP_HOME=/usr/local/hadoop-2.6.5
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
source /etc/profile
-----------------------------
#写hosts文件

echo 192.168.41.225 node01 >> /etc/hosts
echo 192.168.41.222 node02 >> /etc/hosts
echo 192.168.41.223 node03 >> /etc/hosts
echo 192.168.41.224 node04 >> /etc/hosts

```

接下来做免密钥登录

```shell
ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys

#将公钥上传到其他三个datanode中
scp ~/.ssh/id_dsa.pub root@192.168.41.222:~/.ssh/node1.pub
scp ~/.ssh/id_dsa.pub root@192.168.41.223:~/.ssh/node1.pub
scp ~/.ssh/id_dsa.pub root@192.168.41.224:~/.ssh/node1.pub
#分别在其他三个namenode执行
cat ~/.ssh/node01.pub >> ~/.ssh/authorized_keys
```

配置hadoop的配置

首先先修改hadoop-env.sh中的jdk配置 

```shell
export JAVA_HOME=/usr/local/java
```

配置 core-site.xml

```xml

<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://node01:9000</value>
  </property>
 <!--local workspace-->
  <property>
    <name>hadoop.tmp.dir</name>
    <value>/var/hadoop/data/</value>
  </property>
</configuration>
```

配置   hdfs-site.xml

```xml
<configuration>
  <!--配置副本数 建议为3-->
  <property>
    <name>dfs.replication</name>
    <value>3</value>
  </property>
</configuration>
```

配置mapred-site.xml 

```xml
cp mapred-site.xml.template  mapred-site.xml
vim mapred-site.xml
--------------------------------------------------
<configuration>
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
  </property>
</configuration>

```


配置  yarn-site.xml
```xml
<configuration>

<!-- Site specific YARN configuration properties -->
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>node01</value>
  </property>
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>
</configuration>
```

配置slaves

```
node02
node03
node04
```



关闭防火墙 

```shel
service iptables stop
```

---

启动hadoop之前需要先做初始化操作

```shell
 hadoop namenode  -format
```

![1565324474367](C:\Users\root\AppData\Roaming\Typora\typora-user-images\1565324474367.png)

这样表示初始化成功 