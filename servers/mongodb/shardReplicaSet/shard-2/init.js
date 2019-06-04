rs.initiate({ 
  _id: "shard-2-repl-set",
  members: [
    { _id : 0, host : "mongo-shard-2-1:27018" },
    { _id : 1, host : "mongo-shard-2-2:27018" },
    { _id : 2, host : "mongo-shard-2-3:27018" }
  ]
})