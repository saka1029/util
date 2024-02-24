package saka1029.util.main;

import java.io.File;
import org.junit.Test;

public class TestDrive {
    @Test
    public void testSmb() {
        File path = new File("/run/user/1000/gvfs/smb-share:server=minipc,share=g/");
        for (File child : path.listFiles())
            System.out.println(child);
    }
    
}
