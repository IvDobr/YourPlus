package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="achievements")
public class Achievement extends Model {

    private static final long serialVersionUID = 1L;

    //Fields

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="achievements_seq")
    private Integer achId;

    @ManyToOne
    private User achOwner;

    private String achTitle;

    private Date achDate;

    @ManyToOne
    private SubCategory achCat;

    @Column(columnDefinition="VARCHAR(4095)")
    private String achComment;

    @Column(columnDefinition="VARCHAR(4095)")
    private String achDop;

    private Integer achPremStatus;

    private Integer achStipStatus;

    //Constructors

    public Achievement() {
    }

    public Achievement(User achOwner, String achTitle, Date achDate, SubCategory achCat, String achDop) {
        this.achOwner = achOwner;
        this.achTitle = achTitle;
        this.achDate = achDate;
        this.achCat = achCat;
        this.achDop = achDop;
        this.achPremStatus = 0;
        this.achStipStatus = 0;
        this.achComment = "";
    }

    public Achievement(User achOwner,
                       String achTitle,
                       Date achDate,
                       SubCategory achCat,
                       String achComment,
                       String achDop,
                       Integer achPremStatus,
                       Integer achStipStatus) {
        this.achOwner = achOwner;
        this.achTitle = achTitle;
        this.achDate = achDate;
        this.achCat = achCat;
        this.achComment = achComment;
        this.achDop = achDop;
        this.achPremStatus = achPremStatus;
        this.achStipStatus = achStipStatus;
    }

    //Getters and setters of class fields

    public Integer getAchId() {
        return achId;
    }

    public void setAchId(Integer achId) {
        this.achId = achId;
    }

    public User getAchOwner() {
        return achOwner;
    }

    public void setAchOwner(User achOwner) {
        this.achOwner = achOwner;
    }

    public String getAchTitle() {
        return achTitle;
    }

    public void setAchTitle(String achTitle) {
        this.achTitle = achTitle;
    }

    public Date getAchDate() {
        return achDate;
    }

    public void setAchDate(Date achDate) {
        this.achDate = achDate;
    }

    public SubCategory getAchCat() {
        return achCat;
    }

    public void setAchCat(SubCategory achCat) {
        this.achCat = achCat;
    }

    public String getAchComment() {
        return achComment;
    }

    public void setAchComment(String achComment) {
        this.achComment = achComment;
    }

    public String getAchDop() {
        return achDop;
    }

    public void setAchDop(String achDop) {
        this.achDop = achDop;
    }

    public Integer getAchPremStatus() {
        return achPremStatus;
    }

    public void setAchPremStatus(Integer achPremStatus) {
        this.achPremStatus = achPremStatus;
    }

    public Integer getAchStipStatus() {
        return achStipStatus;
    }

    public void setAchStipStatus(Integer achStipStatus) {
        this.achStipStatus = achStipStatus;
    }

    //Getters JSON

    public ObjectNode getAchInfoJSON() {
        ObjectNode getAchInfoJSON = Json.newObject();


        getAchInfoJSON.put("achId", this.achId);
        getAchInfoJSON.put("achTitle", this.achTitle);
        getAchInfoJSON.put("achCat", this.achCat.getParentCat().getCatTitle());
        getAchInfoJSON.put("achSubCat", this.achCat.getSubCatAlias());
        DateFormat date = new SimpleDateFormat("dd.MM.yyyy");
        getAchInfoJSON.put("achDate", date.format(this.achDate));
        getAchInfoJSON.put("achComment", this.achComment);
        getAchInfoJSON.put("achDop", this.achDop);
        getAchInfoJSON.put("achPremStatus", this.achPremStatus);
        getAchInfoJSON.put("achStipStatus", this.achStipStatus);

        return getAchInfoJSON;
    }
}
