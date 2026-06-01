# Clube do Album Gateway API

API gateway da plataforma Clube do Album.

## Responsabilidade futura

- Centralizar a entrada das chamadas do frontend.
- Encaminhar requisicoes para as APIs internas.
- Aplicar politicas transversais de seguranca, observabilidade e roteamento.

## Tecnologias usadas

- Java 17
- Spring Boot
- Maven

## Como rodar localmente

```bash
mvn spring-boot:run
```

Endpoint inicial:

```http
GET /health
```

Status atual: projeto inicial criado apenas com estrutura base. As funcionalidades serão implementadas nas próximas etapas.

## Docker

Crie um arquivo local de ambiente a partir do exemplo:

```bash
cp .env.example .env
```

Build da imagem:

```bash
docker build -t clube-do-album-gateway-api .
```

Execucao local:

```bash
docker run --env-file .env -p 8080:8080 clube-do-album-gateway-api
```
