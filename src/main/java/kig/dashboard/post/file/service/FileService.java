package kig.dashboard.post.file.service;

import kig.dashboard.post.file.exception.FileException;
import kig.dashboard.post.file.exception.FileExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.dir}")
    private String fileDir;

    public String save(MultipartFile multipartFile) {

        String filePath = fileDir + UUID.randomUUID();

        String originalName = multipartFile.getOriginalFilename();
        String fileExtension = originalName.substring(originalName.lastIndexOf(".") + 1);
        String ret = filePath + "." + fileExtension;

        try {
            multipartFile.transferTo(new File(ret));
        } catch (IOException e) {
            throw new FileException(FileExceptionType.FILE_CAN_NOT_SAVE);
        }

        return ret;
    }

    public void delete(String filePath) {
        File file = new File(filePath);

        if(!file.exists()) return;

        if(!file.delete()) {
            throw new FileException((FileExceptionType.FILE_CAN_NOT_DELETE));
        }

    }
}
