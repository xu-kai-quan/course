CREATE TABLE users (
    id serial PRIMARY KEY,
    username VARCHAR (50) UNIQUE NOT NULL ,
    encrypted_password VARCHAR (50) NOT NULL ,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    status VARCHAR (10) NOT NULL DEFAULT 'OK'
);
--OK DELETED
CREATE TABLE role (
    id serial PRIMARY KEY,
    name VARCHAR (50) UNIQUE NOT NULL ,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    status VARCHAR (10) NOT NULL DEFAULT 'OK'
);

CREATE TABLE user_role (
  id serial PRIMARY KEY,
  user_id INTEGER NOT NULL ,
  role_id INTEGER NOT NULL ,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  status VARCHAR (10) NOT NULL DEFAULT 'OK'
);

CREATE TABLE permission (
    id serial PRIMARY KEY,
    name VARCHAR (50) NOT NULL ,
    role_id INTEGER  NOT NULL ,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    status VARCHAR (10) NOT NULL DEFAULT 'OK'
);

INSERT INTO users(id,username,encrypted_password)values (1,'Student1','');
INSERT INTO users(id,username,encrypted_password)values (2,'Teacher2','');
INSERT INTO users(id,username,encrypted_password)values (3,'Admin3','');

INSERT INTO role(id,name)values (1,'学生');
INSERT INTO role(id,name)values (2,'老师');
INSERT INTO role(id,name)values (3,'管理员');

INSERT INTO user_role(user_id,role_id)values (1,1);
INSERT INTO user_role(user_id,role_id)values (2,2);
INSERT INTO user_role(user_id,role_id)values (3,3);

INSERT INTO permission(name,role_id)values('登录用户',1);
INSERT INTO permission(name,role_id)values('登录用户',2);
INSERT INTO permission(name,role_id)values('登录用户',3);
INSERT INTO permission(name,role_id)values('上传课程',2);
INSERT INTO permission(name,role_id)values('上传课程',3);
INSERT INTO permission(name,role_id)values('管理用户',3);

alter sequence users_id_seq restart with 4;
alter sequence role_id_seq restart with 4;
alter sequence user_role_id_seq restart with 4;
