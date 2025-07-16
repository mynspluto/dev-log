# 빌드 아티팩트 캐싱
## upload-artifact + 다음 워크 플로우에서 download-artifact
build.yml: 빌드만 하고 jar 저장
deploy.yml: build.yml에서 만든 결과물을 다운로드해서 배포 수행

build.yml
```yml
- name: Build JAR
  run: ./gradlew build

- name: Upload artifact
  uses: actions/upload-artifact@v3
  with:
    name: app
    path: build/libs/app.jar

```

```yml
- name: Download artifact
  uses: actions/download-artifact@v3
  with:
    name: app

- name: Deploy with Argo Rollouts
  run: |
    # app.jar 이용해 Docker 이미지 만들거나 직접 배포

```

아티팩트를 재사용해 배포 시간 단축
워크플로우 분리 시 동기화 잘 관리해야 함

## Docker Buildx + GitHub Actions Cache (이미지 레이어 캐시)
Docker 이미지 자체를 다시 빌드할 때도 이전 레이어를 재사용해서 빌드 시간/배포 시간을 줄이는 방법
이미지를 잘게 잘라서 레이어 단위로 캐싱하며 저장소(github actions cache, docker registry 등)에 저장/복원
GitHub Actions + Docker + Buildx + ArgoCD 기반 배포 환경을 구성할 때,
빌드 결과는 Docker Hub 등 외부에 저장하고 캐시는 GitHub Actions 내에 저장하는 구조가 일반적

build.yml
```yml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3

- name: Cache Docker layers
  uses: actions/cache@v3
  with:
    path: /tmp/.buildx-cache
    key: docker-${{ github.sha }}
    restore-keys: |
      docker-

- name: Build and push Docker image
  uses: docker/build-push-action@v5
  with:
    context: .
    push: true
    tags: your-registry/image:tag
    cache-from: type=local,src=/tmp/.buildx-cache
    cache-to: type=local,dest=/tmp/.buildx-cache

```