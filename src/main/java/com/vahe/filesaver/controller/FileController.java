package com.vahe.filesaver.controller;

import com.vahe.filesaver.model.FileResponse;
import com.vahe.filesaver.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {


    private final FileService fileService;


    @PostMapping
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {

        return fileService.uploadFile(file);
    }


    @GetMapping(value = "/{file_name}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFile(@PathVariable("file_name") String fileName, HttpServletResponse response) {
        try {

            Path filePath = this.fileService.getFileFor(fileName);
            Files.copy(filePath, response.getOutputStream());

            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLength((int) Files.size(filePath));
            response.flushBuffer();
        } catch (IOException ex) {

            throw new RuntimeException("IOError writing file to output stream");
        }

    }

    @DeleteMapping(value = "/{file_name}")
    public void deleteFile(@PathVariable("file_name") String fileName) {
        this.fileService.deleteFile(fileName);
    }

}
