package net.suuft.soqul.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.suuft.soqul.annotation.field.Field;

@Getter(AccessLevel.MODULE)
@AllArgsConstructor(access = AccessLevel.MODULE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SoqulField {

    boolean notNull;
    boolean primaryKey;
    String defaultValue;
    String name;
    Field field;


    @Override
    public String toString() {
        return "`" + field.name() + "` " + field.type().withMaxLength(field.lenght())
                + (notNull ? " NOT NULL" : "") + (primaryKey ? " PRIMARY KEY" : "") +
                (defaultValue != null ? " DEFAULT '" + defaultValue + "'" : "");
    }
}
