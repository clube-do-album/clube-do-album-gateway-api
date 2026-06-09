# Clube do Album Gateway API

API de entrada da plataforma Clube do Album.

## Responsabilidade

- Centralizar chamadas do frontend.
- Encaminhar requisicoes para APIs internas.
- Manter uma unica base URL local para o frontend.
- Validar JWT nas rotas protegidas.
- Repassar usuario autenticado para servicos internos via headers.

O token JWT deve ser emitido pela Identity API e enviado no header `Authorization: Bearer <token>`.
Quando o token e valido, o gateway encaminha `X-User-Id` e `X-User-Email` para os servicos internos.

## Tecnologias usadas

- Java 17
- Spring Boot
- Maven

## Variaveis de ambiente

Crie um arquivo local a partir do exemplo:

```bash
cp .env.example .env
```

Variaveis esperadas:

```env
SERVER_PORT=3000

IDENTITY_API_URL=http://localhost:8081
CATALOG_API_URL=http://localhost:3001
RATINGS_API_URL=http://localhost:8082
RANKING_API_URL=http://localhost:3002
FEED_API_URL=http://localhost:3003
SOCIAL_API_URL=http://localhost:3004
CORS_ALLOWED_ORIGINS=http://localhost:5173
JWT_SECRET=clube-do-album-local-development-secret-key-change-me
```

Quando rodar em container na network Docker, use os nomes dos containers:

```env
IDENTITY_API_URL=http://clube-do-album-identity-api:8081
CATALOG_API_URL=http://clube-do-album-catalog-api:3001
RATINGS_API_URL=http://clube-do-album-ratings-api:8082
RANKING_API_URL=http://clube-do-album-ranking-worker:3002
FEED_API_URL=http://clube-do-album-feed-worker:3003
SOCIAL_API_URL=http://clube-do-album-social-api:3004
JWT_SECRET=clube-do-album-local-development-secret-key-change-me
```

## Como rodar localmente

```bash
mvn spring-boot:run
```

Base URL:

```text
http://localhost:3000
```

## Rotas

Rotas publicas:

```http
GET /health
POST /auth/login
POST /users
GET /albums
GET /albums/{id}
GET /albums/search?query=abbey%20road
GET /rankings
GET /rankings/{albumId}
GET /feed
GET /feed/albums/{albumId}
GET /ratings/albums/{albumId}
GET /ratings/users/{userId}/public
GET /follows/following
GET /follows/followers
```

As demais rotas exigem `Authorization: Bearer <token>`.

### Health

```http
GET /health
```

### Identity

```http
POST /auth/login
POST /users
GET /users
GET /users/{id}
```

### Catalog

```http
GET /albums
GET /albums/{id}
GET /albums/search?query=abbey%20road
POST /albums/import
```

### Ratings

```http
POST /ratings
GET /ratings/albums/{albumId}
GET /ratings/users/{userId}
GET /ratings/users/{userId}/public
```

### Ranking

```http
GET /rankings
GET /rankings/{albumId}
```

### Feed

```http
GET /feed
GET /feed/users/{userId}
GET /feed/albums/{albumId}
```

### Social

```http
POST /follows/{userId}
DELETE /follows/{userId}
GET /follows/following
GET /follows/followers
```

## Docker

Build da imagem:

```bash
docker build -t clube-do-album-gateway-api .
```

Execucao em container na network local:

```bash
docker run -d --name clube-do-album-gateway-api \
  --network clube-do-album-network \
  -e SERVER_PORT=3000 \
  -e IDENTITY_API_URL=http://clube-do-album-identity-api:8081 \
  -e CATALOG_API_URL=http://clube-do-album-catalog-api:3001 \
  -e RATINGS_API_URL=http://clube-do-album-ratings-api:8082 \
  -e RANKING_API_URL=http://clube-do-album-ranking-worker:3002 \
  -e FEED_API_URL=http://clube-do-album-feed-worker:3003 \
  -e SOCIAL_API_URL=http://clube-do-album-social-api:3004 \
  -e JWT_SECRET=clube-do-album-local-development-secret-key-change-me \
  -p 3000:3000 \
  clube-do-album-gateway-api
```

## Status atual

Gateway criado para centralizar chamadas para Identity, Catalog, Ratings, Ranking, Feed e Social, com validacao JWT nas rotas protegidas.
