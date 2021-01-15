package com.github.bdqfork.server.transaction.backup;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.github.bdqfork.server.transaction.RedoLog;
import com.github.bdqfork.server.transaction.TransactionLog;

/**
 * @author bdq
 * @since 2020/09/22
 */
public class AlwaysBackup extends AbstractBackupStrategy {

    public AlwaysBackup() {
        super(null);
    }

    public AlwaysBackup(String logFilePath) {
        super(logFilePath, null);
    }

    @Override
    public void backup(TransactionLog transactionLog) {
        try (OutputStream outputStream = new FileOutputStream(new File(getLogFilePath()), true);
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

            List<RedoLog> redoLogs = transactionLog.getRedoLogs();
            for (RedoLog redoLog : redoLogs) {
                byte[] data = getSerializer().serialize(redoLog);
                dataOutputStream.writeByte(HEAD);
                dataOutputStream.writeByte(VERSION);
                dataOutputStream.writeInt(data.length);
                dataOutputStream.write(data);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
