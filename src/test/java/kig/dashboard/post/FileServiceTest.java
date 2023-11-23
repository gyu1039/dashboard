package kig.dashboard.post;

import kig.dashboard.post.file.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileServiceTest {

    @Autowired
    FileService fileService;

    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("test", "test.jpg", "image/jpg",
                new FileInputStream("C:\\Users\\Administrator\\Desktop\\tmp\\test.jpg"));
    }

    @Test
    public void 파일저장() throws IOException {

        String save = fileService.save(getMockUploadFile());

        File file = new File(save);
        assertThat(file.exists()).isTrue();

        file.delete();
    }

    @Test
    public void 파일삭제() throws IOException {

        String save = fileService.save(getMockUploadFile());
        fileService.delete(save);

        File file = new File(save);
        assertThat(file.exists()).isFalse();
    }
}