package com.api.eshop.service.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStoragePath;
    private final String fileStorageLocation;

    public FileStorageService(@Value("${file.storage.location:temp}") String fileStorageLocation) {
        this.fileStorageLocation = fileStorageLocation;
        fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Issue in creating file directory",e);
        }
    }

    public String storeFile(MultipartFile file ) {

        //create owner directory if does not exist
        File f = new File(fileStorageLocation);
        if(!f.exists())
        {
            f.mkdir();
        }


        //create today date folder if does not exist
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String today = dateFormat.format(date);

        File ff = new File(fileStorageLocation+today);
        if(!ff.exists())
        {
            ff.mkdir();
        }


        Path path = Paths.get(fileStorageLocation);

        String fileName=StringUtils.cleanPath(file.getOriginalFilename());


        String fileUUID = UUID.randomUUID().toString();
        Path filePath = Paths.get(fileStoragePath+"/"+today+"/"+fileUUID+"__________"+fileName);

        try {
            Files.copy(file.getInputStream() , filePath , StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Issue in storing the file",e);
        }
        return fileUUID+"__________"+fileName;
    }

    public Resource downloadFile(String fileName , String owner , String date)
    {

        Path path =  Paths.get(fileStorageLocation+"\\"+owner+"\\"+date).toAbsolutePath().resolve(fileName);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Issue in Reading the file" , e);
        }
        if (resource.exists() && resource.isReadable()){
            return  resource;
        } else
        {
            throw new RuntimeException("the file does not exist or not readable");
        }

    }
}
