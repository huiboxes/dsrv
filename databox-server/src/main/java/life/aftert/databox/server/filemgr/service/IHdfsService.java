package life.aftert.databox.server.filemgr.service;

import java.io.IOException;
import java.io.InputStream;

public interface IHdfsService {

    void saveFile(String dir, String name,
                  InputStream input, long length, short replication) throws IOException;

    void deleteFile(String dir, String name) throws IOException;

    InputStream openFile(String dir, String name) throws IOException;

    void mikDir(String dir) throws IOException;

    void deleteDir(String dir) throws IOException;

}
