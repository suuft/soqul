package net.suuft.example;

public class Test {

    public static void main(String[] args) {

        UserManager manager = UserManager.INSTANCE;
        User user = manager.getUser("admin");

        user.setAge(99);
        user.setFullName("valeriy shushpanow");

        manager.save(user);
    }

}
