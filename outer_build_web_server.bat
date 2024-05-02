@echo off
cd C:\Users\Administrator\Desktop\code\smart-admin\smart-admin-web\javascript-ant-design-vue3
call npm run build:outerprod
xcopy "C:\Users\Administrator\Desktop\code\smart-admin\smart-admin-web\javascript-ant-design-vue3\dist" "C:\Users\Administrator\Desktop\code\smart-admin\smart-admin-api\sa-admin\src\main\resources\static" /E /I /Y

cd C:\Users\Administrator\Desktop\code\smart-admin\smart-admin-api

C:\Users\Administrator\.jdks\corretto-1.8.0_402\bin\java.exe -Dmaven.multiModuleProjectDirectory=C:\Users\Administrator\Desktop\code\smart-admin\smart-admin-api -Djansi.passthrough=true "-Dmaven.home=C:\Program Files\JetBrains\IntelliJ IDEA 2023.2.2\plugins\maven\lib\maven3" "-Dclassworlds.conf=C:\Program Files\JetBrains\IntelliJ IDEA 2023.2.2\plugins\maven\lib\maven3\bin\m2.conf" "-Dmaven.ext.class.path=C:\Program Files\JetBrains\IntelliJ IDEA 2023.2.2\plugins\maven\lib\maven-event-listener.jar" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2023.2.2\lib\idea_rt.jar=64928:C:\Program Files\JetBrains\IntelliJ IDEA 2023.2.2\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\JetBrains\IntelliJ IDEA 2023.2.2\plugins\maven\lib\maven3\boot\plexus-classworlds-2.7.0.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2023.2.2\plugins\maven\lib\maven3\boot\plexus-classworlds.license" org.codehaus.classworlds.Launcher -Didea.version=2023.2.2 package -P outerprod,!dev

