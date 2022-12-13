package net.suuft.example;

import lombok.AllArgsConstructor;
import net.suuft.soqul.annotation.Table;
import net.suuft.soqul.annotation.field.DefaultValue;
import net.suuft.soqul.annotation.field.Field;
import net.suuft.soqul.annotation.field.NotNull;
import net.suuft.soqul.annotation.field.PrimaryKey;

@AllArgsConstructor
@Table("users")
public class User {

    @NotNull
    @PrimaryKey
    @Field(name = "Id", type = Field.Type.INT)
    private long identifier;

    @NotNull
    @DefaultValue("James Brown")
    @Field(name = "Full_Name", type = Field.Type.VARCHAR)
    private String fullName;

    @NotNull
    @Field(name = "Role", type = Field.Type.INT)
    private String role;
}
