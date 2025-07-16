
# Docker 멀티스테이지 빌드
Docker 멀티스테이지 빌드는 여러 단계의 빌드 과정을 한 Dockerfile 안에서 정의해서, 최종 이미지에는 필요한 파일과 결과물만 포함시켜 이미지 크기를 크게 줄이는 기법입니다.

```yml
# 1단계: 빌드
FROM node:18-alpine AS build

WORKDIR /app
COPY package.json yarn.lock ./
RUN yarn install --frozen-lockfile

COPY . .
RUN yarn build

# 2단계: 실행
FROM node:18-alpine

WORKDIR /app
COPY --from=build /app/dist ./dist
COPY --from=build /app/node_modules ./node_modules
COPY package.json ./

CMD ["node", "dist/index.js"]
```
첫 번째 단계에서 빌드에 필요한 패키지를 설치하고 소스 코드를 빌드
두 번째 단계는 경량 이미지(node:18-alpine)를 사용하고, 빌드 결과물과 최소 파일만 복사
from을 여러 번 사용하면 마지막 from으로 정의된 스테이지만 최종 이미지가 됨