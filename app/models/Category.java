package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="categories")
public class Category extends Model {

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="categories_seq")
    private Integer catId;

    @Constraints.Required
    private String catTitle;

    @OneToMany(mappedBy="parentCat")
    private List<SubCategory> subCategories;

    @OneToMany(mappedBy="userStip")
    private List<User> users;

    //Constructors

    public Category() {
    }

    public Category(String catTitle) {
        this.catTitle = catTitle;
    }

    //Getters and setters of class fields

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getCatTitle() {
        return catTitle;
    }

    public void setCatTitle(String catTitle) {
        this.catTitle = catTitle;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public List<User> getUsers() {
        return users;
    }

    //Getters JSON

    public ObjectNode getCategoryInfoJSON() {
        ObjectNode getCategoryInfoJSON = Json.newObject();

        int i=0;
        for(SubCategory s: this.subCategories){
            i+=s.getAchievements().size();
        }

        getCategoryInfoJSON.put("catId", this.catId);
        getCategoryInfoJSON.put("catTitle", this.catTitle);
        getCategoryInfoJSON.put("catSubCategoriesCount", this.subCategories.size());
        getCategoryInfoJSON.put("catAchesCount", i);
        getCategoryInfoJSON.put("catUsersCount", this.users.size());

        return getCategoryInfoJSON;
    }
}
