[![Build Status](https://travis-ci.org/jovanibrasil/auth-api.svg?branch=develop)](https://travis-ci.org/jovanibrasil/auth-api)
![Codecov branch](https://img.shields.io/codecov/c/github/jovanibrasil/auth-api/develop)

# API para autenticação utilizando JWT

Esta API permite operações básicas para cadastro e autenticação de usuários.

Para rodar o projeto você deve primeiro subir o MariaDB. Para tanto basta ir ao diretório ```/mariadb``` no projeto
e executar o comando ```make start```. Então basta executa o comando ```mvn -pl web spring-boot:run``` na raíz do projeto e
a aplicação deve iniciar. Uma vez rodando é possível acessar a documentação Swagger em ```http://localhost:8083/api/swagger-ui.html```.

 




