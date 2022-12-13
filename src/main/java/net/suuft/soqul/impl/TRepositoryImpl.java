package net.suuft.soqul.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.suuft.soqul.TRepository;
import net.suuft.soqul.annotation.field.Field;
import net.suuft.soqul.log.Log;
import net.suuft.soqul.sql.Executor;
import net.suuft.soqul.util.JsonUtil;

import java.util.Collection;
import java.util.function.Predicate;

@Getter(AccessLevel.MODULE)
@AllArgsConstructor(access = AccessLevel.MODULE)
public class TRepositoryImpl<T> implements TRepository<T> {

    private static final Log log = new Log("SoqulRepo");

    private Executor executor;
    private SoqulDto dto;

    @Override
    public void save(@NonNull T t) {
        log.info("Saving class %s to SQL..", t.getClass().getName());
        StringBuilder builderNames = new StringBuilder();
        StringBuilder builderValues = new StringBuilder();
        StringBuilder builderValuesAndNames = new StringBuilder();
        int i = 0;
        if (dto == null) {
            log.warn("dto is null!");
            return;
        }
        for (SoqulField field : dto.getFields()) {
            i++;
            String end = (i >= dto.getFields().size() ? "" : ", ");
            Object value = field.getDefaultValue();
            try {
                java.lang.reflect.Field field1 = t.getClass().getDeclaredField(field.getName());
                field1.setAccessible(true);
                Object fieldValue = field1.get(t);
                if (fieldValue != null)
                    value = field.getField().type() == Field.Type.JSON ? JsonUtil.to(fieldValue) : fieldValue;
            } catch (Exception exception) {
                exception.printStackTrace();
                log.warn("Cant get field value!");
            }
            builderValues.append("`" + value + "`" + end);
            builderValuesAndNames.append("`" + field.getField().name() + "` = `" + value + "`" + end);
            builderNames.append("`" + field.getField().name() + "`" + end);
        }


        String request = String.format("INSERT INTO `%s` (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s", dto.getTable(), builderNames, builderValues, builderValuesAndNames);
        log.info(" SQL: " + request);
    }

    @Override
    public T getByPrimaryKey(@NonNull Object object) {
        return null;
    }

    @Override
    public Collection<T> getAll() {
        return null;
    }

    @Override
    public Collection<T> getByFilter(Predicate<T> filter) {
        return null;
    }

}
