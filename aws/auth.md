# AccessKey + Secret Key
IAM 사용자에서 발급 후 ~/.aws/credentials에 저장, 가장 간단하고 빠르지만 보안에 취약

# aws sso login
aws sso login --profile corp-dev 명령어 사용3ㅂ
웹 브라우저가 열리며 SSO 로그인 화면으로 이동
프로필 정보는 ~/aws/config에 저장
```ini
[profile corp-dev]
sso_start_url = https://my-org.awsapps.com/start
sso_region = ap-northeast-2
sso_account_id = 123456789012
sso_role_name = DeveloperAccess
region = ap-northeast-2

```