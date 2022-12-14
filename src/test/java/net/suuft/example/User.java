package net.suuft.example;

import lombok.*;
import net.soqul.annotation.Table;
import net.soqul.annotation.field.*;

import static net.soqul.annotation.field.Field.Type.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("web_users")
public class User {

    @NotNull
    @PrimaryKey
    @Field(name = "Login", type = VARCHAR)
    private String login;

    @DefaultValue("magic")
    @Field(name = "Full_Name", type = VARCHAR)
    private String fullName;

    @DefaultValue("0")
    @Field(name = "Age", type = INT)
    private int age;

}
