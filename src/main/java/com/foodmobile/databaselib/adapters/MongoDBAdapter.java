package com.foodmobile.databaselib.adapters;

import com.foodmobile.databaselib.annotations.DBId;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.foodmobile.databaselib.exceptions.InvalidHostException;
import com.foodmobile.databaselib.exceptions.InvalidQueryType;
import com.foodmobile.databaselib.models.Entity;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MongoDBAdapter implements DBAdapter {

    private MongoClient client;

    // Reference to the mongo db
    private MongoDatabase db;


    @Override
    public <T> List<T> read(QueryDetails details, Class<T> tClass) throws Exception{
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);

        List<T> results = new LinkedList<>();
        MongoCollection<Document> collection = this.db.getCollection(query.collection);
        Constructor<T> constr = tClass.getConstructor(Document.class);
        if(query.filter == null){
            collection.find().forEach((Consumer<? super Document>) d -> {
                try {
                    results.add(constr.newInstance(d));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }else {
            collection.find(query.filter).forEach((Consumer<? super Document>) d -> {
                try {
                    results.add(constr.newInstance(d));
                } catch (Exception e) {
                }
            });
        }

        return results;
    }

    @Override
    public <T extends Entity> int create(QueryDetails details, T obj) throws Exception{
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);
        MongoCollection<Document> collection = this.db.getCollection(query.collection);
        Document d = new Document(obj.keyValuePairs(DBId.class));
        collection.insertOne(d);
        return 1;
    }

    @Override
    public <T extends Entity> int update(QueryDetails details, T obj) throws Exception{
        return 0;
    }

    @Override
    public int delete(QueryDetails details) throws Exception {
        return 0;
    }


    /**
     * Connects to the mongodb cluster with a list of hosts. Note that the same credentials and database name are used
     * for every host so these must be the same across hosts.
     * @param info Connection information required to connect to MongoDB
     * @throws Exception Connection Exceptions may be thrown by the Mongo driver, as well as host validation exceptions
     * (see com.foodmobile.databaselib.exceptions for a list of exceptions)
     */
    @Override
    public void connect(ConnectionInfo info) throws Exception{
        List<ServerAddress> addressList = info.hosts.stream().map((h)->{
           return new ServerAddress(h.hostName,h.hostPort);
        }).collect(Collectors.toList());

        Host firstHost = info.hosts.stream().findFirst().orElseThrow(InvalidHostException::new).validate();
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(info.connectionsPerHost).sslEnabled(info.useSsl).build();
        MongoCredential credential = MongoCredential.createCredential(firstHost.username,info.database,firstHost.password.toCharArray());

        this.client = new MongoClient(addressList,credential,options);

        this.db = this.client.getDatabase(info.database);
    }

    @Override
    public void close() throws Exception {
        this.db = null;
        this.client.close();
    }

    @Override
    public MongoQuery queryFactory() {
        return new MongoQuery();
    }
}
