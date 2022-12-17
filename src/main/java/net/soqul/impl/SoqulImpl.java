package net.soqul.impl;

import lombok.NonNull;
import net.soqul.Soqul;
import net.soqul.TRepository;
import net.soqul.annotation.InitateEntity;
import net.soqul.annotation.field.InitateColumn;
import net.soqul.annotation.field.MakeNotNull;
import net.soqul.annotation.field.RetentionDefault;
import net.soqul.annotation.field.RetentionPrimary;
import net.soqul.cache.ResponseCache;
import net.soqul.log.Log;
import net.soqul.sql.Executor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoqulImpl implements Soqul {

    private final Log log = new Log("Soqul");
    private final Map<String, SoqulDto> scannedClasses = new LinkedHashMap<>();

    @Override
    public void scanPackage(@NonNull String packageName) {
        log.info("Try scan %s package", packageName);

    }

    @Override
    public void scanClass(@NonNull Class<?> clazz) {
        log.info("Try scan %s class", clazz.getName());
        InitateEntity initateEntity = clazz.getDeclaredAnnotation(InitateEntity.class);
        AtomicBoolean hasEmptyConstructor = new AtomicBoolean(false);
        Arrays.stream(clazz.getConstructors()).forEach(constructor -> {
            if (constructor.getParameterCount() == 0) hasEmptyConstructor.set(true);
        });
        if (!hasEmptyConstructor.get()) {
            log.warn("Failed to register class %s because it does not have empty constructor", clazz.getName());
            return;
        }

        if (initateEntity != null) {
            SoqulDto dtoClassData = new SoqulDto(clazz, null, new ArrayList<>());
            Arrays.stream(clazz.getDeclaredFields()).forEach(f -> {
                InitateColumn annotation = f.getAnnotation(InitateColumn.class);
                if (annotation != null) {
                    RetentionDefault retentionDefault = f.getAnnotation(RetentionDefault.class);
                    dtoClassData.registerField(new SoqulField(
                            f.getAnnotation(MakeNotNull.class) != null,
                            f.getAnnotation(RetentionPrimary.class) != null,
                            retentionDefault != null ? retentionDefault.value() : null,
                            f.getName(),
                            annotation));
                }
            });
            if (dtoClassData.getKeyField() == null) {
                log.warn("Failed to register class %s because it does not have a field marked as @PrimaryKey", clazz.getName());
                return;
            }
            scannedClasses.put(clazz.getName(), dtoClassData);
            log.info("The %s class was successfully scanned and saved!", clazz.getName());
        }
    }

    @Override
    public <T> TRepository<T> createRepository(@NonNull Class<T> clazz, @NonNull String tableName, @NonNull Connection connection, ResponseCache<T> cache) {
        if (!scannedClasses.containsKey(clazz.getName())) {
            log.warn("Class %s is not scanned, scan now..", clazz.getName());
            scanClass(clazz);
        }
        log.info("try create table..");
        try {
            String sql = scannedClasses.get(clazz.getName()).getCreateQuery(tableName);
            log.info(" SQL: %s", sql);
            connection.createStatement().execute(sql);
        } catch (Exception exception) {
            log.warn("The table could not be created.");
        }
        return new TRepositoryImpl<>(Executor.getExecutor(connection), cache, scannedClasses.get(clazz.getName()));
    }
}
