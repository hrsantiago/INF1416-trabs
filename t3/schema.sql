-- When done, only create tables if they do not exist.

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS registries;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS tanlist;

CREATE TABLE users (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL, -- 50
  login TEXT UNIQUE NOT NULL, -- 20
  group_id TEXT NOT NULL,
  password TEXT NOT NULL,
  salt TEXT NOT NULL, -- 9
  certificate TEXT NOT NULL, -- 255
  private_key TEXT, -- 256
  directory TEXT, -- 255
  num_accesses INTEGER DEFAULT 0,
  num_queries INTEGER DEFAULT 0,
  FOREIGN KEY(group_id) REFERENCES groups(id)
);

CREATE TABLE groups (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE registries (
  id INTEGER PRIMARY KEY,
  message_id INTEGER NOT NULL,
  user_id INTEGER,
  filename TEXT,
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(user_id) REFERENCES users(id),
  FOREIGN KEY(message_id) REFERENCES messages(id)
);

CREATE TABLE messages (
  id INTEGER PRIMARY KEY,
  text TEXT NOT NULL
);

CREATE TABLE tanlist (
  id INTEGER,
  user_id INTEGER,
  password TEXT NOT NULL,
  used INTEGER DEFAULT 0,
  PRIMARY KEY(id, user_id),
  FOREIGN KEY(user_id) REFERENCES users(id)
);

INSERT INTO groups VALUES(1, 'Administrador');
INSERT INTO groups VALUES(2, 'Usuario');

INSERT INTO messages VALUES(1001, 'Sistema iniciado.');
INSERT INTO messages VALUES(1002, 'Sistema encerrado.');
INSERT INTO messages VALUES(2001, 'Autenticação etapa 1 iniciada.');
INSERT INTO messages VALUES(2002, 'Autenticação etapa 1 encerrada.');
INSERT INTO messages VALUES(2003, 'Login name <login_name> identificado com acesso liberado.');
INSERT INTO messages VALUES(2004, 'Login name <login_name> identificado com acesso bloqueado.');
INSERT INTO messages VALUES(2005, 'Login name <login_name> não identificado.');
INSERT INTO messages VALUES(3001, 'Autenticação etapa 2 iniciada para <login_name>.');
INSERT INTO messages VALUES(3002, 'Autenticação etapa 2 encerrada para <login_name>.');
INSERT INTO messages VALUES(3003, 'Senha pessoal verificada positivamente para <login_name>.');
INSERT INTO messages VALUES(3004, 'Senha pessoal verificada negativamente para <login_name>.');
INSERT INTO messages VALUES(3005, 'Primeiro erro da senha pessoal contabilizado para <login_name>.');
INSERT INTO messages VALUES(3006, 'Segundo erro da senha pessoal contabilizado para <login_name>.');
INSERT INTO messages VALUES(3007, 'Terceiro erro da senha pessoal contabilizado para <login_name>.');
INSERT INTO messages VALUES(3008, 'Acesso do usuario <login_name> bloqueado pela autenticação etapa 2.');
INSERT INTO messages VALUES(4001, 'Autenticação etapa 3 iniciada para <login_name>.');
INSERT INTO messages VALUES(4002, 'Autenticação etapa 3 encerrada para <login_name>.');
INSERT INTO messages VALUES(4003, 'Senha de única vez verificada positivamente para <login_name>.');
INSERT INTO messages VALUES(4004, 'Primeiro erro da senha de única vez contabilizado para <login_name>.');
INSERT INTO messages VALUES(4005, 'Segundo erro da senha de única vez contabilizado para <login_name>.');
INSERT INTO messages VALUES(4006, 'Terceiro erro da senha de única vez contabilizado para <login_name>.');
INSERT INTO messages VALUES(4009, 'Acesso do usuario <login_name> bloqueado pela autenticação etapa 3.');
INSERT INTO messages VALUES(5001, 'Tela principal apresentada para <login_name>.');
INSERT INTO messages VALUES(5002, 'Opção 1 do menu principal selecionada por <login_name>.');
INSERT INTO messages VALUES(5003, 'Opção 2 do menu principal selecionada por <login_name>.');
INSERT INTO messages VALUES(5004, 'Opção 3 do menu principal selecionada por <login_name>.');
INSERT INTO messages VALUES(5005, 'Opção 4 do menu principal selecionada por <login_name>.');
INSERT INTO messages VALUES(6001, 'Tela de cadastro apresentada para <login_name>.');
INSERT INTO messages VALUES(6002, 'Botão cadastrar pressionado por <login_name>.');
INSERT INTO messages VALUES(6003, 'Caminho do certificado digital inválido fornecido por <login_name>.');
INSERT INTO messages VALUES(6004, 'Confirmação de dados aceita por <login_name>.');
INSERT INTO messages VALUES(6005, 'Confirmação de dados rejeitada por <login_name>.');
INSERT INTO messages VALUES(6006, 'Botão voltar de cadastro para o menu principal pressionado por <login_name>.');
INSERT INTO messages VALUES(7001, 'Tela de carregamento da chave privada apresentada para <login_name>.');
INSERT INTO messages VALUES(7002, 'Caminho da chave privada inválido fornecido por <login_name>.');
INSERT INTO messages VALUES(7003, 'Frase secreta inválida fornecida por <login_name>.');
INSERT INTO messages VALUES(7004, 'Erro de validação da chave privada com o certificado digital de <login_name>.');
INSERT INTO messages VALUES(7005, 'Chave privada validada com sucesso para <login_name>.');
INSERT INTO messages VALUES(7006, 'Botão voltar de carregamento para o menu principal pressionado por <login_name>.');
INSERT INTO messages VALUES(8001, 'Tela de consulta de arquivos secretos apresentada para <login_name>.');
INSERT INTO messages VALUES(8002, 'Botão voltar de consulta para o menu principal pressionado por <login_name>.');
INSERT INTO messages VALUES(8003, 'Botão Listar de consulta pressionado por <login_name>.');
INSERT INTO messages VALUES(8006, 'Caminho de pasta inválido fornecido por <login_name>.');
INSERT INTO messages VALUES(8007, 'Lista de arquivos apresentada para <login_name>.');
INSERT INTO messages VALUES(8008, 'Arquivo <arq_name> selecionado por <login_name> para decriptação.');
INSERT INTO messages VALUES(8009, 'Arquivo <arq_name> decriptado com sucesso para <login_name>.');
INSERT INTO messages VALUES(8010, 'Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para <login_name>.');
INSERT INTO messages VALUES(8011, 'Falha na decriptação do arquivo <arq_name> para <login_name>.');
INSERT INTO messages VALUES(8012, 'Falha na verificação do arquivo <arq_name> para <login_name>.');
INSERT INTO messages VALUES(9001, 'Tela de saída apresentada para <login_name>.');
INSERT INTO messages VALUES(9002, 'Botão sair pressionado por <login_name>.');
INSERT INTO messages VALUES(9003, 'Botão voltar de sair para o menu principal pressionado por <login_name>.');

-- Tests
INSERT INTO users VALUES(1, 'Fulano', 'fulano', 1, '4f900d95a27390faec3179c76fec21e2', '123456789', './Keys/usercert-x509.crt', null, null, 0, 0); -- Pass CAFEBE
INSERT INTO users VALUES(2, 'Cicrano', 'cicrano', 2, '8872d019ddea79565e18298d77e1a065', '123456789', './Keys/usercert-x509.crt', null, null, 0, 0); -- Pass BAGEGA

INSERT INTO registries (id, message_id) VALUES(1, 1001);
INSERT INTO registries (id, message_id, user_id, filename) VALUES(2, 8011, 1, 'arquivox');
INSERT INTO registries (id, message_id, user_id) VALUES(3, 5002, 2);
