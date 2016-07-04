# DataGenerator
## 编译和运行
> mvn package

>cp jobs.xml ./target/jobs.xml

> cd target

> mkdir pool

> mkdir output

> java -jar DataGenerator-0.0.1-SNAPSHOT.jar

## 查看结果
> cat output/test.sql
```sql
insert into test (hex,author,name,sn) values('123456_da99aa0efb','gongdexing','王五','123456')
insert into test (hex,author,name,sn) values('123456_4801bc3556','gongdexing','王五','123456')
insert into test (hex,author,name,sn) values('123456_fdf9e2100e','gongdexing','王八','123456')
```

## 基本使用
> cat jobs.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <jobs>
        <job>
            <id>1</id>
            <num>3</num>
            <table>test</table>
            <filename>test.sql</filename>
            <detail>
                <author>$var{author}</author>
                <sn>123456</sn>
                <hex>$var{sn}_$rule{hex,10}</hex>
                <name>$pool{name}</name>
            </detail>
        </job>
    </jobs>
</root>
```
### _$var{variable}_ 用法
在上面的 **jobs.xml** 中 **_$var{author}_** 代表引用 **author** 这个变量，在jar中config.properties中进行了配置
> cat config.properties
```properties
#内置变量
var.author=gongdexing
#内置类型
rule.bin=01
rule.oct=01234567
rule.dec=0123456789
rule.hex=0123456789abcdef
rule.letter=abcdefghijklmnopqrstuvwxyz
rule.string=0123456789abcdefghijklmnopqrstuvwxyz
##内置池
pool.name=张三,李四,王五,王八
pool.sex=男,女
```
可以看到 **var.author** 被设置为 **gongdexing** ，但是 **_$var{sn}_** 并没有在config.properties中被定义，这时 **_$var{sn}_** 指的是jobs.xml中的 ```<sn>123456</sn>``` 标签，即为 **123456**

### _$rule{type,len}_ 用法
**_$rule{type,len}_** 中间是没有任何空格的，在jobs.xml中 **_$rule{hex,10}_** 代表从 **hex** 类型的数据中随机取出10个，而 **hex** 在 **config.properties** 中进行了定义
```properties
rule.hex=0123456789abcdef
```
同样可以使用 **_$rule{bin,2}_** 表示随机产生一个十位数

### _$pool{name}_ 用法
**_$pool{name}_** 用法和 **_$var{variable}_** 类似，但是 **_$pool{name}_** 还有一种更加高级的用法，比如每次要从上百个证件号码中随机取一个，如果在 **config.properties** 中定义，会显得非常臃肿，这时可以在 **pool** 目录中添加一个 **ids.csv** 文件

> ids.csv
```
  001
  002
  003
  004
  005
```

将 **jobs.xml** 修改如何如何
```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <jobs>
        <job>
            <id>1</id>
            <num>3</num>
            <table>test</table>
            <filename></filename>
            <detail>
				        <id>$pool{ids.csv}</id>
                <author>$var{author}</author>
                <sn>123456</sn>
                <hex>$var{sn}_$rule{hex,10}</hex>
				        <name>$pool{name}</name>
				        <sex>$pool{sex}</sex>
            </detail>
        </job>                    
    </jobs>
</root>
```
再次运行

> java -jar DataGenerator-0.0.1-SNAPSHOT.jar
```sql
insert into test (author,sex,name,hex,id,sn) values('gongdexing','女','张三','123456_bc5f720d09','002','123456')
insert into test (author,sex,name,hex,id,sn) values('gongdexing','女','李四','123456_461fd2fbdd','005','123456')
insert into test (author,sex,name,hex,id,sn) values('gongdexing','男','王八','123456_f800f13a6f','001','123456')
```
