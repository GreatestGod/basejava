package ru.javawebinar.basejava.storage;

import ru.javawebinar.basejava.model.Resume;

import java.util.List;

public class StorageStrategy implements Storage {
    Storage storage;

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public void update(Resume r) {
        storage.update(r);
    }

    @Override
    public void save(Resume r) {
        storage.save(r);
    }

    @Override
    public Resume get(String uuid) {
        return storage.get(uuid);
    }

    @Override
    public void delete(String uuid) {
        storage.delete(uuid);
    }

    @Override
    public List<Resume> getAllSorted() {
        return storage.getAllSorted();
    }

    @Override
    public int size() {
        return storage.size();
    }
}
