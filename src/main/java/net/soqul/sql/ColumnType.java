package net.soqul.sql;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
public enum ColumnType {
    CHAR("CHAR($)"),
    VARCHAR("VARCHAR($)"),
    TINYTEXT,
    TEXT,
    MEDIUMTEXT,
    LARGETEXT,

    TINYINT,
    BOOL,
    SMALLINT,
    MEDIUMINT,
    INT,
    BIGINT,
    DECIMAL,
    FLOAT("FLOAT($)"),
    DOUBLE,

    DATE,
    TIME,
    DATETIME,
    TIMESTAMP,
    YEAR,

    ENUM,
    SET,
    JSON("TEXT"),
    BLOB;
    String name;

    public static ColumnType getFromField(@NonNull Field field) {
        Class<?> clazz = field.getType();
        if (clazz.isPrimitive()) {

            if (clazz.equals(char.class)) return CHAR;
            else if (clazz.equals(boolean.class)) return BOOL;
            return clazz.equals(float.class) ? FLOAT : clazz.equals(double.class) ? DOUBLE : INT;
        }
        if (clazz.equals(String.class)) return VARCHAR;
        else if (clazz.equals(Set.class) || clazz.isAssignableFrom(Set.class)) return JSON;
        else if (clazz.equals(Enum.class)) return ENUM;
        else if (clazz.equals(Date.class)) return DATE;

        return JSON;
    }

    public String withMaxLength(int maxLength) {
        return name == null ? name() : name.replace("$", maxLength + "");
    }
}