package com.burskey.property;

import org.junit.Test;

import static com.burskey.property.LambdaResourceLoader.ReadFile;
import static org.junit.Assert.*;

public class ResourcesIT {


    @Test
    public void testReadFile() {
        String data = ReadFile("classpath:resources.txt");
        assertNotNull(data);
    }

    @Test
    public void testLoadLambdaResource() {
        LambdaResourceLoader loader = LambdaResourceLoader.Build("Stage");
        assertNotNull(loader);
        assertNotEquals(0, loader.map.size());
        assertEquals(3, loader.map.size());
    }


    @Test
    public void testSaveExists() {
        LambdaResourceLoader loader = LambdaResourceLoader.Build("Stage");
        assertNotNull(loader);
        assertNotNull(loader.get(LambdaResourceLoader.Thing.Save));
    }

    @Test
    public void testFindExists() {
        LambdaResourceLoader loader = LambdaResourceLoader.Build("Stage");
        assertNotNull(loader);
        assertNotNull(loader.get(LambdaResourceLoader.Thing.FindByID));
    }

    @Test
    public void testFindByCategoryAndNameExists() {
        LambdaResourceLoader loader = LambdaResourceLoader.Build("Stage");
        assertNotNull(loader);
        assertNotNull(loader.get(LambdaResourceLoader.Thing.FindByCategoryAndName));
    }

}
