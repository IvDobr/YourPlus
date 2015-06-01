package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity
@Table(name="md")
public class Moderation extends Model{

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="md_seq")
    private int mdId;

    @ManyToOne
    private User mdUser;

    @ManyToOne
    private Faculty mdFaculty;

    //Constructors

    public Moderation() {
    }

    public Moderation(User mdUser, Faculty mdFaculty) {
        this.mdUser = mdUser;
        this.mdFaculty = mdFaculty;
    }

    //Getters and setters of class fields

    public int getMdId() {
        return mdId;
    }

    public void setMdId(int mdId) {
        this.mdId = mdId;
    }

    public User getMdUser() {
        return mdUser;
    }

    public void setMdUser(User mdUser) {
        this.mdUser = mdUser;
    }

    public Faculty getMdFaculty() {
        return mdFaculty;
    }

    public void setMdFaculty(Faculty mdFaculty) {
        this.mdFaculty = mdFaculty;
    }

    //Getters JSON
}
