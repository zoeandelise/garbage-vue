@echo off
echo 垃圾投递记录图片迁移工具
echo ============================
echo 此工具将把Base64格式的图片数据转换为文件存储
echo.

REM 设置MongoDB路径，请根据实际情况修改
set MONGO_PATH=C:\Program Files\MongoDB\Server\5.0\bin

REM 设置MongoDB连接信息，请根据实际情况修改
set MONGO_HOST=localhost
set MONGO_PORT=27017
set MONGO_DB=ruoyi_vue

echo 正在检查MongoDB连接...
"%MONGO_PATH%\mongo.exe" --host %MONGO_HOST% --port %MONGO_PORT% --eval "db.version()" %MONGO_DB%
if %ERRORLEVEL% NEQ 0 (
    echo MongoDB连接失败，请检查MongoDB是否已启动或连接信息是否正确
    goto :end
)

echo.
echo 警告：执行迁移前，请确保已备份数据库！
echo.
set /p CONFIRM=是否已备份数据库并继续执行？(y/n): 

if /i "%CONFIRM%" NEQ "y" (
    echo 操作已取消
    goto :end
)

echo.
echo 开始执行迁移...
echo.

REM 执行MongoDB脚本
"%MONGO_PATH%\mongo.exe" --host %MONGO_HOST% --port %MONGO_PORT% %MONGO_DB% migrate_base64_to_files.js

echo.
echo 迁移完成
echo.
echo 请检查上述输出，确认迁移是否成功

:end
pause 