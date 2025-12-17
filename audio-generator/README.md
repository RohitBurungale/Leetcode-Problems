# Video → Audio Generator

Simple Java + HTML project that accepts a video upload and returns an MP3 audio extract using `ffmpeg`.

Structure

```
audio-generator/
 ├── src/
 │    └── main/
 │         ├── java/
 │         │     └── com/example/AudioServlet.java
 │         └── webapp/
 │               ├── index.html
 │               ├── style.css
 │               └── script.js
 ├── pom.xml
 └── ffmpeg.exe (place in project root or add to PATH)
```

Requirements

- Java 11+
- Maven
- `ffmpeg` binary (place `ffmpeg.exe` in project root or add `ffmpeg` to PATH)

Build & Run (development)

1. Open a PowerShell in the project folder (where `pom.xml` is):

```powershell
cd "d:\Project\New folder\audio-generator"
mvn jetty:run
```

2. Open `http://localhost:8080` in your browser and use the web form.

Notes

- The servlet reads `FFMPEG_PATH` environment variable if set. Otherwise it will try `ffmpeg.exe` in the project root or `ffmpeg` on PATH.
- If deploying to Tomcat/other container, build with `mvn package` and deploy the generated WAR.
