package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="faculties")
public class Faculty extends Model {

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="faculties_seq")
    private Integer fclId;

    @Column(unique = true)
    private String fclLongTitle;

    private String fclTitle;

    @Column(columnDefinition="VARCHAR(2047)")
    private String fclAdress;

    @OneToMany(mappedBy="userFaculty")
    private List<User> users;

    @OneToMany(mappedBy="mdFaculty")
    private List<Moderation> moderations;

    //Constructors

    public Faculty() {
    }

    public Faculty(String fclLongTitle, String fclTitle, String fclAdress) {
        this.fclLongTitle = fclLongTitle;
        this.fclTitle = fclTitle;
        this.fclAdress = fclAdress;
    }

    //Getters and setters of class fields

    public Integer getFclId() {
        return fclId;
    }

    public void setFclId(Integer fclId) {
        this.fclId = fclId;
    }

    public String getFclLongTitle() {
        return fclLongTitle;
    }

    public void setFclLongTitle(String fclLongTitle) {
        this.fclLongTitle = fclLongTitle;
    }

    public String getFclTitle() {
        return fclTitle;
    }

    public void setFclTitle(String fclTitle) {
        this.fclTitle = fclTitle;
    }

    public String getFclAdress() {
        return fclAdress;
    }

    public void setFclAdress(String fclAdress) {
        this.fclAdress = fclAdress;
    }

    public List<Moderation> getModerations() {
        return moderations;
    }

    public List<User> getUsers() {
        return users;
    }

    //Getters JSON

    public ObjectNode getFacultyInfoJSON() {
        ObjectNode getFacultyInfoJSON = Json.newObject();

        getFacultyInfoJSON.put("fclId", this.fclId);
        getFacultyInfoJSON.put("fclLongTitle", this.fclLongTitle);
        getFacultyInfoJSON.put("fclTitle", this.fclTitle);
        getFacultyInfoJSON.put("fclAdress", this.fclAdress);
        getFacultyInfoJSON.put("fclStudCount", this.users.size());
        getFacultyInfoJSON.put("fclModerCount", this.moderations.size());

        return getFacultyInfoJSON;
    }
}
