package ru.javawebinar.basejava.storage;

import static org.junit.Assert.*;

public class MapStorageTest extends AbstractStorageTest{

    public MapStorageTest() {
        super(new MapStorage());
    }
}