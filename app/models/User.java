package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="users")
public class User extends Model {

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="users_seq")
    private int userId;

    @Constraints.Required
    @Column(unique=true)
    @NotNull
    private String login;

    private String userFirstName;

    private String userLastName;

    @Constraints.Required
    private String pass;

    @ManyToOne
    private Faculty userFaculty;

    private Date userReg;

    @ManyToOne
    private Category userStip;

    @Constraints.Required
    private Boolean userStatus;

    @Constraints.Required
    @ManyToOne
    private Role userRole;

    @OneToMany(mappedBy="achOwner")
    private List<Achievement> achievements;

    @OneToMany(mappedBy="mdUser")
    private List<Moderation> moderations;

    //Constructors

    public User() {
    }

    public User(String login,
                String userFirstName,
                String userLastName,
                String pass,
                Faculty userFaculty,
                Category userStip,
                Boolean userStatus,
                Role userRole) {
        this.login = login;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.pass = BCrypt.hashpw(pass, BCrypt.gensalt());
        this.userFaculty = userFaculty;
        this.userReg = new java.util.Date ();
        this.userStip = userStip;
        this.userStatus = userStatus;
        this.userRole = userRole;
    }

    //Getters and setters of class fields

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = BCrypt.hashpw(pass, BCrypt.gensalt());
    }

    public Faculty getUserFaculty() {
        return userFaculty;
    }

    public void setUserFaculty(Faculty userFaculty) {
        this.userFaculty = userFaculty;
    }

    public Date getUserReg() {
        return userReg;
    }

    public void setUserReg(Date userReg) {
        this.userReg = userReg;
    }

    public Category getUserStip() {
        return userStip;
    }

    public void setUserStip(Category userStip) {
        this.userStip = userStip;
    }

    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public List<Moderation> getModerations() {
        return moderations;
    }

    //Getters JSON

    public ObjectNode getUserInfoJSON() {
        ObjectNode getUserInfoJSON = Json.newObject();

        getUserInfoJSON.put("userId", this.userId);
        getUserInfoJSON.put("userLogin", this.login);
        getUserInfoJSON.put("userFirstName", this.userFirstName);
        getUserInfoJSON.put("userLastName", this.userLastName);
        getUserInfoJSON.put("userFaculty", this.userFaculty.getFclTitle());
        DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        getUserInfoJSON.put("userReg", date.format(this.userReg));
        getUserInfoJSON.put("userStip", this.userStip.getCatTitle());
        if (this.userStatus) {
            getUserInfoJSON.put("userStatus", "Активен");
        } else {
            getUserInfoJSON.put("userStatus", "Не активен");
        }
        getUserInfoJSON.put("userRole", this.userRole.getRoleTitle());

        return getUserInfoJSON;
    }

    public ObjectNode getFullUserInfoJSON() {
        ObjectNode getFullUserInfoJSON = this.getUserInfoJSON();
        getFullUserInfoJSON.put("achCount", this.achievements.size());
        return getFullUserInfoJSON;
    }
}
