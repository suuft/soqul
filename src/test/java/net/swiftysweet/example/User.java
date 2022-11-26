package net.swiftysweet.example;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.swiftysweet.soqul.annotation.field.DefaultValue;
import net.swiftysweet.soqul.annotation.field.NotNull;
import net.swiftysweet.soqul.annotation.field.PrimaryKey;
import net.swiftysweet.soqul.annotation.Table;

@Data
@Table("users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @NotNull
    @PrimaryKey
    long identifier;

    @NotNull
    @DefaultValue("James Brown")
    String fullName;
}
