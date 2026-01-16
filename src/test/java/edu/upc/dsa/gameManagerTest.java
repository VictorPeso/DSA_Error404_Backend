package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
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
        gm.clear();
        gm.addNewObjeto("Espada", "Corta dragones", ESPADA, 10);
        gm.addNewObjeto("Escudo", "Resistente al fuego", ESCUDO, 20);
        gm.addNewObjeto("Pocion", "Recupera energía", POCION, 30);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void TestRegister() throws Exception {
        this.gm.Register("name1", "password123", "name1@example.com");
        this.gm.Register("name2", "password456", "name2@example.com");
        this.gm.Register("name3", "password789", "name3@example.com");

        Assert.assertEquals(3, gm.getNumberOfUsersRegistered());
    }

    @Test
    public void TestLogin() throws Exception {
        // Registrar usuario con contraseña válida
        this.gm.Register("name1", "password123", "name1@example.com");

        // Login exitoso debe devolver el usuario
        User u = gm.LogIn("name1", "password123");
        Assert.assertNotNull(u);
        Assert.assertEquals("name1", u.getUsername());
        
        // La contraseña NO debe ser el texto plano, debe ser un hash BCrypt
        Assert.assertNotEquals("password123", u.getPassword());
        Assert.assertTrue("Password should be BCrypt hash", u.getPassword().startsWith("$2a$"));
    }

    @Test(expected = FailedLoginException.class)
    public void TestLoginWithWrongPassword() throws Exception {
        // Registrar usuario
        this.gm.Register("name1", "password123", "name1@example.com");
        
        // Intentar login con contraseña incorrecta - debe lanzar excepción
        gm.LogIn("name1", "wrongpassword");
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void TestRegisterDuplicateUser() throws Exception {
        // Registrar usuario
        this.gm.Register("name1", "password123", "name1@example.com");
        
        // Intentar registrar mismo usuario - debe lanzar excepción
        this.gm.Register("name1", "password456", "different@example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestRegisterWeakPassword() throws Exception {
        // Contraseña muy corta - debe lanzar excepción
        this.gm.Register("name1", "123", "name1@example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestRegisterInvalidEmail() throws Exception {
        // Email inválido - debe lanzar excepción
        this.gm.Register("name1", "password123", "invalid-email");
    }

    @Test
    public void TestGetObjectList() throws Exception {
        this.gm.Register("name1", "password123", "name1@example.com");
        this.gm.Register("name2", "password456", "name2@example.com");
        this.gm.Register("name3", "password789", "name3@example.com");

        List<UserGameObject> l = gm.getListObjects("name1");
        Assert.assertEquals(0, l.size());

        gm.addObjectToUser("name1", gm.getObjectId("Espada"));
        gm.addObjectToUser("name1", gm.getObjectId("Escudo"));

        l = gm.getListObjects("name1");
        Assert.assertEquals(2, l.size());
    }

    @Test
    public void TestSetObjectToUser() throws Exception {
        this.gm.Register("name1", "password123", "name1@example.com");
        this.gm.Register("name2", "password456", "name2@example.com");
        this.gm.Register("name3", "password789", "name3@example.com");

        gm.addObjectToUser("name1", gm.getObjectId("Espada"));
        gm.addObjectToUser("name2", gm.getObjectId("Escudo"));

        List<UserGameObject> objetosName1 = gm.getListObjects("name1");
        Assert.assertEquals(1, objetosName1.size());
        Assert.assertEquals("Espada", objetosName1.get(0).getGameObject().getNombre());

        List<UserGameObject> objetosName2 = gm.getListObjects("name2");
        Assert.assertEquals(1, objetosName2.size());
        Assert.assertEquals("Escudo", objetosName2.get(0).getGameObject().getNombre());
    }

    @Test
    public void TestPurchaseObject() throws Exception {
        // Registrar usuario (inicia con 100 monedas)
        this.gm.Register("testuser", "password123", "test@example.com");
        
        User u = gm.getUser("testuser");
        int initialMoney = u.getMonedas();
        Assert.assertEquals(100, initialMoney);
        
        // Comprar objeto de 10 monedas
        String espadaId = gm.getObjectId("Espada");
        User updatedUser = gm.purchaseObject("testuser", espadaId);
        
        // Verificar que se descontaron las monedas
        Assert.assertEquals(90, updatedUser.getMonedas());
        
        // Verificar que el usuario tiene el objeto
        List<UserGameObject> objects = gm.getListObjects("testuser");
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals("Espada", objects.get(0).getGameObject().getNombre());
    }

    @Test(expected = InsufficientFundsException.class)
    public void TestPurchaseObjectInsufficientFunds() throws Exception {
        // Registrar usuario (inicia con 100 monedas)
        this.gm.Register("pooruser", "password123", "poor@example.com");
        
        // Crear objeto muy caro
        gm.addNewObjeto("ObjetoCaro", "Muy caro", ESPADA, 200);
        String objetoCaroId = gm.getObjectId("ObjetoCaro");
        
        // Intentar comprar objeto más caro que el saldo - debe lanzar excepción
        gm.purchaseObject("pooruser", objetoCaroId);
    }

    @Test(expected = UserNotFoundException.class)
    public void TestPurchaseObjectUserNotFound() throws Exception {
        String espadaId = gm.getObjectId("Espada");
        
        // Intentar comprar con usuario inexistente - debe lanzar excepción
        gm.purchaseObject("nonexistent", espadaId);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void TestPurchaseObjectNotFound() throws Exception {
        this.gm.Register("testuser", "password123", "test@example.com");
        
        // Intentar comprar objeto inexistente - debe lanzar excepción
        gm.purchaseObject("testuser", "nonexistent-id");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void TestGetObjectIdNotFound() throws Exception {
        // Intentar obtener ID de objeto inexistente - debe lanzar excepción
        gm.getObjectId("ObjetoInexistente");
    }
}
