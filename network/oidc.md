# OIDC (OpenID Connect)
OIDC는 직접 비밀번호를 확인하지 않고, 신뢰하는 제3자가 이미 확인했다는 증명서를 받는 방식
마치 학교에서 발급한 졸업증명서를 회사가 믿고 채용하는 것과 같은 개념

# Github Actions + AWS OIDC 인증
1. AWS에 Github OIDC Provider 등록
```bash
# GitHub OIDC Provider 등록
aws iam create-open-id-connect-provider \
    --url https://token.actions.githubusercontent.com \
    --client-id-list sts.amazonaws.com \
    --thumbprint-list 6938fd4d98bab03faadb97b34396831e3780aea1
```

2. IAM 역할 생성
```json
// trust-policy.json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::123456789012:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:myorg/myrepo:*"
        }
      }
    }
  ]
}
```

```bash
# IAM 역할 생성
aws iam create-role \
    --role-name github-actions-role \
    --assume-role-policy-document file://trust-policy.json

# EC2 접근 권한 추가
aws iam attach-role-policy \
    --role-name github-actions-role \
    --policy-arn arn:aws:iam::aws:policy/AmazonEC2FullAccess
```

3. Github Actions 워크플로우

```yml
name: Deploy to AWS
on:
  push:
    branches: [main]

permissions:
  id-token: write  # OIDC 토큰 생성 권한
  contents: read

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: arn:aws:iam::123456789012:role/github-actions-role
          aws-region: ap-northeast-2
          
      - name: Connect to EC2 via SSH
        run: |
          # EC2 인스턴스 정보 조회
          INSTANCE_IP=$(aws ec2 describe-instances \
            --filters "Name=tag:Name,Values=my-server" \
            --query "Reservations[0].Instances[0].PublicIpAddress" \
            --output text)
          
          # SSH 접속 (예: 배포 스크립트 실행)
          aws ssm send-command \
            --instance-ids i-1234567890abcdef0 \
            --document-name "AWS-RunShellScript" \
            --parameters 'commands=["echo Hello from GitHub Actions"]'
```

## 실제 OIDC 흐름
Step 1: GitHub Actions 실행
Step 2: GitHub이 OIDC 토큰 발급
```bash
# GitHub Actions 런타임에서 자동 생성되는 토큰
{
  "iss": "https://token.actions.githubusercontent.com",
  "sub": "repo:myorg/myrepo:ref:refs/heads/main",
  "aud": "sts.amazonaws.com",
  "repository": "myorg/myrepo",
  "repository_owner": "myorg",
  "run_id": "1234567890",
  "ref": "refs/heads/main",
  "sha": "abc123def456",
  "workflow": "Deploy to AWS",
  "actor": "developer-name",
  "exp": 1705123456,
  "iat": 1705037056
}
```
Step 3: AWS Configure Credentials Action
Step 4: AWS STS에 토큰 전송
Step 5: AWS STS 검증 과정
```bash
1. "이 토큰이 GitHub에서 온 게 맞나?"
   → issuer가 "https://token.actions.githubusercontent.com"인지 확인 ✅

2. "GitHub의 공개키로 서명 검증"
   → curl https://token.actions.githubusercontent.com/.well-known/jwks ✅

3. "신뢰 정책 조건 확인"
   → sub가 "repo:myorg/myrepo:*" 패턴과 일치하는지 확인 ✅
   → aud가 "sts.amazonaws.com"인지 확인 ✅

4. "토큰이 만료되지 않았나?"
   → exp 시간 확인 ✅
```
Step 6: 임시 자격증명 발급
Step 7: AWS 리소스 접근