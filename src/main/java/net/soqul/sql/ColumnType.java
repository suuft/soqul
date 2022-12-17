package net.soqul.sql;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

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
            //boolean,char,byte,short,int,long,double,float
            if (clazz.isAssignableFrom(Number.class)) {
                return clazz.isAssignableFrom(float.class) ? FLOAT : clazz.isAssignableFrom(double.class) ? DOUBLE : INT;
            }
        }
        return JSON;
    }

    public String withMaxLength(int maxLength) {
        return name == null ? name() : name.replace("$", maxLength + "");
    }
}