package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="admins")
public class Administrator extends Model{

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="admins_seq")
    private int adminId;

    @Constraints.Required
    @Column(unique=true)
    @NotNull
    private String login;

    @Constraints.Required
    private String pass;

    //Constructors

    public Administrator() {
    }

    public Administrator(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    //Getters and setters of class fields

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    //Getters JSON
}
