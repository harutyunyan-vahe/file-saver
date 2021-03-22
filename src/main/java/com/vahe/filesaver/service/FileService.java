package com.vahe.filesaver.service;


import com.vahe.filesaver.model.FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileService {


    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    public FileResponse uploadFile(MultipartFile file) {
        try {
            String hash = DigestUtils.md5DigestAsHex(file.getBytes());
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            Path copyLocation = Paths.get(uploadDir + File.separator + hash + "." + extension);

            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            FileResponse fileResponse = new FileResponse();

            fileResponse.setRef(hash);
            fileResponse.setSizeBytes(Files.size(copyLocation));


            fileResponse.setContentType(getMimeType(copyLocation));

            return fileResponse;

        } catch (Exception e) {
            log.error("error creating file" + file.getOriginalFilename(), e);
            throw new IllegalStateException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
        }
    }

    private String getMimeType(Path file) {
        try {
            Tika tika = new Tika();
            return tika.detect(file);
        } catch (IOException e) {
            log.error("count not detect mime type for " + file, e);
            return "";
        }
    }

    public Path getFileFor(String fileName) {
        return Paths.get(uploadDir + File.separator + StringUtils.cleanPath(fileName));
    }


    public void deleteFile(String fileName) {
        Path filePath = getFileFor(fileName);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("delete error", e);
        }
    }
}
