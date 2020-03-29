# Features
- Decorator based API. Throw away your boilerplate code :hooray
- Сluster bootstrap out of the box
- `morgan` for logging
- JWT tokens for security. See [security decorator](https://github.com/thiagobustamante/typescript-rest/wiki/@Security-Decorator)
- MongoDB connector
- TODO: MongoDB examples... in progress

# Initial setup
```
npm install
```

## Swagger Docs Generation

```
npm run swagger
```

# Project run
```
npm start
```

## Start project in cluster
```
npm start:cluster
```

## Docker build and run
```
npm run build
docker-compose up -d
```

## UI Test
Just go to http://127.0.0.1:3000

## Test

```
npm run test
```

### Test with coverage reports:

```
npm run test:coverage
```

The coverage report will be saved under ```./reports/coverage``` folder.

