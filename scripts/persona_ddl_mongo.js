use admin

if (db.getUser("persona_db") == null) {
  db.createUser({
    user: "persona_db",
    pwd: "persona_db",
    roles: [
      { role: "read", db: "persona_db" },
      { role: "readWrite", db: "persona_db" },
      { role: "dbAdmin", db: "persona_db" }
    ],
    mechanisms: ["SCRAM-SHA-1","SCRAM-SHA-256"]
  });
}

var personaDatabase = db.getSiblingDB("persona_db");
if (personaDatabase.getCollectionNames().indexOf("persona") === -1) {
  personaDatabase.createCollection("persona");
}

var result = personaDatabase.runCommand({
  collMod: "persona",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "nombre", "apellido", "genero"],
      properties: {
        _id: { bsonType: "int" },
        nombre: { bsonType: "string" },
        apellido: { bsonType: "string" },
        genero: { enum: ["F", "M"] },
        edad: { bsonType: ["int", "null"], minimum: 0 }
      }
    }
  },
  validationLevel: "strict",
  validationAction: "error"
});
if (result.ok !== 1) {
  throw new Error("No se pudo configurar el validador de persona: " + result.errmsg);
}
