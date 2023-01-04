soqul
---
This is maybe the simplest ORM in the world for SQL. There is a little functionality here, I try to make it better.
If you have suggestions on what to add, what to remove, where to change - contact to me!

### `Project To-Do:`

* Refactor code
* Add more functions (caching, other..)

### `Add as depend:`

| **Gradle:**

```groovy
repositories {
    // other repositories
    maven {
        name = "clojars.org"
        url = uri("https://repo.clojars.org")
    }
}

dependencies {
    implementation 'net.clojars.suuft:soqul:1.0.4'
}
```

| **Maven:**
Repository:

```xml
<repository>
    <id>clojars.org</id>
    <url>https://repo.clojars.org</url>
</repository>
```

Depend:

```xml

<dependency>
    <groupId>net.clojars.suuft</groupId>
    <artifactId>soqul</artifactId>
    <version>1.0.4</version>
</dependency>
```

---

### `Usage examples:`

First, you need to create a class that we will store, let's say it's a User and it has an ID, name and age:

```java
public class User {

    private long identifier;
    private String fullName;
    private int age;
}
```

Now, above the class, you need to specify the annotation @InitateEntity, thanks to this
annotation, ORM will find your class and register:
name "web_users"::

```java
import net.soqul.annotation.InitateEntity;

@InitateEntity
public class User {

    private String login;
    private String fullName;
    private int age;

}
```

Wonderful, now we need to place annotations over each variable that we want to store in the database:

| Annotation        | Description                                                                                       |
|-------------------|---------------------------------------------------------------------------------------------------|
| @InitateColumn    | Must be above each field. <br/>Specifies the type to store and the name of the column.                 |
| @RetentionPrimary | Should ONLY be above the main field. <br/>Indicates the ID field by which the object can be retrieved. |
| @RetentionFilled  | You need to put over each field the value of which != null.                                                     |
| @RetentionDefault | Sets the default value for the variable.                                                     |

As a result, we get something like:

```java
import lombok.*;
import net.soqul.annotation.InitateEntity;
import net.soqul.annotation.field.*;

import static net.soqul.annotation.field.InitateColumn.Type.*;

@Setter // not compulsory
@NoArgsConstructor // There must be an empty constructor
@AllArgsConstructor // not compulsory
@InitateEntity
public class User {

    @RetentionFilled
    @RetentionPrimary
    @InitateColumn(name = "Login")
    private String login;

    @RetentionDefault("Imaginary Man")
    @InitateColumn(name = "Full_Name")
    private String fullName;

    @RetentionDefault("0")
    @InitateColumn(name = "Age")
    private int age;

}
```

| **Now let's learn how to use the DTO class that we just wrote:**

For example, we will use MySQL. We will have the MySqlUtil utility:

```java
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.SQLException;

@UtilityClass
public class MySqlUtil {

    public Connection createConnection(String host, int port, String username, String password, String database,
                                       boolean useSSL) {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(database);

        dataSource.setEncoding("UTF-8");
        dataSource.setAutoReconnect(true);
        dataSource.setUseSSL(useSSL);
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

Creating a class with a single instance - UserManager:

```java
public class UserManager {

    public static final UserManager INSTANCE = new UserManager();
}
```

Let's create a userRepository variable in it, from which we will receive data, and initialize it using our user class
and use generated mysql connection:

```java
import net.soqul.SoqulProvider;
import net.soqul.TRepository;
import net.suuft.example.util.MySqlUtil;

public class UserManager {

    public static final UserManager INSTANCE = new UserManager();
    private final TRepository<User> userRepository = SoqulProvider.get().createRepository(User.class, "Users", MySqlUtil
            .createConnection("localhost", 3306, "root", "root", "test", false));


}
```

Next, we will create two methods to get (if not, create) and save the user:

```java
import net.soqul.SoqulProvider;
import net.soqul.TRepository;
import net.suuft.example.util.MySqlUtil;

// It is best to transfer and use the repository by injection, but it will do :)
// Singleton is bad!
public class UserManager {

    public static final UserManager INSTANCE = new UserManager();
    private final TRepository<User> userRepository = SoqulProvider.get().createRepository(User.class, MySqlUtil
            .createConnection("localhost", 3306, "root", "root", "test", false));

    public User getUser(String login) {
        User user = userRepository.getByPrimary(login);
        return user == null ? new User(login, "New User", 0) : user;
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
```

Super, we did it! Shall we try to check?

```java
public class Test {

    public static void main(String[] args) {
        UserManager manager = UserManager.INSTANCE;
        User user = manager.getUser("admin");

        user.setAge(99);
        user.setFullName("valeriy shushpanow");

        manager.save(user);
    }

}
```

---

If you have any ideas, or you have found problems, contact to me or make a pull requests.