package net.soqul.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.soqul.annotation.field.InitateColumn;

@Getter(AccessLevel.MODULE)
@AllArgsConstructor(access = AccessLevel.MODULE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SoqulField {

    boolean notNull;
    boolean primaryKey;
    String defaultValue;
    String name;
    InitateColumn initateColumn;


    @Override
    public String toString() {
        return "`" + initateColumn.name() + "` " + initateColumn.type().withMaxLength(initateColumn.lenght())
                + (notNull ? " NOT NULL" : "") + (primaryKey ? " PRIMARY KEY" : "") +
                (defaultValue != null ? " DEFAULT '" + defaultValue + "'" : "");
    }
}
