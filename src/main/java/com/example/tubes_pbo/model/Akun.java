package com.example.tubes_pbo.model;

public abstract class Akun {
    protected String username;
    protected String password;

    protected Akun() {
    }

    protected Akun(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean login(String username, String password) {
        return this.username != null
                && this.username.equals(username)
                && this.password != null
                && this.password.equals(password);
    }

    public void logout() {
        // no-op placeholder for future session handling
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

