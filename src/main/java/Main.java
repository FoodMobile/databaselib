import adapters.ConnectionInfo;
import adapters.MongoDBAdapter;
import adapters.MongoQuery;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        ConnectionInfo info = new ConnectionInfo();
        info.protocol = "mongodb";
        info.addHost("localhost",27017,"test","test");
        info.database = "foodtruck";
        info.useSsl = false;
        try {
            DatabaseAdapter.shared = new DatabaseAdapter(MongoDBAdapter.class);
            DatabaseAdapter.shared.connect(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MongoQuery query = ((MongoQuery) DatabaseAdapter.shared.queryFactory()).setCollection("users");
        try {
            User newUser = new User();
            newUser.name = "john";
            DatabaseAdapter.shared.create(query,newUser);
            List<User> user = DatabaseAdapter.shared.read(query,User.class);
            System.out.println(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
