package net.suuft.example;

import net.soqul.SoqulProvider;
import net.soqul.TRepository;
import net.suuft.example.util.MySqlUtil;

// It is best to transfer and use the repository by injection, but it will do :)
// Singleton is bad!
public class UserManager {

    public static final UserManager INSTANCE = new UserManager();
    private TRepository<User> userRepository = SoqulProvider.get().createRepository(User.class, MySqlUtil
            .createConnection("localhost", 3306, "root", "root", "test", false));

    public User getUser(String login) {
        User user = userRepository.getByPrimary(login);
        return user == null ? new User(login, "New User", 0) : user;
    }

    public void save(User user) {
        userRepository.save(user);
    }
}

