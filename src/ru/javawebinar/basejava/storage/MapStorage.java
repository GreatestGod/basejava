package ru.javawebinar.basejava.storage;

import ru.javawebinar.basejava.model.Resume;

import java.util.HashMap;
import java.util.Map;

public class MapStorage extends AbstractStorage {
    private Map<String, Resume> storage = new HashMap<>();

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public void update(Resume r) {
        storage.put(r.getUuid(), r);
    }

    @Override
    public void save(Resume r) {
        storage.put(r.getUuid(), r);
    }

    @Override
    public Resume get(String uuid) {
        return storage.get(uuid);
    }

    @Override
    public void delete(String uuid) {
        storage.remove(uuid);
    }

    @Override
    public Resume[] getAll() {
        return storage.entrySet().stream().map(Map.Entry::getValue).toArray(Resume[]::new);
    }

    @Override
    public int size() {
        return storage.size();
    }
}
