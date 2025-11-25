package edu.upc.dsa;

import edu.upc.dsa.models.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static edu.upc.dsa.models.Objects.*;

public class gameManagerTest {
    GameManager gm;

    @Before
    public void setUp() throws Exception {
        this.gm = GameManagerImpl.getInstance();
        gm.addNewObjeto("Espada", "Corta dragones", ESPADA, 10);
        gm.addNewObjeto("Escudo", "Resistente al fuego", ESCUDO, 20);
        gm.addNewObjeto("Pocion", "Recupera energía", POCION, 30);
    }

    @After
    public void tearDown() throws Exception {
        this.gm = null;
    }

    @Test
    public void TestRegister() throws Exception {
        this.gm.Register("name1", "1", "name1@example.com");
        this.gm.Register("name2", "2", "name2@example.com");
        this.gm.Register("name3", "3", "name3@example.com");

        Assert.assertEquals(3, gm.getNumberOfUsersRegistered());

        tearDown();
    }

    @Test
    public void TestLogin() throws Exception {
        this.gm.Register("name1", "1", "name1@example.com");
        this.gm.Register("name2", "2", "name2@example.com");
        this.gm.Register("name3", "3", "name3@example.com");

        User u = gm.getUser("name1");

        Assert.assertEquals("name1", u.getUsername());
        Assert.assertEquals("1", u.getPassword());

        tearDown();
    }

    @Test
    public void TestGetObjectList() throws Exception {
        this.gm.Register("name1", "1", "name1@example.com");
        this.gm.Register("name2", "2", "name2@example.com");
        this.gm.Register("name3", "3", "name3@example.com");

        List<GameObject> l = gm.getListObjects("name1");
        Assert.assertEquals(0, l.size());

        gm.addObjectToUser("name1", gm.getObjectId("Espada"));
        gm.addObjectToUser("name1", gm.getObjectId("Escudo"));

        l = gm.getListObjects("name1");
        Assert.assertEquals(2, l.size());

        tearDown();
    }

    @Test
    public void TestSetObjectToUser() throws Exception {
        this.gm.Register("name1", "1", "name1@example.com");
        this.gm.Register("name2", "2", "name2@example.com");
        this.gm.Register("name3", "3", "name3@example.com");

        gm.addObjectToUser("name1", gm.getObjectId("Espada"));
        gm.addObjectToUser("name2", gm.getObjectId("Escudo"));

        List<GameObject> objetosName1 = gm.getListObjects("name1");
        Assert.assertEquals(3, objetosName1.size());
        Assert.assertEquals("Espada", objetosName1.get(0).getNombre());
        Assert.assertEquals("Espada", objetosName1.get(2).getNombre());

        List<GameObject> objetosName2 = gm.getListObjects("name2");
        Assert.assertEquals(1, objetosName2.size());
        Assert.assertEquals("Escudo", objetosName2.get(0).getNombre());

        tearDown();
    }
}
