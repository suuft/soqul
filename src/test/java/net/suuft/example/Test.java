package net.suuft.example;

import net.suuft.example.util.H2JDBCUtils;
import net.soqul.SoqulProvider;
import net.soqul.TRepository;
import net.soqul.util.JsonUtil;

public class Test {

    public static void main(String[] args) {
        SoqulProvider.get().scanClass(User.class);
        TRepository<User> userRepository = SoqulProvider.get()
                .createRepository(User.class, H2JDBCUtils.getConnection());
        userRepository.save(new User(1, "Valeriy Shushpanov", 53));
        System.out.println("final : " + JsonUtil.to(userRepository.getByPrimary(1)));
    }
}
