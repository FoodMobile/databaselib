package com.foodmobile.databaselib.adapters;

import com.foodmobile.databaselib.annotations.DBId;
import com.foodmobile.databaselib.annotations.DBIgnore;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.foodmobile.databaselib.exceptions.InvalidHostException;
import com.foodmobile.databaselib.exceptions.InvalidQueryType;
import com.foodmobile.databaselib.models.Entity;
import org.bson.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

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
        var collection = this.db.getCollection(query.collection,tClass);
        if(query.filter == null){
            collection.find().forEach((Consumer<T> ) results::add);
        }else {
            collection.find(query.filter).forEach((Consumer<T>) results::add);
        }
        return results;
    }

    @Override
    public <T> Optional<T> readOne(QueryDetails details, Class<T> tClass) throws Exception {
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);
        var collection = this.db.getCollection(query.collection);
        if(query.filter == null){
            return Optional.ofNullable(collection.find(tClass).cursor().tryNext());
        }else{
            return Optional.ofNullable(collection.find(query.filter,tClass).cursor().tryNext());
        }
    }

    @Override
    public <T extends Entity> int create(QueryDetails details, T obj) throws Exception{
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);
        MongoCollection<Document> collection = this.db.getCollection(query.collection);
        Document d = new Document(obj.keyValuePairs(DBId.class, DBIgnore.class));
        collection.insertOne(d);
        return 1;
    }

    @Override
    public <T extends Entity> int update(QueryDetails details, T obj) throws Exception{
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);

        var collection = this.db.getCollection(query.collection,obj.getClass());

        Bson updateObj = new Document(obj.keyValuePairs(DBIgnore.class));
        collection.updateOne(query.filter,updateObj);
        return 1;
    }

    @Override
    public int deleteOne(QueryDetails details) throws Exception {
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);
        MongoCollection<Document> collection = this.db.getCollection(query.collection);
        collection.deleteOne(query.filter);
        return 1;
    }

    @Override
    public int deleteMany(QueryDetails details) throws Exception {
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);
        MongoCollection<Document> collection = this.db.getCollection(query.collection);
        collection.deleteMany(query.filter);
        return 1;
    }

    public <T extends Entity> int create(QueryDetails details,List<T> obj) throws Exception{
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);
        MongoCollection<Document> collection = this.db.getCollection(query.collection);
        List<Document> docs = obj.stream()
                .map(o -> new Document(o.keyValuePairs(DBId.class,DBIgnore.class)))
                .collect(Collectors.toList());
        collection.insertMany(docs);
        return obj.size();
    }

    public <T extends Entity> int update(QueryDetails details, List<T> obj) throws Exception{
        MongoQuery query = Optional.ofNullable((details instanceof MongoQuery) ? (MongoQuery)details : null)
                .orElseThrow(InvalidQueryType::new);
        MongoCollection<Document> collection = this.db.getCollection(query.collection);
        List<Bson> docs = obj.stream()
                .map(o -> new Document(o.keyValuePairs(DBIgnore.class)))
                .collect(Collectors.toList());
        collection.updateMany(query.filter,docs);
        return obj.size();
    }

    /**
     * Connects to the mongodb cluster with a list of hosts. Note that the same credentials and database name are used
     * for every host so these must be the same across hosts.
     * @param info Connection information required to connect to MongoDB
     * @throws Exception Connection Exceptions may be thrown by the Mongo driver, as well as host validation exceptions
     * (see com.foodmobile.databaselib.exceptions for a list of exceptions)
     */
    @Override
    @Deprecated
    public void connect(ConnectionInfo info) throws Exception{
        throw new UnsupportedOperationException();
    }

    public void applyMongoConnectionString(String connectionString, String databaseName) throws Exception {
        CodecRegistry codecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(), 
            org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        this.client = MongoClients.create(connectionString);
        this.db = this.client.getDatabase(databaseName).withCodecRegistry(codecRegistry);
    }

    @Override
    public void close(){
        this.db = null;
        this.client.close();
    }

    @Override
    public MongoQuery queryFactory() {
        return new MongoQuery();
    }
}
