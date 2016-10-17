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
  user_login TEXT,
  filename TEXT,
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  --FOREIGN KEY(user_id) REFERENCES users(id),
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
INSERT INTO users VALUES(1, 'Fulano', 'fulano', 1, '4f900d95a27390faec3179c76fec21e2', '123456789', 'Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number: 0 (0x0)
    Signature Algorithm: sha1WithRSAEncryption
        Issuer: C=BR, ST=RJ, L=Rio, O=PUC, OU=DI, CN=INF1416 AC/emailAddress=ca@grad.inf.puc-rio.br
        Validity
            Not Before: Sep 14 20:54:18 2015 GMT
            Not After : Sep 13 20:54:18 2016 GMT
        Subject: C=BR, ST=RJ, O=PUC, OU=DI, CN=Anderson Oliveira da Silva/emailAddress=oliveira@grad.inf.puc-rio.br
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                Public-Key: (1024 bit)
                Modulus:
                    00:c1:30:df:c0:2b:8a:82:8b:52:62:40:81:9e:88:
                    6c:da:02:29:b8:25:23:56:81:e4:4e:9f:13:33:e3:
                    3c:e7:76:5b:6f:36:ab:17:bb:65:a9:1e:64:a7:d1:
                    bf:1b:8f:92:22:ef:fd:c9:ba:c4:54:37:60:53:d7:
                    ce:43:f0:e8:b8:ba:43:23:ba:fc:3f:0b:34:58:2b:
                    a8:77:05:b0:da:31:00:07:04:7d:85:4d:58:82:9f:
                    23:7d:d6:7e:9c:3d:d1:46:bc:dc:49:0d:f9:f7:ce:
                    27:25:4e:ef:f6:11:39:6a:e0:30:58:e8:c2:68:ef:
                    b5:73:9c:de:90:9d:6b:fd:cd
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Basic Constraints: 
                CA:FALSE
            Netscape Comment: 
                OpenSSL Generated Certificate
            X509v3 Subject Key Identifier: 
                22:6F:9C:E6:65:8F:4E:CA:E8:18:DF:B6:C7:EC:F7:B9:3A:9D:79:52
            X509v3 Authority Key Identifier: 
                keyid:40:B4:AB:6C:E0:95:72:0A:0B:1D:5F:BA:FE:FC:A6:0B:0C:29:8E:FB

    Signature Algorithm: sha1WithRSAEncryption
         25:35:70:37:3f:f5:04:8a:19:c8:43:ef:a9:96:c0:c6:8e:08:
         ec:f5:84:2a:17:32:e3:44:b7:c0:26:7f:68:28:5d:77:d1:63:
         c7:4b:da:60:29:50:18:15:0c:aa:ff:33:0a:76:51:8d:08:6e:
         e5:53:00:6f:66:3d:1b:b5:99:af:c3:83:0e:77:d3:94:94:85:
         29:e9:2f:b6:87:27:29:d6:cf:3d:88:0c:c7:ec:a1:d3:f8:2c:
         3f:fa:da:47:2b:4c:8c:e0:63:4a:c3:40:aa:f9:68:31:64:24:
         dd:26:d1:c9:99:39:99:9f:59:a3:6d:91:bf:1b:53:fe:f0:00:
         8b:03
-----BEGIN CERTIFICATE-----
MIIC+DCCAmGgAwIBAgIBADANBgkqhkiG9w0BAQUFADB/MQswCQYDVQQGEwJCUjEL
MAkGA1UECAwCUkoxDDAKBgNVBAcMA1JpbzEMMAoGA1UECgwDUFVDMQswCQYDVQQL
DAJESTETMBEGA1UEAwwKSU5GMTQxNiBBQzElMCMGCSqGSIb3DQEJARYWY2FAZ3Jh
ZC5pbmYucHVjLXJpby5icjAeFw0xNTA5MTQyMDU0MThaFw0xNjA5MTMyMDU0MTha
MIGHMQswCQYDVQQGEwJCUjELMAkGA1UECAwCUkoxDDAKBgNVBAoMA1BVQzELMAkG
A1UECwwCREkxIzAhBgNVBAMMGkFuZGVyc29uIE9saXZlaXJhIGRhIFNpbHZhMSsw
KQYJKoZIhvcNAQkBFhxvbGl2ZWlyYUBncmFkLmluZi5wdWMtcmlvLmJyMIGfMA0G
CSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBMN/AK4qCi1JiQIGeiGzaAim4JSNWgeRO
nxMz4zzndltvNqsXu2WpHmSn0b8bj5Ii7/3JusRUN2BT185D8Oi4ukMjuvw/CzRY
K6h3BbDaMQAHBH2FTViCnyN91n6cPdFGvNxJDfn3ziclTu/2ETlq4DBY6MJo77Vz
nN6QnWv9zQIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVu
U1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUIm+c5mWPTsroGN+2
x+z3uTqdeVIwHwYDVR0jBBgwFoAUQLSrbOCVcgoLHV+6/vymCwwpjvswDQYJKoZI
hvcNAQEFBQADgYEAJTVwNz/1BIoZyEPvqZbAxo4I7PWEKhcy40S3wCZ/aChdd9Fj
x0vaYClQGBUMqv8zCnZRjQhu5VMAb2Y9G7WZr8ODDnfTlJSFKekvtocnKdbPPYgM
x+yh0/gsP/raRytMjOBjSsNAqvloMWQk3SbRyZk5mZ9Zo22RvxtT/vAAiwM=
-----END CERTIFICATE-----', null, null, 0, 0); -- Pass CAFEBE
