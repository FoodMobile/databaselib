# DatabaseLib ver: 1.1
The databaselib is an abstract database adapter implementing object-mapping, multi-threading, and persistence control. 
The documentation is hosted here: https://foodmobile.github.io/databaselib/index.html
## Installation
### Maven:
The compiled JAR for the databaselib is hosted on a private maven repository. To use it in a project add
the following to your pom.xml file:
```xml
<repository>
    <id>FoodmobileRepo</id>
    <url>https://mymavenrepo.com/repo/15MSmTSCqdNKgB3459tN/</url>
</repository>
```
then add the the following dependency:
```xml
<dependency>
    <groupId>org.foodmobile</groupId>
    <artifactId>databaselib</artifactId>
    <version>1.1</version>
</dependency>
```

### Sources:
Alternatively you can download the sources and build them with the command
```
mvn clean package
```
The library JAR will be built and placed in the `target` directory of the project.

## Startup Guide:
### Initialization:
To use the shared DatabaseAdapter, it must be initialized with a concrete adapter class type at the
 beginning of the program (preferably). To connect to the database adapter selected in initialization, a ConnectionInfo object must be created 
and `.connect(ConnectionInfo)` must be called.
 example using the built in MongoDBAdapter:
 ```java

public class Test{
    public static void main(String[] args){
        ConnectionInfo info = new ConnectionInfo();
        info.protocol = "mongodb";
        info.addHost("localhost",27017,"test","test");
        info.database = "foodtruck";
        info.useSsl = false;
        try {
            DatabaseAdapter.produceMongoAdapter().connect(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Querying the Database:
Once a connection to the database has been established, the adapter is now ready to handle queries.
Since this is an object-mapped design paradigm, queries are handled by providing small bits of 
information (called QueryInformation) and providing objects that Extend `Entity` which contain 
the data that should be involved in the query (for insert and update operations). Each database adapter
 has a built in method called `queryFactory()` which sets up a QueryInformation instance ( note each adapter 
 has a different concrete subclass of the QueryInformation interface) and returns it to be used by the user. We will 
 continue our MongoDB example:
 ```java
MongoQuery query = DatabaseAdapter.produceMongoAdapter().queryFactory();
query.setCollection("exampleCollection");
```
### CRUD Operations
Once a query information object has been received, the user can now fill in the relevant fields and 
perform a query on the database.
#### Create:
Create queries attempt to insert data into the database. They read their information from a concrete 
subclass instance of `Entity` using query information.
Suppose we have the following example class for all further queries:
```java
import com.foodmobile.databaselib.annotations.DBId;
import com.foodmobile.databaselib.models.Entity;
import org.bson.types.ObjectId;
public class User extends Entity{
    public String name;
    @DBId
    public ObjectId _id;
}
```
This represents a user in the real world that has a name, and an id.
We can insert a user into the database with the following example:
```java
MongoQuery query = DatabaseAdapter.produceMongoAdapter().queryFactory();
query.setCollection("users");
User andrew = new User();
andrew.name = "Andrew";
try{
    DatabaseAdapter.produceMongoAdapter().create(query,andrew);
}catch(Exception e){
    e.printStackTrace();
}
```

#### Read:
Read queries retrieve information from the database and return a list of concrete subclass instances of `Entity` 
that represent the data. 
 We can read all users in our mongo example by
 performing the following query:
 ```java
MongoQuery query = DatabaseAdapter.produceMongoAdapter().queryFactory();
query.setCollection("users");
try{
    List<User> users = DatabaseAdapter.produceMongoAdapter().read(query,User.class);
}catch(Exception e){
    e.printStackTrace();
}
```

#### Update:
We can update existing entities in a database with the update operation. This takes query information and an example object 
used to provided the updated information. Most QueryInformation objects will have some mechanism to provide 
filters for the type of data we want updated (eg. change the age of everyone name 'Andrew' to 21). See 
the specific QueryInformation for your selected adapter on how to do that.
Here we will just update everyone in the user table and change their name to 'Andrew':
```java
MongoQuery query = DatabaseAdapter.produceMongoAdapter().queryFactory();
query.setCollection("users");
User andrew = new User();
andrew.name = "Andrew";
try{
    DatabaseAdapter.produceMongoAdapter().update(query,andrew);
}catch(Exception e){
    e.printStackTrace();
}
```

#### Delete:
Delete queries remove data from the database by applying a filter provided in the QueryInformation. In the 
following example we will delete everyone named 'Andrew':
```java
MongoQuery query = DatabaseAdapter.produceMongoAdapter().queryFactory();
query.setCollection("users");
query.filter = Filters.eq("name","Andrew");
try{
    DatabaseAdapter.produceMongoAdapter().deleteMany(query);
}catch(Exception e){
    e.printStackTrace();
}
```


