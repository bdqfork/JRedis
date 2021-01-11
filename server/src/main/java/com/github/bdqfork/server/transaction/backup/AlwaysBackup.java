package com.github.bdqfork.server.transaction.backup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.github.bdqfork.core.exception.SerializeException;
import com.github.bdqfork.server.transaction.TransactionLog;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class AlwaysBackup extends AbstractBackupStrategy {
    protected static int head = 0x86;

    public AlwaysBackup() {
        super(null);
    }

    public AlwaysBackup(String logFilePath) {
        super(logFilePath, null);
    }

    @Override
    public void backup(TransactionLog transactionLog) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(getLogFilePath()), true)) {
            byte[] data = getSerializer().serialize(transactionLog);
            // TODO: 序列化
            fileOutputStream.flush();
        } catch (IOException | SerializeException e) {
            throw new IllegalStateException(e);
        }
    }

}
