package ru.javawebinar.basejava.storage;

import ru.javawebinar.basejava.model.Resume;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage extends AbstractArrayStorage {

    @Override
    protected void fillDeletedElement(int index, String uuid) {
        storage[index] = storage[size - 1];
        storage[size - 1] = null;
        size--;
    }

    @Override
    protected void insertElement(Resume r, int index) {
        storage[size] = r;
        size++;
    }

    @Override
    protected int getIndex(String uuid) {
        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                return i;
            }
        }
        return -1;
    }
}
