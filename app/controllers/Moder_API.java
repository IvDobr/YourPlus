package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
import play.Routes;
import play.libs.Crypto;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Moder_API extends Controller {

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
            Logger.warn("Ошибка 1 в преобразовании даты: " +
                    ex.getMessage());
        }

        formatDateJSON.applyPattern("dd.MM.yyyy");
        try {
            return formatDateJSON.parse(jsonDate);
        } catch (ParseException exx) {
            Logger.error("Ошибка 2 в преобразовании даты: " +
                    exx.getMessage());
            return new Date();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result newAchievJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        if (!getUser().getUserStatus()) return badRequest();

        Logger.debug(request.toString());
        Logger.debug(getUser().getPass());
        Logger.debug(request.findPath("achTitle").textValue());
        Logger.debug(JsonToDate(request.findPath("achDate").textValue()).toString());
        Logger.debug(request.findPath("subCatAlias").textValue());
        Logger.debug(request.findPath("achDop").textValue());



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
            Logger.error("Достижение не сохранено! " + e.toString());
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
        Integer i = request.findPath("achId").intValue();
        return deleteAchiev(i.toString());
    }

    public Result deleteAchiev(String idStr){
        Integer id;
        try {
            id = new Integer(idStr);
            if (0==id) throw new Exception("Id is null");
        } catch (Exception e) {
            Logger.error(e.toString());
            return badRequest();
        }

        ObjectNode result = Json.newObject();
        try{
            Achievement ach = Ebean.find(Achievement.class, id);
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


    /**
     * 1 - Администратор
     * 2 - Проверяющий
     * 3 - Председатель профбюро
     * 4 - Студент
     * 5 - Наблюдатель
     */

    public Result getCountAches() {
        User user = getUser();
        switch (user.getUserRole().getRoleId()) {
            case 4:
                return ok("0");
            case 3:
                List<Faculty> fcls = new ArrayList<>();
                for(Moderation mdr: user.getModerations()) {
                    fcls.add(mdr.getMdFaculty());
                }
                List<User> users = Ebean.find(User.class).where().in("userFaculty", fcls).findList();
                List<Achievement> aches = Ebean.find(Achievement.class).where()
                        .and(
                                Expr.contains("achStipStatus", "0"),
                                Expr.in("achOwner", users)
                        ).findList();
                return ok(aches.size()+"");
            case 2:
                return TODO;
            default:
                return badRequest();
        }
    }

    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        routes.javascript.Moder_API.getAllAchievsJSON(),
                       // routes.javascript.Moder_API.deleteAchievJSON(),
                        //routes.javascript.Moder_API.editAchievJSON(),
                        routes.javascript.API.getUserInfoJSON(),
                        routes.javascript.API.getCountAches()
                )
        );
    }
}