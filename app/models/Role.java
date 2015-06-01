package models;


import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="roles")
public class Role extends Model {

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="roles_seq")
    private Integer roleId;

    private String roleTitle;

    @OneToMany(mappedBy="userRole")
    private List<User> users;

    //Constructors

    public Role() {
    }

    public Role(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    //Getters and setters of class fields

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public List<User> getUsers() {
        return users;
    }

    //Getters JSON

    public ObjectNode getRoleInfoJSON() {
        ObjectNode getRoleInfoJSON = Json.newObject();

        getRoleInfoJSON.put("roleId", this.roleId);
        getRoleInfoJSON.put("roleTitle", this.roleTitle);
        getRoleInfoJSON.put("usersCount", this.users.size());

        return getRoleInfoJSON;
    }
}
