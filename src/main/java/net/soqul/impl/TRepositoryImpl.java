package net.soqul.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.soqul.TRepository;
import net.soqul.annotation.field.InitateColumn;
import net.soqul.cache.ResponseCache;
import net.soqul.log.Log;
import net.soqul.sql.ColumnType;
import net.soqul.sql.Executor;
import net.soqul.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter(AccessLevel.MODULE)
@AllArgsConstructor(access = AccessLevel.MODULE)
public class TRepositoryImpl<T> implements TRepository<T> {

    private static final Log log = new Log("SoqulRepo");

    private String table;
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
                    value = field.getType() == ColumnType.JSON ? JsonUtil.to(fieldValue) : fieldValue;
            } catch (Exception exception) {
                exception.printStackTrace();
                log.warn("Cant get field value!");
            }
            builderNames.append("`" + field.getInitateColumn().name() + "`" + end);
            builderValues.append("'" + value + "'" + end);
            if (!field.isPrimaryKey())
                builderValuesAndNames.append("`" + field.getInitateColumn().name() + "` = '" + value + "'" + end);
        }


        String request = String.format("INSERT INTO `%s` (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s", table, builderNames, builderValues, builderValuesAndNames);
        executor.execute(false, request);

        if (cache != null) {
            try {
                java.lang.reflect.Field field1 = t.getClass().getDeclaredField(dto.getKeyField().getName());
                field1.setAccessible(true);
                Object fieldValue = field1.get(t);
                if (fieldValue != null)
                    cache.upCache("SELECT * FROM `" + table +
                            "` WHERE `" + primaryInitateColumn.name() + "` = '" +
                            (dto.getKeyField().getType() == ColumnType.JSON ? JsonUtil.to(fieldValue) :
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
        String request = String.format("SELECT * FROM `" + table + "` WHERE `" + primaryInitateColumn.name() + "` = '" + (dto.getKeyField().getType() == ColumnType.JSON ? JsonUtil.to(object) : object.toString()) + "' LIMIT 1");

        return cache != null ? cache.putIfAbsent(request, executor, e -> requestByQuery(object, request))
                : requestByQuery(object, request);
    }



    @Override
    public Collection<T> getAll() {
        List<T> all = new ArrayList<>();
        executor.executeQuery(false, "SELECT * FROM `" + table + ";", rs -> {
            if (!rs.next()) return null;
            for (SoqulField field : dto.getFields()) {
                T t = (T) dto.getClazz().newInstance();
                java.lang.reflect.Field field1 = dto.getClazz().getDeclaredField(field.getName());
                field1.setAccessible(true);
                Object value = rs.getObject(field.getInitateColumn().name());
                field1.set(t, field.getType() == ColumnType.JSON ? JsonUtil.from(value.toString(), field1.getClass()) : value);
                all.add(t);
            }
            return null;
        });
        return all;
    }

    @Override
    public Collection<T> getByFilter(Predicate<T> filter) {
        return getAll().stream().filter(filter).collect(Collectors.toList());
    }


    @Deprecated
    private T requestByQuery(@NonNull Object object, String request) {
        try {
            InitateColumn primaryInitateColumn = dto.getKeyField().getInitateColumn();
            T t = (T) dto.getClazz().newInstance();
            executor.executeQuery(false, request, rs -> {
                if (!rs.next()) return null;
                for (SoqulField field : dto.getFields()) {
                    java.lang.reflect.Field field1 = t.getClass().getDeclaredField(field.getName());
                    field1.setAccessible(true);
                    Object value = rs.getObject(field.getInitateColumn().name());
                    field1.set(t, field.getType() == ColumnType.JSON ? JsonUtil.from(value.toString(), field1.getClass()) : value);
                }
                return null;
            });
            return t;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
