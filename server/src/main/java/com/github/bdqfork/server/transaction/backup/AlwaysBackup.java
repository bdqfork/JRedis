package com.github.bdqfork.server.transaction.backup;

import com.github.bdqfork.core.exception.SerializeException;
import com.github.bdqfork.server.transaction.TransactionLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class AlwaysBackup extends AbstractBackupStrategy {

    public AlwaysBackup() {
        super(new LinkedList<>());
    }

    public AlwaysBackup(String logFilePath) {
        super(logFilePath, new LinkedList<>());
    }

    @Override
    protected void doBackup() {
        File file = new File(getLogFilePath());
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file, true);
            Queue<TransactionLog> transactionLogs = getTransactionLogs();
            while (!transactionLogs.isEmpty()) {
                TransactionLog transactionLog = transactionLogs.poll();

                fileOutputStream.write("begin".getBytes(StandardCharsets.UTF_8));

                byte[] data = getSerializer().serialize(transactionLog);
                int size = data.length;
                fileOutputStream.write(size);
                fileOutputStream.write(data);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (SerializeException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
