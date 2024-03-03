# Rinha APP

Desenvolvida para participar da [Rinha de Backend - 2024/Q1](https://github.com/zanfranceschi/rinha-de-backend-2024-q1)

## Stack

- **Linguagem:** Kotlin 1.9.2 (Java 21)
- **Framework:** Spring Boot com JDBC
- **Banco de dados:** PostgreSQL
- **Load Balancer:** NGINX

## Docker

Para facilitar diminuir o tamanho da imagem, foi criada uma imagem Docker com uma JRE customizada, considerando apenas
os
módulos necessários para a aplicação, utilizando o jlink e jdeps.

### Construção da Imagem

Para construir a imagem Docker, utilize o seguinte comando:

```bash
docker build -t rinha-app .
```
