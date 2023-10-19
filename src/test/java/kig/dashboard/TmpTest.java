package kig.dashboard;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TmpTest {

    @Test
    public void 파일타입확인() throws IOException {

        File file = new File("/tmp" + File.separator + "text.txt");
        System.out.println(file.toPath());
    }
}
