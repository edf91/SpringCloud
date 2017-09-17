# SpringCloud

eureka-server 
    2台 30000  30001
u-service
    2台  30002 30003
u-consumer
    2台  30004 30005
zipkin
    2台 30006 30007
hystrix-dashboard
    2台 30008 30009
turbine
    2台 30010 30011
    
consumer通过实例名称调用service，需要ribbon

docker 网咯模型


cd $WORKSPACE/docker
docker-compose up