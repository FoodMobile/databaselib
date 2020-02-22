package com.foodmobile.databaselib;

import com.foodmobile.databaselib.adapters.ConnectionInfo;
import com.foodmobile.databaselib.adapters.DBAdapter;
import com.foodmobile.databaselib.adapters.QueryDetails;
import com.foodmobile.databaselib.exceptions.NoAdapterOpenException;
import com.foodmobile.databaselib.models.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Shared Generic Database Adapter
 */
public class DatabaseAdapter implements DBAdapter {

    private Optional<DBAdapter> adapter;

    public static DatabaseAdapter shared;

    public <T extends DBAdapter> DatabaseAdapter(Class<T> tClass) throws Exception {
        this.adapter = Optional.of(tClass.getConstructor().newInstance());
    }

    /**
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param tClass Class type that the data fetched from the database should be serialized to
     * @param <T> Class type that the data fetched from the database should be serialized to
     * @return A list of T objects (serialized from the data received from the database)
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public <T> List<T> read(QueryDetails details, Class<T> tClass) throws Exception {
        return this.adapter.orElseThrow(NoAdapterOpenException::new).read(details,tClass);
    }

    /**
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param obj Object that should be inserted into the database.
     * @param <T> Type constraint. An object that is to be inserted must extend Entity
     * @return Number of rows (or documents) inserted
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public <T extends Entity> int create(QueryDetails details, T obj) throws Exception{
        return this.adapter.orElseThrow(NoAdapterOpenException::new).create(details,obj);
    }


    /**
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param obj Object that should be inserted into the database.
     * @param <T> Type constraint. An object that is to be inserted must extend Entity
     * @return Number of rows (or documents) affected by the update.
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public <T extends Entity> int update(QueryDetails details, T obj) throws Exception{
        return this.adapter.orElseThrow(NoAdapterOpenException::new).update(details,obj);
    }

    /**
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @return Number of rows (or documents) deleted.
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public int delete(QueryDetails details) throws Exception {
        return this.adapter.orElseThrow(NoAdapterOpenException::new).delete(details);
    }

    /**
     * @param info Connection information containing db name, db url, username, password, number of concurrent connections, etc.
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public void connect(ConnectionInfo info) throws Exception {
        this.adapter.orElseThrow(NoAdapterOpenException::new).connect(info);
    }

    /**
     * Provides the correct QueryDetails instance for the adapter in use. See adapter specific QueryClasses
     * in com.foodmobile.databaselib.adapters.
     * @param <T> Type constraint ensures the caller the returned object inherits QueryDetails.
     * @return An instance of QueryDetails (different for each adapter).
     */
    @Override
    public <T extends QueryDetails> T queryFactory() throws Exception{
        return this.adapter.orElseThrow(NoAdapterOpenException::new).queryFactory();
    }

    /**
     * Closes the connection to the database server / cluster.
     */
    @Override
    public void close() throws Exception{
        this.adapter.orElseThrow(NoAdapterOpenException::new).close();
        this.adapter = Optional.empty();
    }
}
