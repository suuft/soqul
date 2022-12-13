package net.suuft.soqul.impl;

import lombok.NonNull;
import net.suuft.soqul.Soqul;
import net.suuft.soqul.TRepository;
import net.suuft.soqul.annotation.Table;
import net.suuft.soqul.annotation.field.DefaultValue;
import net.suuft.soqul.annotation.field.Field;
import net.suuft.soqul.annotation.field.NotNull;
import net.suuft.soqul.annotation.field.PrimaryKey;
import net.suuft.soqul.log.Log;
import net.suuft.soqul.sql.Executor;

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
        Table table = clazz.getDeclaredAnnotation(Table.class);
        AtomicBoolean hasEmptyConstructor = new AtomicBoolean(false);
        Arrays.stream(clazz.getConstructors()).forEach(constructor -> {
            if(constructor.getParameterCount() == 0) hasEmptyConstructor.set(true);
        });
        if (!hasEmptyConstructor.get()) {
            log.warn("Failed to register class %s because it does not have empty constructor", clazz.getName());
            return;
        }

        if (table != null) {
            SoqulDto dtoClassData = new SoqulDto(table.value(), clazz, null, new ArrayList<>());
            Arrays.stream(clazz.getDeclaredFields()).forEach(f -> {
                Field annotation = f.getAnnotation(Field.class);
                if (annotation != null) {
                    DefaultValue defaultValue = f.getAnnotation(DefaultValue.class);
                    dtoClassData.registerField(new SoqulField(
                            f.getAnnotation(NotNull.class) != null,
                            f.getAnnotation(PrimaryKey.class) != null,
                            defaultValue != null ? defaultValue.value() : null,
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
    public <T> TRepository<T> createRepository(@NonNull Class<T> clazz, @NonNull Connection connection) {
        if (!scannedClasses.containsKey(clazz.getName())) {
            log.warn("Class %s is not scanned, scan now..", clazz.getName());
            scanClass(clazz);
        }
        log.info("try create table..");
        try {
            String sql = scannedClasses.get(clazz.getName()).getCreateQuery();
            log.info(" SQL: %s", sql);
            connection.createStatement().execute(sql);
        } catch (Exception exception) {
            log.warn("The table could not be created.");
        }
        return new TRepositoryImpl<>(Executor.getExecutor(connection), scannedClasses.get(clazz.getName()));
    }
}
