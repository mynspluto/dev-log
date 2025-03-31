### mongod
- 서버
- 사용예시
  - `& "C:\Program Files\MongoDB\Server\8.0\bin\mongod.exe" --replSet rs0 --dbpath="c:\data\db" --port 27017`
  - watch 사용을 위해 시작시 `--replSet rs0` 옵션 필요

### mongosh
- 클라이언트
- 사용예시
  - `& "C:\Program Files\MongoDB\mongosh-2.4.2-win32-x64\bin\mongosh.exe" --port 27017`
  - 접속 후 watch 사용을 위해
    ```sh
    rs.initiate({
      _id: "rs0",
      members: [
        { _id: 0, host: "localhost:27017" }
      ]
    })
    ```
