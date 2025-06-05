#!/bin/bash

echo "======================================"
echo "  垃圾分类指南数据初始化脚本"
echo "======================================"
echo ""

# 检查MongoDB是否安装
if ! command -v mongo &> /dev/null; then
    echo "错误: 未找到MongoDB客户端工具。请确保已安装MongoDB并将其添加到系统PATH中。"
    echo "请从https://www.mongodb.com/try/download/community下载并安装MongoDB。"
    exit 1
fi

echo "开始初始化垃圾分类指南数据..."
echo ""

# 执行MongoDB脚本
mongo localhost:27017/garbage_classification init_garbage_guide_data.js

echo ""
echo "数据初始化完成。"
echo "" 