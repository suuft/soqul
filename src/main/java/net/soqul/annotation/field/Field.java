package net.soqul.annotation.field;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {

    String name();

    Type type();

    int lenght() default 32;

    @AllArgsConstructor
    @RequiredArgsConstructor
    enum Type {
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

        public String withMaxLength(int maxLength) {
            return name == null ? name() : name.replace("$", maxLength + "");
        }
    }
}
