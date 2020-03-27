package com.foodmobile.databaselib;

import com.foodmobile.databaselib.adapters.ConnectionInfo;
import com.foodmobile.databaselib.adapters.DBAdapter;
import com.foodmobile.databaselib.adapters.MongoDBAdapter;
import com.foodmobile.databaselib.adapters.QueryDetails;
import com.foodmobile.databaselib.exceptions.NoAdapterOpenException;
import com.foodmobile.databaselib.models.Entity;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Shared Generic Database Adapter
 */
public class DatabaseAdapter implements DBAdapter {

    private Optional<DBAdapter> adapter;

    /**
     * Shared reference to the database adapter.
     */
    private static DatabaseAdapter shared = new DatabaseAdapter();

    public static DBAdapter produceMongoAdapter(){
        if(shared.adapter.isEmpty()){
            shared.adapter = Optional.of(new MongoDBAdapter());
        }
        return shared;
    }

    /** Reads data from the database and serializes each row / document to a concrete class provided by T.
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param tClass Class type that the data fetched from the database should be serialized to
     * @param <T> Type constraint ensures that the class provided must be a subclass of Entity.
     * @return A list of T objects (serialized from the data received from the database)
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public <T> List<T> read(QueryDetails details, Class<T> tClass) throws Exception {
        return this.adapter.orElseThrow(NoAdapterOpenException::new).read(details,tClass);
    }

    @Override
    public <T> Optional<T> readOne(QueryDetails details, Class<T> tClass) throws Exception {
        return this.adapter.orElseThrow(NoAdapterOpenException::new).readOne(details,tClass);
    }

    /** Inserts 1 entity into the database. The information for the insert query is gatherd by calling obj.keyValuePairs.
     * Any custom serialization of data should be done by overriding the method keyValuePairs(Class< ? extends Annotation > ...)
     * in the class T.
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param obj Object that should be inserted into the database.
     * @param <T> Type constraint. An object that is to be inserted must extend Entity
     * @return Number of rows (or documents) inserted
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public <T extends Entity> int create(QueryDetails details, T obj) throws Exception{
        return this.create(details, Collections.singletonList(obj));
    }


    /** Updates 1 entity in the database. The information for the update query is gathered by calling obj.keyValuePairs.
     * Any custom serialization of data should be done by overriding the method keyValuePairs(Class< ? extends Annotation > ...)
     * in the class T.
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param obj Object that should be inserted into the database.
     * @param <T> Type constraint. An object that is to be inserted must extend Entity
     * @return Number of rows (or documents) affected by the update.
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public <T extends Entity> int update(QueryDetails details, T obj) throws Exception{
        return this.update(details,Collections.singletonList(obj));
    }

    /** Deletes at most 1 entity in the database that matches the provided query information.
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @return Number of rows (or documents) deleted.
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public int deleteOne(QueryDetails details) throws Exception {
        return this.adapter.orElseThrow(NoAdapterOpenException::new).deleteOne(details);
    }


    /**
     * Deletes all entities in the database that match the provided query information.
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @return Number of rows (or documents) deleted.
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    @Override
    public int deleteMany(QueryDetails details) throws Exception {
        return this.adapter.orElseThrow(NoAdapterOpenException::new).deleteMany(details);
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

    /** Inserts multiple entities into the database using the provided query information. Each obj is serialized by calling
     * obj.keyValuePairs(...). To add custom serialization override this method in class T.
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param obj Objects that should be inserted into the database.
     * @param <T> Type constraint. An object that is to be inserted must extend Entity
     * @return Number of rows (or documents) inserted
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    public <T extends Entity> int create(QueryDetails details,List<T> obj) throws Exception{
        return this.adapter.orElseThrow(NoAdapterOpenException::new).create(details,obj);
    }

    /** Updates multiple entities in the database using the query information provided. Each obj is serialized by calling
     * obj.keyValuePairs(...). To add custom serialization override this method in class T.
     * @param details Query details contain all the information relevant for a query (tablename, projection, etc.)
     * @param obj Objects that should be inserted into the database.
     * @param <T> Type constraint. An object that is to be inserted must extend Entity
     * @return Number of rows (or documents) affected by the update.
     * @throws Exception Multiple exceptions may be thrown differing per database adapter.
     */
    public <T extends Entity> int update(QueryDetails details, List<T> obj) throws Exception{
        return this.adapter.orElseThrow(NoAdapterOpenException::new).update(details,obj);
    }

    /**
     * Closes the connection to the database server / cluster.
     */
    @Override
    public void close(){
        try {
            this.adapter.orElseThrow(NoAdapterOpenException::new).close();
            this.adapter = Optional.empty();
        }catch(Exception ignore){}
    }
}
