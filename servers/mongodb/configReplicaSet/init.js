rs.initiate({ 
  _id: "config-repl-set",
  configsvr: true,
  members: [
    { _id : 0, host : "mongo-config-1:27019" },
    { _id : 1, host : "mongo-config-2:27019" },
    { _id : 2, host : "mongo-config-3:27019" }
  ]
})