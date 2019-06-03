# Rabbit Save

Project that saves data from a text file to a Postgres database as soon as possible. It also preserves the order within match id from the data stream.

## Getting Started

This are instructions on how to start and use the application.

### Prerequisites

```
Docker
```

## Running

### Start RabbitMQ and Postgres servers

```
cd servers
docker-compose up
```

### Start Read from Queue write to Db process

```
cd rabbitSaveReadFromQueueAndSaveToDb
docker-compose up --build
```

### Start Write to Queue process

```
cd rabbitSaveWriteToQueue
docker-compose up --build
```

## Stopping

Either run `docker-compose down` or stop them with `Ctrl + c`

## Connecting

### Postgres db

Port 5432 is exposed. So just connect to localhost:5432 with you favourite psql client.

Default username is postgres and password is rabbitsavepass. Database is rabbitsave.

### RabbitMQ

Rabbits are running on the port range 50000:50009.

## Architecture

```
data stream (file) --> write to queue app --> splits data by match id *     --> rabbitmq queue 0     -->   read from queue write to database app
                                              match id % number of queues   
                                                                            --> rabbitmq queue 1     -->   read from queue write to database app
                                          
                                                                            --> rabbitmq queue 2     -->   read from queue write to database app

                                                                            --> rabbitmq queue 3     -->   read from queue write to database app

                                                                            --> rabbitmq queue 4     -->   read from queue write to database app
                                                                                                                                                        --> postgres db
                                                                            --> rabbitmq queue 5     -->   read from queue write to database app

                                                                            --> rabbitmq queue 6     -->   read from queue write to database app

                                                                            --> rabbitmq queue 7     -->   read from queue write to database app

                                                                            --> rabbitmq queue 8     -->   read from queue write to database app

                                                                            --> rabbitmq queue 9     -->   read from queue write to database app
                                                                            
```

&ast; Write to queue app combines lines into packages (json format) of 1000 (configurable) and then sends it to a correct queue. This is because if you send lines 1 by 1 to queue message loss will occur in the RabbitMQ.

## Timings [ms]

### Max diff

```
select savedat - receivedat as diff, *
from entry
where savedat - receivedat = (select max(savedat - receivedat) from entry)
```

```
diff|id     |marketid|matchid |outcomeid                          |receivedat   |savedat      |specifiers                                  |
----|-------|--------|--------|-----------------------------------|-------------|-------------|--------------------------------------------|
4208|1210000|     535|   65449|pre:outcometext:5225860            |1559549011209|1559549015417|variant=pre:markettext:51404                |
4208|1210001|     218|14203679|4                                  |1559549011209|1559549015417|setnr=1|gamenr=8|pointnr=6                  |
4208|1210002|      14|14182409|1712                               |1559549011209|1559549015417|hcp=2:0                                     |
4208|1210003|     225|14147379|13                                 |1559549011209|1559549015417|total=142.5                                 |
4208|1210004|     209|14198839|3                                  |1559549011209|1559549015417|setnr=2|gamenrX=3|gamenrY=4                 |
4208|1210005|     225|14141049|13                                 |1559549011209|1559549015417|total=140.5                                 |
```

### Avg diff

```
select avg(savedat - receivedat) as avg_diff
from entry
```

```
avg_diff             |
---------------------|
1677.6392131845466325|
```

## Entry ordering in Postgres

### Entry count

```
select count(*) from entry
```

```
count |
------|
302536|
```

### Order

```
select matchid, marketid, outcomeid, specifiers
from entry
where matchid = 13762991
order by id
limit 100
```

```
matchid |marketid|outcomeid|specifiers|
--------|--------|---------|----------|
13762991|      60|2        |          |
13762991|      90|13       |total=0.5 |
13762991|       5|19       |          |
13762991|      90|12       |total=2.5 |
13762991|      45|314      |          |
13762991|      45|294      |          |
13762991|      47|424      |          |
13762991|      14|1712     |hcp=0:1   |
13762991|      68|12       |total=1.5 |
13762991|      86|4        |          |
13762991|      45|284      |          |
13762991|      14|1711     |hcp=1:0   |
13762991|      10|11       |          |
13762991|      45|316      |          |
13762991|      14|1713     |hcp=0:1   |
13762991|       1|1        |          |
13762991|      90|13       |total=1.75|
13762991|      68|13       |total=0.5 |
13762991|       5|17       |          |
13762991|      45|298      |          |
13762991|      45|310      |          |
13762991|      45|306      |          |
13762991|      47|420      |          |
13762991|      52|440      |          |
13762991|       2|5        |          |
13762991|      45|276      |          |
13762991|      45|312      |          |
13762991|       5|14       |          |
13762991|      45|302      |          |
13762991|      90|12       |total=1.75|
13762991|      29|74       |          |
13762991|      45|308      |          |
13762991|      45|322      |          |
13762991|      10|9        |          |
13762991|     122|76       |          |
13762991|      69|12       |total=0.5 |
```

## Authors

* **Vojko Drev** - *Initial work* - (vojkodrev@gmail.com)
