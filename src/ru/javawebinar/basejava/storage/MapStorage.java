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
    protected void fillDeletedElement(int index, String uuid) {
        storage.remove(uuid);
    }

    @Override
    protected void insertElement(Resume r, int index) {
        storage.put(r.getUuid(), r);
    }

    @Override
    protected int getIndex(String uuid) {
        return storage.containsKey(uuid) ? 1 : -1;
    }

    @Override
    protected void insert(int index, Resume resume) {
        storage.put(resume.getUuid(), resume);
    }

    @Override
    protected Resume pull(int index, String uuid) {
        return storage.get(uuid);
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
