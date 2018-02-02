_**# dashBoard 仪表盘**_
监控服务消费者,数据可视化
依赖: spring-cloud-starter-hystrix-dashboard ;
前提: 被监控服务需要开启hystrix;
数据来源: /hystrix.stream获取信息;
可能出现问题:
初次进入某服务监控,可能会出现 Unable to connect to Command Metric Stream.
是由于被监控服务内部使用hystrix的服务从未被调用,初始数据不存在导致

