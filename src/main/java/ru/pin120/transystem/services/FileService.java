package ru.pin120.transystem.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {

    private final List<String> extensionsImages = Arrays.asList("png", "bmp", "jpg", "jpeg");

    public byte[] convertFile2Bytes(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        return bytes;
    }

    public boolean fileIsImage(MultipartFile file){
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);

        if(!extensionsImages.contains(fileExtension)){
            return false;
        }

        return true;
    }

}
