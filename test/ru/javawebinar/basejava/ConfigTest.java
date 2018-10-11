package ru.javawebinar.basejava;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void getStorageDir() {
        Assert.assertEquals(new File("storage"), Config.get().getStorageDir());
    }

    @Test
    public void getDbUrl() {
        Assert.assertEquals("jdbc:postgresql://localhost:5432/resumes", Config.get().getDbUrl());
    }

    @Test
    public void getDbUser() {
        Assert.assertEquals("postgres", Config.get().getDbUser());
    }

    @Test
    public void getDbPassword() {
        Assert.assertEquals("postgres", Config.get().getDbPassword());
    }
}