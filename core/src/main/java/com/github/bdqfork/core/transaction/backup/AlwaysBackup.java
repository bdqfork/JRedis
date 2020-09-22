package com.github.bdqfork.core.transaction.backup;

import com.github.bdqfork.core.transaction.TransactionLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    protected void doBackup() throws IOException {
        File file = new File(getLogFilePath());
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        Queue<TransactionLog> transactionLogs = getTransactionLogs();
        while (!transactionLogs.isEmpty()) {
            TransactionLog transactionLog = transactionLogs.poll();
            fileOutputStream.write("begin".getBytes());
            // todo: 序列化TransactionLog，求出size并写入磁盘，然后将二进制数据写入磁盘
        }
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
