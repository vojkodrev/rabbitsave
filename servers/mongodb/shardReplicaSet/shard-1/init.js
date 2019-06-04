rs.initiate({ 
  _id: "shard-1-repl-set",
  members: [
    { _id : 0, host : "mongo-shard-1-1:27018" },
    { _id : 1, host : "mongo-shard-1-2:27018" },
    { _id : 2, host : "mongo-shard-1-3:27018" }
  ]
})