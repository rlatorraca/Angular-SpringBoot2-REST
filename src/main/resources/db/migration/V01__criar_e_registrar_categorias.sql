CREATE TABLE categoria (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL
);


INSERT INTO categoria (nome) values ('Alimentação');
INSERT INTO categoria (nome) values ('Aluguel');
INSERT INTO categoria (nome) values ('Carro');
INSERT INTO categoria (nome) values ('Energia');
INSERT INTO categoria (nome) values ('Farmárcia');
INSERT INTO categoria (nome) values ('Internet e TV');
INSERT INTO categoria (nome) values ('Lazer');
INSERT INTO categoria (nome) values ('Supermercado');
INSERT INTO categoria (nome) values ('Outros');
