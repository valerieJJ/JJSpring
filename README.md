# Movie RecSys
**This is a web system for movie recommendation based on a movie dataset(including movies and user behaviours)**

The movie data used in this project is a subset adopted from a public movie dataset MovieLen

It is mainly developed in java, html, and the techniques includes the usage of thymleaf, spring-boot, redis, mongodb, elasticsearch.
Besides, multi-threading is also applied for accelerating some processing speed.
I tried my best to obey the rules of the restful。

The demo for offline recommendation algorithm and data processing is offered in this site `https://github.com/valerieJJ/RecSys`(mainly wrote in spark-scala).

I am still working on functional refinements and improvements, even though I am busy with my academic paper at the same time.

As for environment, you can use default jvm like this:

<img width="1268" alt="image" src="https://user-images.githubusercontent.com/43733497/158498759-11464f43-f290-49ed-a908-a98fa9b9d046.png">

but I would prefer the follow refined JVM settings：
`-Xms4096M
-Xmx4096M
-Xmn3072M
-Xss1M
-XX:PermSize=256M
-XX:MaxPermSize=256M
-XX:+UseParNewGC
-XX:+UseConcMarkSweepGC
-XX:CMSInitiatingOccupancyFaction=92
-XX:+UseCMSCompactAtFullCollection
-XX:CMSFullGCsBeforeCompaction=0`

### mongDB
Useful Tool: MongoDB - Robo

start server:

`jj@master ~ % mongod --dbpath /usr/local/Cellar/data/db`

### redis

start server:

`jj@master ~ % redis-server`
<img width="1623" alt="image" src="https://user-images.githubusercontent.com/43733497/158499459-46ce4489-72af-4133-98f7-9459892ee47d.png">

Here are some system showcases：
Note: To save storage space, I didn't download all the poster images for each movie. Instead, I used the same images as general movie posters.

![image-20220316094252015](/Users/jj/Library/Application Support/typora-user-images/image-20220316094252015.png)

<img width="1280" alt="image" src="https://user-images.githubusercontent.com/43733497/158501934-0de13ccb-5eec-46e1-a3b2-e58f42b6f2e6.png">

<img width="1285" alt="image" src="https://user-images.githubusercontent.com/43733497/158501345-8cfe6ef0-97a5-4115-8dd0-7129cc249c9a.png">

<img width="1296" alt="image" src="https://user-images.githubusercontent.com/43733497/158501396-3c576e5c-5032-44c6-a5ab-6d8e1ea842f5.png">


<img width="1277" alt="image" src="https://user-images.githubusercontent.com/43733497/158501832-fb7a6d82-bbe8-4b15-b5fd-3feebe5ff946.png">



