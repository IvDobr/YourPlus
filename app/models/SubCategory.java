package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="sub_categories")
public class SubCategory extends Model {

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="sub_categories_seq")
    private Integer subCatId;

    @Column(columnDefinition="VARCHAR(4095)")
    private String subCatDefinition;

    private String subCatAlias;

    @ManyToOne
    private Category parentCat;

    @OneToMany(mappedBy="achCat")
    private List<Achievement> achievements;

    //Constructors

    public SubCategory() {
    }

    public SubCategory(String subCatAlias, Category parentCat, String subCatDefinition) {
        this.subCatDefinition = subCatDefinition;
        this.subCatAlias = subCatAlias;
        this.parentCat = parentCat;
    }

    //Getters and setters of class fields

    public Integer getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(Integer subCatId) {
        this.subCatId = subCatId;
    }

    public String getSubCatDefinition() {
        return subCatDefinition;
    }

    public void setSubCatDefinition(String subCatDefinition) {
        this.subCatDefinition = subCatDefinition;
    }

    public Category getParentCat() {
        return parentCat;
    }

    public void setParentCat(Category parentCat) {
        this.parentCat = parentCat;
    }

    public String getSubCatAlias() {
        return subCatAlias;
    }

    public void setSubCatAlias(String subCatAlias) {
        this.subCatAlias = subCatAlias;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    //Getters JSON

    public ObjectNode getSubCatInfoJSON() {
        ObjectNode getSubCatInfoJSON = Json.newObject();

        getSubCatInfoJSON.put("subCatId", this.subCatId);
        getSubCatInfoJSON.put("subCatAlias", this.subCatAlias);
        getSubCatInfoJSON.put("subCatDefinition", this.subCatDefinition);
        getSubCatInfoJSON.put("parentCat", this.parentCat.getCatTitle());
        getSubCatInfoJSON.put("subCatAchesCount", this.achievements.size());

        return getSubCatInfoJSON;
    }
}
