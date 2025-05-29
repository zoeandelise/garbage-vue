#!/bin/bash

# 设置变量
SERVER_URL="http://localhost:81"
API_PATH="/dev-api/data/init/points"
TOKEN="Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImFkbWluIn0.admin_token" # 替换为实际的token
COUNT=30
CLEAR=true

# 发送请求
echo "正在初始化积分数据..."
curl -X POST "${SERVER_URL}${API_PATH}?count=${COUNT}&clear=${CLEAR}" \
  -H "Authorization: ${TOKEN}" \
  -H "Content-Type: application/json"

echo -e "\n数据初始化请求已发送" 