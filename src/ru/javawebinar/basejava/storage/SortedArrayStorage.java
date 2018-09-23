package ru.javawebinar.basejava.storage;

import ru.javawebinar.basejava.model.Resume;

import java.util.Arrays;
import java.util.Collections;

public class SortedArrayStorage extends AbstractArrayStorage{

    @Override
    public void save(Resume r) {
        if (size == 0) {
            storage[0] = r;
            size++;
        } else if (getIndex(r.getUuid()) >= 0) {
            System.out.println("Resume " + r.getUuid() + " already exist");
        } else if (size >= STORAGE_LIMIT) {
            System.out.println("Storage overflow");
        } else {
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
    }

    @Override
    public void delete(String uuid) {
        int index = getIndex(uuid);
        if (index < 0) {
            System.out.println("Resume " + uuid + " not exist");
        } else {
            int nextIndex = index + 1;
            System.arraycopy(storage, nextIndex, storage, index, size - index);
            size--;
        }
    }

    @Override
    protected int getIndex(String uuid) {
        Resume searchKey = new Resume();
        searchKey.setUuid(uuid);
        System.out.println(Arrays.binarySearch(storage, 0, size, searchKey) + " Это вернул поиск");
        return Arrays.binarySearch(storage, 0, size, searchKey);
    }
}
