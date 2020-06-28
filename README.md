# seckill
## 解决一个用户购买两件商品
给商品id和用户id建立唯一索引

## 解决超卖的方式
在sql中加入count>0条件，由于update是一种排他锁，因此只会有一个线程进入，这时只要判断库存大于0就行了
update goods set num = num - 1 WHERE id = 1001 and count > 0

或是CAS的原理建立一个version列
修改前先查询version，update修改版本号和库存where版本号是对的同时库存大于0。
