@echo off
echo ======================================
echo   垃圾分类指南数据初始化脚本
echo ======================================
echo.

rem 检查MongoDB是否安装
where mongo >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到MongoDB客户端工具。请确保已安装MongoDB并将其添加到系统PATH中。
    echo 请从https://www.mongodb.com/try/download/community下载并安装MongoDB。
    goto :end
)

echo 开始初始化垃圾分类指南数据...
echo.

rem 执行MongoDB脚本
mongo localhost:27017/garbage_classification init_garbage_guide_data.js

echo.
echo 垃圾分类指南数据初始化完成。
echo.

:end
pause 