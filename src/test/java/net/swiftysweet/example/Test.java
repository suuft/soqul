package net.swiftysweet.example;

import net.swiftysweet.soqul.SoqulProvider;
import net.swiftysweet.soqul.TDao;
import net.swiftysweet.soqul.sql.SqlConnection;
import net.swiftysweet.soqul.sql.impl.MySqlConnection;

public class Test {

    public static void main(String[] args) {
//        // Можно зарегать разными способами, самый простой по классу
//        // SoqulProvider.get().scanPackage("net.swiftysweet.example"); // - регистрация всех классов с нашими аннотациями
//        SoqulProvider.get().scanClass(User.class);
//        // Получение дао для нашего класса
//        TDao<User> userDao = SoqulProvider.get().getDao(User.class, null);
//        // Получение юзера, по типу филда который отмечен аннотацией @PrimaryKey
//        User user = userDao.getByPrimaryKey(1);
//        // Меняем любое значение
//        user.setFullName("Juhani Layne");
//        // Сохраняем в бд, все окей!
//        userDao.save(user);

        SqlConnection connection = MySqlConnection.createBuilder()
                .setHost("localhost")
                .setPort(3306)
                .setUsername("root")
                .setUseSSL(false)
                .setPassword("root")
                .setDatabase("test")
                .createTable("EnigmaCoins", "`Name` VARCHAR(32) NOT NULL PRIMARY KEY, `Coins` INT NOT NULL DEFAULT '0'")
                .build();
    }
}
