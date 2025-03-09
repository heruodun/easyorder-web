@echo off
rem 修复JAVA_HOME路径
set "JAVA_HOME=C:\Program Files\Java\jdk-1.8"
set JAR2=E:\printserver\sa-admin-outerprod-3.0.0.jar

rem 切换到JAR包所在目录（关键！）
cd /d "E:\printserver"

rem 异步启动并静默运行（窗口一闪而过）
start "" /B "%JAVA_HOME%\bin\javaw" -Xms2G -Xmx2G -jar "%JAR2%"

exit
