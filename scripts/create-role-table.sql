use mydb

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
	user_id BIGINT NOT NULL,
	role_id int NOT NULL
)

ALTER TABLE users_roles
ADD CONSTRAINT PK_users_roles
PRIMARY KEY (user_id, role_id)

ALTER TABLE users_roles
ADD CONSTRAINT FK_user_id_users_roles
FOREIGN KEY (user_id) REFERENCES users(id)

ALTER TABLE users_roles
ADD CONSTRAINT FK_role_id_users_roles
FOREIGN KEY (role_id) REFERENCES roles(id)