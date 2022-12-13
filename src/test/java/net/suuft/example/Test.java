package net.suuft.example;

import net.suuft.example.util.H2JDBCUtils;
import net.suuft.soqul.SoqulProvider;
import net.suuft.soqul.TRepository;

public class Test {

    public static void main(String[] args) {
        SoqulProvider.get().scanClass(User.class);
        TRepository<User> userRepository = SoqulProvider.get()
                .createRepository(User.class, H2JDBCUtils.getConnection());

        userRepository.save(new User(1, "Valeriy Shushpanov", "Admin"));
    }
}
