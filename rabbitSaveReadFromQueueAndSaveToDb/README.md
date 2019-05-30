# Sort Save

Project that saves data from a text file to a Postgres database as soon as possible.

## Getting Started

This are instructions on how to start the application.

### Prerequisites

```
Docker
```

## Running

```
docker-compose up -d
docker logs sortsave_app_1
```

## Connecting to Postgres db

Port 5432 is exposed. So just connect to localhost:5432 with you favourite psql client.

Default username is postgres and password is sortsavepass. Database is sortsave.

## Stopping

```
docker-compose down
```

## Timings (logs)

```
20:10:37,745  INFO SortSave:15 - START + PARSE!
20:10:38,747  INFO SortSaveLineSorter:22 - BEFORE SORT
20:10:38,998  INFO SortSaveLineSorter:43 - AFTER SORT
20:10:39,885  INFO SortSaveLineDbSaver:28 - BEFORE DB SAVE
20:10:47,000  INFO SortSaveLineDbSaver:38 - AFTER DB SAVE
20:10:47,003  INFO SortSave:36 - DONE!
```

## Authors

* **Vojko Drev** - *Initial work* - (vojkodrev@gmail.com)
