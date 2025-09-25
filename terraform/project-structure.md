# versions.tf or terraform.tf
Terraform 버전과 Provider 요구사항 정의
```py
terraform {
  required_version = ">= 1.0"

  required_providers {
    # 클라우드 리소스 관리
    aws = {
      source = "hasicorp/aws"
      version = "~> 5.0"
    }
    # 유니크한 값 생성(random_id, random_password 등 리소스 활용 가능)
    random = {
      source = "hashicorp/random"
      version = "~> 3.1"
    }
  }

  # Remote backend 설정(선택사항) => Terraform State 파일을 AWS S3에 원격 저장하는 설정
  backend "s3" {
    bucket  = "my-terraform-state"
    key     = "prod/terraform.tfstate"
    region  = "ap-northeast-2"
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Enviroment  = var.enviroment
      Project     = var.project_name
    }
  }
}
```
# vatiables.tf
역할: 입력 변수 선언
```py
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-northease-2"
}

variable "enviroment" {
  description = "Enviroment name"
  type        = string
  validation {
    condition = contains(["dev", "staging", "prod"], var.enviroment)
    error_message = "Enviroment must be dev, staging, or prod."
  }
}
# 값 할당시 var 생략
# terraform.tfvars, *.auto.tfvars
# enviroment = "dev" var. 없이 변수명만

# 값 참조시 var 필요
# variables.tf, main.tf, outputs.tf 등
#resource "aws_instance" "web" {
#  instance_type = var.enviroment # var 필요
#}


variable "instance_count" {
  description = "number of instances"
  type        = number
  default     = 1
}

variable "instance_types" {
  description = "List of instance types"
  type        = list(string)
  default     = ["t3.micro", "t3.small"]
}

variable "tags" {
  description = "Common tags"
  type        = map(string)
  default     = {}
}
```
# main.tf
주요 리소스 정의
```py
# VPC 생성
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostname = true
  enable_dns_support = true

  tags = merge(var.tags, {
    Name = "${var.enviroment}-vpc"
  })
}

# 서브넷 생성
resource "aws_subnet" "public" { # aws_subnet은 키워드(terraform.tf의 Provider가 제공) public은 사용자가 지정한 이름
  count = length(var.availability_zones)

  vpc_id      = aws_vpc.main.id
  cidr_block  = "10.0.${count.index + 1}.0/24"
  availability_zone = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = merge(var.tags, {
    Name = "${var.enviroment}-public-subnet-${count.index + 1}"
    Type = "Public"
  })
}

# 보안 그룹
resource "aws_security_group" "web" {
  name_prefix = "${var.enviroment}-web-"
  vpc_id    = aws_vpc.main.id

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags
}
```

# output.tf
역할: 출력 값 정의(다른 모듈이나 사용자가 참조)

```py
output "vpc_id" {
  description = "VPC ID"
  value = aws_vpc.main.id
}

output "vpc_cidr_block" {
  description = "VPC CIDR block"
}

output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value = aws_subnet.public[*].id
}

output "security_group_id" {
  description = "Security group ID"
  value = aws_security_group.web.id
}

output "region" {
  description = "AWS region"
  value = var.aws_region
}
```

# terraform.tfvars
역할: 변수 값 설정(실제 값)
staging/prod 환경별로 tfvars 파일 가져서 구분
```py
aws_region   = "ap-northeast-2"
environment  = "prod"
project_name = "my-web-app"

instance_count = 3
instance_types = ["t3.small", "t3.medium"]

availability_zones = [
  "ap-northeast-2a",
  "ap-northeast-2b",
  "ap-northeast-2c"
]

tags = {
  Owner       = "DevOps Team"
  Project     = "Web Application"
  Environment = "Production"
}
```

# 배포(프로비저닝)
```bash
# 개발환경 배포
terraform apply -var-file="dev.tfvars"

# 스테이징환경 배포  
terraform apply -var-file="staging.tfvars"

# 운영환경 배포
terraform apply -var-file="prod.tfvars"
```

# 명령어
terraform init
역할: 준비 작업
Provider 다운로드
Backend 설정
모듈 다운로드
실제 리소스 생성 안 함

terraform plan
역할: 시뮬레이션
어떤 리소스를 생성/수정/삭제할지 미리 보여줌
실제 리소스 생성 안 함 (미리보기만)

terraform apply
역할: 실제 인프라 생성
AWS/GCP/Azure 등에 실제 리소스 생성
진짜 비용 발생!
실제 서버, 데이터베이스, 네트워크 등이 만들어짐

# 클라우드 계정 연결
- aws sso cli로 가능 <= 가장 권장
  - aws내에서 사용자 계정 만들어주고 그 계정에 staging/prod 계정에 대한 권한을 부여
    즉 여러 aws 계정의 권한을 하나의 사용자 계정에 부여하여 한 번의 로그인으로 여러 권한을 가지게 됨
    한 번의 로그인으로 여러 aws 계정에 접근 가능
- provider에 직접 토큰 값 넣기
  - ```py
    # versions.tf
    provider "aws" {
      region     = "ap-northeast-2"
      access_key = "ASIAQ3EGLKJASLKD"       # 임시 액세스 키
      secret_key = "abc123def456..."        # 임시 시크릿 키  
      token      = "IQoJb3JpZ2luX2VjEMn..." # 세션 토큰 (필수!)
    }
    ```

# network.tf
VPC, 서브넷, 라우팅
# security.tf
보안 그룹, NACL
# compute.tf
EC2, ASG, ELB
# storage.tf
S3, EBS
# database.tf
RDS, DynamoDB
# outputs.tf
# terraform.tfvars