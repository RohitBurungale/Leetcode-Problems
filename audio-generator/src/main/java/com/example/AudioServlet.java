package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "AudioServlet", urlPatterns = "/convert")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024L * 1024L * 1024L, // 1GB
        maxRequestSize = 1024L * 1024L * 1024L) // 1GB
public class AudioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part videoPart;
        try {
            videoPart = req.getPart("video");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'video' part in multipart request");
            return;
        }

        String submittedName = getSubmittedFileName(videoPart);
        if (submittedName == null || submittedName.isEmpty()) {
            submittedName = "upload";
        }

        File inputFile = File.createTempFile("upload-", "-" + submittedName);
        try (InputStream in = videoPart.getInputStream(); OutputStream out = new FileOutputStream(inputFile)) {
            in.transferTo(out);
        }

        // Locate ffmpeg: prefer FFMPEG_PATH env var, then file named ffmpeg(.exe) in app dir, then 'ffmpeg' on PATH
        String ffmpegPath = System.getenv("FFMPEG_PATH");
        if (ffmpegPath == null || ffmpegPath.isEmpty()) {
            File maybe = new File(System.getProperty("user.dir"), "ffmpeg.exe");
            if (maybe.exists() && maybe.canExecute()) {
                ffmpegPath = maybe.getAbsolutePath();
            } else {
                // fallback to 'ffmpeg' which might be on PATH
                ffmpegPath = "ffmpeg";
            }
        }

        File outputFile = File.createTempFile("audio-", ".mp3");

        List<String> command = new ArrayList<>(Arrays.asList(
                ffmpegPath,
                "-y",
                "-i",
                inputFile.getAbsolutePath(),
                "-vn",
                "-acodec",
                "libmp3lame",
                "-q:a",
                "2",
                outputFile.getAbsolutePath()
        ));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p;
        StringBuilder processLog = new StringBuilder();
        try {
            p = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processLog.append(line).append('\n');
                }
            }
            int exit = p.waitFor();
            if (exit != 0 || !outputFile.exists() || outputFile.length() == 0) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("text/plain; charset=UTF-8");
                try (PrintWriter pw = resp.getWriter()) {
                    pw.println("FFmpeg failed to convert the file. Exit code: " + exit);
                    pw.println("FFmpeg log:");
                    pw.println(processLog.toString());
                }
                return;
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain; charset=UTF-8");
            try (PrintWriter pw = resp.getWriter()) {
                pw.println("Error executing ffmpeg: " + e.getMessage());
                pw.println("Process log (if any):");
                pw.println(processLog.toString());
            }
            return;
        } finally {
            // attempt to delete uploaded input file
            try { Files.deleteIfExists(inputFile.toPath()); } catch (Exception ignore) {}
        }

        // Stream the resulting audio file back to client
        resp.setContentType("audio/mpeg");
        String outName = submittedName.replaceAll("\\.[^.]+$", "") + ".mp3";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + outName + "\"");
        resp.setContentLengthLong(outputFile.length());

        try (InputStream fis = new FileInputStream(outputFile); OutputStream os = resp.getOutputStream()) {
            fis.transferTo(os);
            os.flush();
        } finally {
            try { Files.deleteIfExists(outputFile.toPath()); } catch (Exception ignore) {}
        }
    }

    private String getSubmittedFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd == null) return null;
        for (String token : cd.split(";")) {
            token = token.trim();
            if (token.startsWith("filename")) {
                String[] kv = token.split("=", 2);
                if (kv.length == 2) {
                    String name = kv[1].trim();
                    if (name.startsWith("\"") && name.endsWith("\"")) {
                        name = name.substring(1, name.length() - 1);
                    }
                    return name;
                }
            }
        }
        return null;
    }
}
