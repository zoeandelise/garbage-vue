@echo off
setlocal

REM 设置变量
set SERVER_URL=http://localhost:81
set API_PATH=/dev-api/data/init/points
set TOKEN=Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImFkbWluIn0.admin_token
set COUNT=30
set CLEAR=true

REM 发送请求
echo 正在初始化积分数据...
curl -X POST "%SERVER_URL%%API_PATH%?count=%COUNT%&clear=%CLEAR%" ^
  -H "Authorization: %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo 数据初始化请求已发送
pause 