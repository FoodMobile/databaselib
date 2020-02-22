import annotations.DBId;
import models.Entity;
import org.bson.Document;
import org.bson.types.ObjectId;

public class User extends Entity {
    public String name;
    public int age = 12;
    @DBId
    private ObjectId _id;
    public User(){
        super();
    }
    public User(Document doc) {
        super(doc);
    }

    public String get_id() {
        return _id.toString();
    }
}
