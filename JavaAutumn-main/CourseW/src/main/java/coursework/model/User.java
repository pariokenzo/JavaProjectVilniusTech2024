package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(unique = true)
    protected String login;
    protected String password;

    protected String name;
    protected String surname;
    //jei daugiau yra, prirasot

    public User(String login, String password, String name, String surname) {
        this.login = login;
        this.cypherPassword(password);
        this.name = name;
        this.surname = surname;
    }
    public void setPassword(String password) {
        this.password = cypherPassword(password);
    }

    private String cypherPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean toCheckPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }
    @Override public String toString() {
        return login + " " + password + " " + name + " " + surname;
    }


}
