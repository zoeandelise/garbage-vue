@echo off
echo ======================================
echo   MongoDBͼƬURL�޸��ű�
echo ======================================
echo.

rem ���MongoDB�Ƿ�װ
where mongod >nul 2>nul
if %errorlevel% neq 0 (
    echo ����: δ�ҵ�MongoDB�ͻ��˹��ߡ���ȷ���Ѱ�װMongoDB���������ӵ�ϵͳPATH�С�
    echo ���https://www.mongodb.com/try/download/community���ز���װMongoDB��
    goto :end
)

echo ��ʼ�޸�MongoDB�е�ͼƬURL...
echo.
echo ע��: �˽ű����޸�����Ͷ�ݼ�¼�е�ͼƬURL��ʽ�������ȱ������ݿ⡣
echo.

set /p confirm=�Ƿ����ִ���޸�����? (Y/N): 
if /i "%confirm%" neq "Y" goto :end

rem ִ��MongoDB�ű�
mongod localhost:27017/garbage_classification fix_image_urls.js

echo.
echo ͼƬURL�޸���ɡ�
echo.

:end
pause 