package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Achievement;
import models.Category;
import models.SubCategory;
import models.User;
import play.Logger;
import play.Routes;
import play.libs.Crypto;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class API extends Controller {

    private User getUser() {
        Crypto crypto = play.Play.application().injector().instanceOf(Crypto.class);
        return Ebean.find( User.class, Integer.parseInt( crypto.decryptAES( session("current_user") ) ) );
    }

    private static Date JsonToDate (String jsonDate) {
        SimpleDateFormat formatDateJSON = new SimpleDateFormat();
        formatDateJSON.applyPattern("yyyy-MM-dd");
        try {
            return formatDateJSON.parse(jsonDate);
        } catch (ParseException ex) {
            Logger.error("Ошибка в преобразовании даты:" +
                    ex.getMessage());
            return new java.util.Date();
        }
    }

    public Result checkBenderJSON(){
        ObjectNode result = Json.newObject();
        if ( Testing.checkBender( getUser().getUserId() ) ) {
            result.put("status","OK");
            return ok(result);
        }
        else {
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result setStipJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        if (!getUser().getUserStatus()) return badRequest();

        try{
            User u = getUser();
            u.setUserStip(Ebean.find(Category.class, request.findPath("stip").asInt()));
            u.update();
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            Logger.error("Не удалось сохранить стипендию я" + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    public Result getStipJSON(){
        return ok(Json.newObject().put("stip", getUser().getUserStip().getCatTitle() ));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result newAchievJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        if (!getUser().getUserStatus()) return badRequest();

        try{
            Achievement achievement = new Achievement(
                    getUser(),
                    request.findPath("achTitle").textValue(),
                    JsonToDate(request.findPath("achDate").textValue()),
                    Ebean.find(SubCategory.class).where().ilike("subCatAlias", request.findPath("achSubCat").textValue()).findUnique(),
                    request.findPath("achDop").textValue()
            );
            Ebean.save(achievement);
            result.put("status","OK");
            result.put("id", achievement.getAchId().toString());
            return ok(result);
        } catch(Exception e) {
            Logger.error("Достижение не сохранено! " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result editAchievJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        try{
            Achievement achievement = Ebean.find(Achievement.class, request.findPath("achId").asInt());
            achievement.setAchTitle(request.findPath("achTitle").textValue());
            achievement.setAchDate(JsonToDate(request.findPath("achDate").textValue()));
            achievement.setAchCat(Ebean.find(SubCategory.class).where().ilike("subCatAlias", request.findPath("achSubCat").textValue()).findUnique());
            achievement.setAchDop(request.findPath("achDop").textValue());
            achievement.update();
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            Logger.error("Достижение не сохранено! " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    public Result getAllAchievsJSON(int pageSize, int currentPage, String sortby, String search){

        ObjectNode result = Json.newObject();

        List<String> str = Arrays.asList("achId", "achId desc", "achTitle", "achDate", "achCat", "achPremStatus", "achStipStatus",
                "achTitle desc", "achDate desc", "achCat desc", "achPremStatus desc", "achStipStatus desc");
        if (!str.contains(sortby)) sortby = "achDate desc";

        PagedList<Achievement> acheves = Ebean
                .find(Achievement.class)
                .where()
                .eq("achOwner", getUser())
                .icontains("achTitle", search)
                .orderBy(sortby)
                .findPagedList(currentPage-1, pageSize);

        List<ObjectNode> achievsListJSON = acheves
                .getList()
                .stream()
                .map(Achievement::getAchInfoJSON)
                .collect(Collectors.toList());

        result.put("status","OK");
        result.put("pages", acheves.getTotalPageCount());
        result.set("aches", Json.toJson(achievsListJSON));
        result.put("countAches", acheves.getTotalRowCount());

        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteAchievJSON(){
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        Achievement ach = Ebean.find(Achievement.class, request.findPath("achId").intValue());
        try{
            if (ach.getAchPremStatus() != 1 && ach.getAchStipStatus() != 1) {
                ach.delete();
                result.put("status","OK");
                return ok(result);
            } else {
                result.put("status", "error");
                return badRequest(result);
            }
        } catch(Exception e) {
            Logger.error("Не удалось удалить достижение: " +
                    e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    public Result getUserInfoJSON(){
        ObjectNode result = Json.newObject();
        result.put("status","OK");
        result.set("user", getUser().getUserInfoJSON());
        return ok(result);
    }

    public Result generateAches(int count){
        Testing.achievesToUserRND(count, getUser());
        ObjectNode result = Json.newObject();
        result.put("status","OK");
        return ok(result);
    }

    public Result delAches(){
        ObjectNode result = Json.newObject();
        List<Achievement> aches = Ebean.find(Achievement.class).where().eq("achOwner", getUser()).findList();
        try{
            for(Achievement a: aches) a.delete();
            result.put("status","OK");
            return ok(result);
        } catch(Exception e) {
            result.put("status", "error");
            return badRequest(result);
        }
    }

    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.API.checkBenderJSON(),
                        controllers.routes.javascript.API.setStipJSON(),
                        controllers.routes.javascript.API.getStipJSON(),
                        controllers.routes.javascript.API.getAllAchievsJSON(),
                        controllers.routes.javascript.API.deleteAchievJSON(),
                        controllers.routes.javascript.API.editAchievJSON(),
                        controllers.routes.javascript.API.newAchievJSON(),
                        controllers.routes.javascript.API.getUserInfoJSON(),
                        controllers.routes.javascript.API.generateAches(),
                        controllers.routes.javascript.API.delAches()
                )
        );
    }
}