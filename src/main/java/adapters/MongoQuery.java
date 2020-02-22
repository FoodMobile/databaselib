package adapters;

import org.bson.conversions.Bson;

public class MongoQuery implements QueryDetails {
    protected String collection;
    protected Bson filter;

    public MongoQuery setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    public MongoQuery setFilter(Bson filter) {
        this.filter = filter;
        return this;
    }
}
