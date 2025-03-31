### 서버 서비스 등록 확인
Get-Service MySQL80

### 수동 on/off
net start MySQL80 or Start-Service MySQL80
net stop MySQL80 or Stop-Service MySQL80

### 클라이언트(bin/mysql) Path 등록 
$envPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
$newPath = $envPath + ";C:\Program Files\MySQL\MySQL Server 8.0\bin"
[System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")

### 접속
mysql -u root -p