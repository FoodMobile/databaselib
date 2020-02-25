package com.foodmobile.databaselib.adapters;

import java.io.Closeable;

public interface DBAdapter extends CRUDCompliant, Closeable {
    public void connect(ConnectionInfo info) throws Exception;

    public <T extends QueryDetails> T queryFactory() throws Exception;

    public void close();
}
