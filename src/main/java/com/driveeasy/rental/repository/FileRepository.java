package com.driveeasy.rental.repository;

import com.driveeasy.rental.model.FileStorable;
import com.driveeasy.rental.model.Identifiable;
import com.driveeasy.rental.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generic file-based repository. Stores one record per line in a .txt file.
 *
 * OOP concepts:
 *  - Abstraction + Inheritance: all repositories extend this class and only
 *    write their own parse() method. Reading, writing and ID generation are shared.
 *  - Generics: works for any type that has an ID and can be written to a file.
 *
 * File handling: uses BufferedReader/FileReader to read and BufferedWriter/FileWriter
 * to write. Methods are synchronized so two requests never write the file at once.
 *
 * @param <T> type of record stored
 */
public abstract class FileRepository<T extends Identifiable & FileStorable> implements DataRepository<T> {

    private static final Logger LOG = LoggerFactory.getLogger(FileRepository.class);

    private final File file;
    private final String idPrefix;

    protected FileRepository(String dataDirectory, String fileName, String idPrefix) {
        this.file = new File(dataDirectory, fileName);
        this.idPrefix = idPrefix;
        createFileIfMissing();
    }

    /** Builds one object from the fields of a stored line. Each repository knows its own format. */
    protected abstract T parse(String[] fields);

    // ---------- CRUD ----------

    @Override
    public synchronized T save(T entity) {
        List<T> records = readAll();
        if (entity.getId() == null || entity.getId().isBlank()) {
            entity.setId(generateNextId(records));
        }
        records.add(entity);
        writeAll(records);
        return entity;
    }

    @Override
    public synchronized List<T> findAll() {
        return readAll();
    }

    @Override
    public synchronized Optional<T> findById(String id) {
        return readAll().stream()
                .filter(record -> record.getId().equals(id))
                .findFirst();
    }

    @Override
    public synchronized boolean update(T entity) {
        List<T> records = readAll();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(entity.getId())) {
                records.set(i, entity);
                writeAll(records);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        List<T> records = readAll();
        boolean removed = records.removeIf(record -> record.getId().equals(id));
        if (removed) {
            writeAll(records);
        }
        return removed;
    }

    public synchronized boolean isEmpty() {
        return readAll().isEmpty();
    }

    // ---------- File read / write ----------

    private List<T> readAll() {
        List<T> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    records.add(parse(FileUtil.split(line)));
                } catch (RuntimeException e) {
                    LOG.warn("Skipping damaged line {} in {}: {}", lineNumber, file.getName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + file.getPath(), e);
        }
        return records;
    }

    private void writeAll(List<T> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {
            for (T record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + file.getPath(), e);
        }
    }

    private void createFileIfMissing() {
        try {
            File folder = file.getParentFile();
            if (folder != null && !folder.exists() && !folder.mkdirs()) {
                throw new IOException("Could not create folder " + folder.getPath());
            }
            if (file.createNewFile()) {
                LOG.info("Created data file {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create " + file.getPath(), e);
        }
    }

    /** Next ID such as U001, U002 ... based on the highest number already used. */
    private String generateNextId(List<T> records) {
        int highest = 0;
        for (T record : records) {
            String number = record.getId().substring(idPrefix.length());
            try {
                highest = Math.max(highest, Integer.parseInt(number));
            } catch (NumberFormatException ignored) {
                // IDs that do not follow the pattern are ignored
            }
        }
        return String.format("%s%03d", idPrefix, highest + 1);
    }

    // ---------- Helpers for subclasses when parsing ----------

    protected static int toInt(String value) {
        return value == null || value.isBlank() ? 0 : Integer.parseInt(value.trim());
    }

    protected static double toDouble(String value) {
        return value == null || value.isBlank() ? 0 : Double.parseDouble(value.trim());
    }

    protected static boolean toBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    protected static LocalDate toDate(String value) {
        return LocalDate.parse(value.trim());
    }

    protected static LocalDateTime toDateTime(String value) {
        return LocalDateTime.parse(value.trim());
    }

    /** Returns the field at this position, or "" if the line is shorter. */
    protected static String field(String[] fields, int index) {
        return index < fields.length ? fields[index] : "";
    }
}
