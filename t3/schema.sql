DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS registries;
DROP TABLE IF EXISTS messages;

CREATE TABLE users (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL, -- 50
  login TEXT NOT NULL, -- 20
  group_id TEXT NOT NULL,
  password TEXT NOT NULL,
  certificate TEXT NOT NULL, -- 255
  private_key TEXT, -- 256
  directory TEXT, -- 255
  num_accesses INTEGER NOT NULL,
  num_queries INTEGER NOT NULL,
  FOREIGN KEY(group_id) REFERENCES groups(id)
);

CREATE TABLE groups (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE registries (
  id INTEGER PRIMARY KEY,
  code INTEGER NOT NULL,
  user_id INTEGER,
  filename TEXT,
  created TIME,
  FOREIGN KEY(user_id) REFERENCES users(id),
  FOREIGN KEY(code) REFERENCES messages(id)
);

CREATE TABLE messages (
  id INTEGER PRIMARY KEY,
  text TEXT NOT NULL
);


INSERT INTO groups VALUES(1, 'Administrador');
INSERT INTO groups VALUES(2, 'Usuário');

INSERT INTO messages VALUES(1001, 'Sistema iniciado.');
INSERT INTO messages VALUES(1002, 'Sistema encerrado.');


-- Test registries
INSERT INTO registries VALUES(1, 1001, null, null, 1);
