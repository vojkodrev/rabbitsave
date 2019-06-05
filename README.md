# Rabbit Save

Project that saves data from a text file to a Postgres database as soon as possible. It also preserves the order within match id from the data stream.

## Getting Started

This are instructions on how to start and use the application.

### Prerequisites

```
Docker
```

## Running

### Start RabbitMQ

```
cd servers/rabbitmq
docker-compose up
```

### Start MongoDB

#### Config replica set

```
cd servers/mongodb/configReplicaSet
docker-compose up
docker exec mongo-config-1 mongo --host localhost --port 27019 /usr/local/mongo-config-init/init.js
```

#### Shard replica set

```
cd servers/mongodb/shardReplicaSet
docker-compose up
docker exec mongo-shard-1-1 mongo --host localhost --port 27018 /usr/local/mongo-shard-init/init.js
docker exec mongo-shard-2-1 mongo --host localhost --port 27018 /usr/local/mongo-shard-init/init.js
```

#### Mongos

```
cd servers/mongodb/mongos
docker-compose up
docker exec mongos-1 mongo rabbitsave --host localhost --port 27017 /usr/local/mongos-init/init.js
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

### Mongo db

Mongo db is running on the port range 60001:60002.

### RabbitMQ

Rabbits are running on the port range 50000:50009.

## Architecture

```
data stream (file) --> write to queue app --> splits data by match id *     --> rabbitmq queue 0     -->   read from queue write to database app
                                              match id % number of queues   
                                                                            --> rabbitmq queue 1     -->   read from queue write to database app
                                          
                                                                            --> rabbitmq queue 2     -->   read from queue write to database app
                                                                                                                                                        --> mongo db router 1
                                                                            --> rabbitmq queue 3     -->   read from queue write to database app

                                                                            --> rabbitmq queue 4     -->   read from queue write to database app
                                                                                                                                                        --> mongo db router 2
                                                                            --> rabbitmq queue 5     -->   read from queue write to database app

                                                                            --> rabbitmq queue 6     -->   read from queue write to database app

                                                                            --> rabbitmq queue 7     -->   read from queue write to database app

                                                                            --> rabbitmq queue 8     -->   read from queue write to database app

                                                                            --> rabbitmq queue 9     -->   read from queue write to database app
                                                                            
```

&ast; Write to queue app waits for 3000 (configurable) lines to arrive or 250ms (also configurable). Splits them into packages (json format) by matchId % num of queues. Then sends them to queues. This proces is repeating. This is because if you send lines 1 by 1 to queues, message loss will occur in the RabbitMQ.

Check commits from b59f1a7 to fc8c383 where I tried to solve this problems in different ways (different sql lib, remove rabbit clustering, run in different threads ...)

## Authors

* **Vojko Drev** - *Initial work* - (vojkodrev@gmail.com)
