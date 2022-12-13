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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
            builderNames.append("`" + field.getField().name() + "`" + end);
            builderValues.append("'" + value + "'" + end);
            if (!field.isPrimaryKey()) builderValuesAndNames.append("`" + field.getField().name() + "` = '" + value + "'" + end);
        }


        String request = String.format("INSERT INTO `%s` (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s", dto.getTable(), builderNames, builderValues, builderValuesAndNames);
        executor.execute(false, request);
    }

    @Override
    public T getByPrimary(@NonNull Object object) {
        try {
            Field primaryField = dto.getKeyField().getField();
            T t = (T) dto.getClazz().newInstance();
            String request = String.format("SELECT * FROM `" + dto.getTable() + "` WHERE `" + primaryField.name() + "` = '" + (primaryField.type() == Field.Type.JSON ? JsonUtil.to(object) : object.toString()) + "' LIMIT 1");
            Map<String, Object> fieldValueMap = new HashMap<>();
            executor.executeQuery(false, request, rs -> {
                if (!rs.next()) return null;
                for (SoqulField field : dto.getFields()) {
                    java.lang.reflect.Field field1 = t.getClass().getDeclaredField(field.getName());
                    field1.setAccessible(true);
                    field1.set(t, rs.getObject(field.getField().name()));
                }
                return null;
            });
            return t;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<T> getAll() {
        return new ArrayList<>();
    }

    @Override
    public Collection<T> getByFilter(Predicate<T> filter) {
        return new ArrayList<>();
    }

}
