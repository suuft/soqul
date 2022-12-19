package net.suuft.example;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.soqul.annotation.InitateEntity;
import net.soqul.annotation.field.InitateColumn;
import net.soqul.annotation.field.RetentionDefault;
import net.soqul.annotation.field.RetentionFilled;
import net.soqul.annotation.field.RetentionPrimary;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@InitateEntity
public class User {

    @RetentionFilled
    @RetentionPrimary
    @InitateColumn(name = "Login")
    private String login;

    @RetentionDefault("magic")
    @InitateColumn(name = "Full_Name")
    private String fullName;

    @RetentionDefault("0")
    @InitateColumn(name = "Age")
    private int age;

}
