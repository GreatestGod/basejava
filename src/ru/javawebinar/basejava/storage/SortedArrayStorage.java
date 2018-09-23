package ru.javawebinar.basejava.storage;

import ru.javawebinar.basejava.model.Resume;

import java.util.Arrays;
import java.util.Collections;

public class SortedArrayStorage extends AbstractArrayStorage{

    @Override
    protected void insertElement(Resume r, int index) {
        int indexToInsert = 0;
        for (int i = 0; i < size; i++) {
            if (r.compareTo(storage[i]) > 0) {
                indexToInsert = i + 1;
            } else {
                break;
            }
        }
        Resume[] tmpResumeArray = new Resume[size - indexToInsert];
        System.arraycopy(storage, indexToInsert, tmpResumeArray, 0, size - indexToInsert);
        storage[indexToInsert] = r;
        System.arraycopy(tmpResumeArray, 0, storage, ++indexToInsert, tmpResumeArray.length);
        size++;
    }

    @Override
    protected void deleteElement(int index) {
        int nextIndex = index + 1;
        System.arraycopy(storage, nextIndex, storage, index, size - index);
        size--;
    }

    @Override
    protected int getIndex(String uuid) {
        Resume searchKey = new Resume();
        searchKey.setUuid(uuid);
        return Arrays.binarySearch(storage, 0, size, searchKey);
    }
}
