package com.github.bdqfork.client.ops;

import com.github.bdqfork.core.operation.ValueOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

public class ValueOperationTest {
    JRedisClient client;
    ValueOperation operation;

    @Before
    public void setUp() throws Exception {
        client = new JRedisClient("127.0.0.1", 7000, 0);
        client.connect();
        operation = client.OpsForValue();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void set() {
        User user = new User();
        user.username = "test";
        user.password = "pass";
        operation.set("testKey", user);
    }

    @Test
    public void setex() {
    }

    @Test
    public void setpx() {
    }

    @Test
    public void setnx() {
    }

    @Test
    public void setxx() {
    }

    @Test
    public void get() {
        User user = operation.get("testKey");
        System.out.println(user);
    }

    static class User implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        String username;
        String password;

        @Override
        public String toString() {
            return "User{" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
        }
    }

}