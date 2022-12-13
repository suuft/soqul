package net.soqul.impl;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter(AccessLevel.MODULE)
@Setter(AccessLevel.MODULE)
@AllArgsConstructor(access = AccessLevel.MODULE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SoqulDto {


    String table;
    Class<?> clazz;
    SoqulField keyField;
    List<SoqulField> fields;

    protected void registerField(@NonNull SoqulField field) {
        if (field.isPrimaryKey()) keyField = field;
        fields.add(field);
    }

    public String getCreateQuery() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (SoqulField field : fields) {
            i++;
            builder.append(field.toString() + (i >= fields.size() ? "" : ", "));
        }
        return String.format("CREATE TABLE IF NOT EXISTS `%s` (%s)", table, builder);
    }
}
