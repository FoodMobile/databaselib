package com.foodmobile.databaselib.adapters;

public interface DBAdapter extends CRUDCompliant{
    public void connect(ConnectionInfo info) throws Exception;

    public <T extends QueryDetails> T queryFactory() throws Exception;

    public void close() throws Exception;
}
