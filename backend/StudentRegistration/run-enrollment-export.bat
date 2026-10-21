
@echo off
cd /d "C:\Users\HP\OneDrive\Desktop\File_Net Training\StudentRegistration"

set "JAVA_EXE=C:\STS\sts-5.0.1.RELEASE\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_25.0.1.v20251108-1451\jre\bin\java.exe"

REM Configure these using your own values.
set "JWT_SECRET=4F31XfhzDFH1gWiW9ISxrwB6kJkqFGMpAOJwDE49vz0="
set "DB_PASSWORD=Sadhana@1325"

"%JAVA_EXE%" -jar "target\student-registration-system-0.0.1-SNAPSHOT.jar" --run-enrollment-export=true

echo.
echo Batch export finished. Check the output above for success or errors.
pause
