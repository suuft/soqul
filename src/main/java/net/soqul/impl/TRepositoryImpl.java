package net.soqul.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.soqul.TRepository;
import net.soqul.annotation.field.InitateColumn;
import net.soqul.cache.ResponseCache;
import net.soqul.log.Log;
import net.soqul.sql.Executor;
import net.soqul.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

@Getter(AccessLevel.MODULE)
@AllArgsConstructor(access = AccessLevel.MODULE)
public class TRepositoryImpl<T> implements TRepository<T> {

    private static final Log log = new Log("SoqulRepo");

    private Executor executor;
    private ResponseCache<T> cache;
    private SoqulDto dto;

    @Override
    public void save(@NonNull T t) {
        log.info("Saving class %s to SQL..", t.getClass().getName());
        StringBuilder builderNames = new StringBuilder();
        StringBuilder builderValues = new StringBuilder();
        StringBuilder builderValuesAndNames = new StringBuilder();
        InitateColumn primaryInitateColumn = dto.getKeyField().getInitateColumn();

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
                    value = field.getInitateColumn().type() == InitateColumn.Type.JSON ? JsonUtil.to(fieldValue) : fieldValue;
            } catch (Exception exception) {
                exception.printStackTrace();
                log.warn("Cant get field value!");
            }
            builderNames.append("`" + field.getInitateColumn().name() + "`" + end);
            builderValues.append("'" + value + "'" + end);
            if (!field.isPrimaryKey())
                builderValuesAndNames.append("`" + field.getInitateColumn().name() + "` = '" + value + "'" + end);
        }


        String request = String.format("INSERT INTO `%s` (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s", dto.getTable(), builderNames, builderValues, builderValuesAndNames);
        executor.execute(false, request);

        if (cache != null) {
            try {
                java.lang.reflect.Field field1 = t.getClass().getDeclaredField(dto.getKeyField().getName());
                field1.setAccessible(true);
                Object fieldValue = field1.get(t);
                if (fieldValue != null)
                    cache.upCache("SELECT * FROM `" + dto.getTable() +
                            "` WHERE `" + primaryInitateColumn.name() + "` = '" +
                            (primaryInitateColumn.type() == InitateColumn.Type.JSON ? JsonUtil.to(fieldValue) :
                                    fieldValue + "' LIMIT 1"), t);

            } catch (Exception exception) {
                exception.printStackTrace();
                log.warn("Cant get field value!");

            }
        }

    }

    @Override
    public T getByPrimary(@NonNull Object object) {
        InitateColumn primaryInitateColumn = dto.getKeyField().getInitateColumn();
        String request = String.format("SELECT * FROM `" + dto.getTable() + "` WHERE `" + primaryInitateColumn.name() + "` = '" + (primaryInitateColumn.type() == InitateColumn.Type.JSON ? JsonUtil.to(object) : object.toString()) + "' LIMIT 1");

        return cache != null ? cache.putIfAbsent(request, executor, e -> requestByQuery(object, request))
                : requestByQuery(object, request);
    }

    public T requestByQuery(@NonNull Object object, String request) {
        try {
            InitateColumn primaryInitateColumn = dto.getKeyField().getInitateColumn();
            T t = (T) dto.getClazz().newInstance();
            executor.executeQuery(false, request, rs -> {
                if (!rs.next()) return null;
                for (SoqulField field : dto.getFields()) {
                    java.lang.reflect.Field field1 = t.getClass().getDeclaredField(field.getName());
                    field1.setAccessible(true);
                    Object value = rs.getObject(field.getInitateColumn().name());
                    field1.set(t, field.getInitateColumn().type() == InitateColumn.Type.JSON ? JsonUtil.from(value.toString(), field1.getClass()) : value);
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
