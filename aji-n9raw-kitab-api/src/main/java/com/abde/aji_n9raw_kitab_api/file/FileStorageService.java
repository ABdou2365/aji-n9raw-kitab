package com.abde.aji_n9raw_kitab_api.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {


    // This is the Path where the pictures will be stored at
    //  HERE WE HAVE DONE THIS BECAUSE WE MAY NEED TO CHANGE THE FILEUPLOADPATH DEEPENDING ON THE ENVIRONMENT
    @Value("${application.photos.output.path}")
    private String fileUploadPath;

    // this return string because the string is the file path of our cover
    public String saveFile(
            @NonNull MultipartFile sourceFile,
            @NonNull Integer userId) {

        //This is the subPath where each user will have his own folder where his photos going to be stored at
        final String fileUploadSubPath = "users" + File.pathSeparator + userId;
        return uploadFile(sourceFile,fileUploadSubPath);
    }

    private String uploadFile
            ( @NonNull MultipartFile sourceFile,
              @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        // Creates a File object representing the file or directory located at finalUploadPath,
        // It doesn't create the file or directory itself; it simply represents the location (path) in the file system.
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            //This is a safety check to ensure the target directory
            // where files will be uploaded exists.

            boolean folderCreated = targetFolder.mkdirs();

            //If the directory doesn’t exist, it attempts
            // to create the directory and any necessary parent
            // directories using mkdirs().

            if (!folderCreated) {

                //If the directory cannot be created for any reason
                // (such as a permission issue or a file with
                // the same name exists), the method logs a warning and
                // returns null, indicating the operation failed.

                log.warn("failed to create the target folder");
                return null;
            }
        }

        String fileExtension = getFileExtension(sourceFile);

        // this will return the path = ./uploads/users/2/6976967806680860.jpg
        String targetFilePath =  finalUploadPath + File.separator + System.currentTimeMillis() + fileExtension;

        // This converts the targetFilePath (a string) into a Path object

        Path targetPath = Paths.get(targetFilePath);

        try {
            Files.write(targetPath,sourceFile.getBytes());
            log.info("file saved to " + targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("file was not saved ",e);
        }
        return null;
    }

    private String getFileExtension(MultipartFile sourceFile) {
        if(sourceFile == null || sourceFile.isEmpty()){
            return "";
        }
        int lastDotIndex = sourceFile.getOriginalFilename().lastIndexOf(".");
        if(lastDotIndex == -1){
            return "";
        }
        return sourceFile.getOriginalFilename().substring(lastDotIndex).toLowerCase();
    }
}
