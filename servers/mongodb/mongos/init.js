sh.addShard( "shard-1-repl-set/mongo-shard-1-1:27018");
sh.addShard( "shard-2-repl-set/mongo-shard-2-1:27018");

db.createCollection("entries");

sh.enableSharding("rabbitsave")
sh.shardCollection("rabbitsave.entries", { "rabbitSaveEntryId" : "hashed" } );

