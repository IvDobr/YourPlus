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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Admin_API extends Controller {

    //Контроллеры 1 модуля дашборда

    public Result getGeneralInfoJSON() {
        ObjectNode result = Json.newObject();
        ObjectNode info = Json.newObject();

        List<Role> roles = Ebean.find(Role.class).findList();

        info.put("user_count", Ebean.find(User.class).findRowCount());
        info.put("user_1", Ebean.find(User.class).where().eq("userStatus", true).findRowCount());
        info.put("user_2", Ebean.find(User.class).where().eq("userStatus", false).findRowCount());
        info.put("user_3", roles.get(0).getUsers().size());
        info.put("user_4", roles.get(1).getUsers().size());
        info.put("user_5", Ebean.find(Administrator.class).findRowCount());

        info.put("ach_count", Ebean.find(Achievement.class).findRowCount());
        info.put("ach_1", Ebean.find(Achievement.class).where().ilike("achStipStatus", "1").findRowCount());
        info.put("ach_2", Ebean.find(Achievement.class).where().ilike("achPremStatus", "1").findRowCount());
        info.put("ach_3", Ebean.find(Achievement.class).where().ilike("achStipStatus", "0").findRowCount());
        info.put("ach_4", Ebean.find(Achievement.class).where().ilike("achPremStatus", "0").findRowCount());
        info.put("ach_5", Ebean.find(Achievement.class).where().ilike("achStipStatus", "-1").findRowCount());
        info.put("ach_6", Ebean.find(Achievement.class).where().ilike("achPremStatus", "-1").findRowCount());
        info.put("ach_7", Math.rint(100.0 * (
                (double)Ebean.find(Achievement.class).findRowCount() / (double)Ebean.find(User.class).findRowCount())) / 100.0);

        info.put("fcl_count", Ebean.find(Faculty.class).findRowCount());

        info.put("long_count", Ebean.find(SubCategory.class).findRowCount());

        info.put("stip_count", Ebean.find(Category.class).findRowCount());

        result.set("info", info);
        return ok(result);
    }


    //Контроллеры 2 модуля дашборда

    public Result getAllUsersJSON(int pageSize, int currentPage, String sortby, String search){
        ObjectNode result = Json.newObject();

        List<String> str = Arrays.asList(
                "userReg",
                "userReg desc",
                "login",
                "login desc",
                "userLastName",
                "userLastName desc",
                "userFirstName desc",
                "userFirstName",
                "userFaculty desc",
                "userFaculty",
                "userStip desc",
                "userStip",
                "userStatus desc",
                "userStatus",
                "userId desc",
                "userId"
                );

        if (!str.contains(sortby)) sortby = "userId";

        PagedList<User> users = Ebean
                .find(User.class)
                .where()
                .or(
                        Expr.icontains("login", search),
                        Expr.or(
                                Expr.icontains("userLastName", search),
                                Expr.icontains("userFirstName", search)
                        )
                )
                .orderBy(sortby)
                .findPagedList(currentPage - 1, pageSize);

        List<ObjectNode> usersListJSON = users
                .getList()
                .stream()
                .map(User::getFullUserInfoJSON)
                .collect(Collectors.toList());

        result.put("pages", users.getTotalPageCount());
        result.put("countUsers", users.getTotalRowCount());
        result.put("status","OK");
        result.set("users", Json.toJson(usersListJSON));
        return ok(result);
    }

    public Result getUtilTitlesJSON(){
        ObjectNode result = Json.newObject();

        List<String> fclListJson = Ebean
                .find(Faculty.class)
                .findList()
                .stream()
                .map(Faculty::getFclTitle)
                .collect(Collectors.toList());

        List<String> roleListJson = Ebean
                .find(Role.class)
                .findList()
                .stream()
                .map(Role::getRoleTitle)
                .collect(Collectors.toList());

        List<String> catListJson = Ebean
                .find(Category.class)
                .findList()
                .stream()
                .map(Category::getCatTitle)
                .collect(Collectors.toList());

        result.put("status", "OK");
        result.set("faculties", Json.toJson(fclListJson));
        result.set("roles", Json.toJson(roleListJson));
        result.set("stips", Json.toJson(catListJson));
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addUserJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Ebean.save(new User(
                    request.findPath("newUserLogin").textValue(),
                    request.findPath("newUserFirstName").textValue(),
                    request.findPath("newUserLastName").textValue(),
                    request.findPath("newUserPass").textValue(),
                    Ebean.find(Faculty.class).where().ilike("fclTitle", request.findPath("selectedFaculty").textValue()).findUnique(),
                    Ebean.find(Category.class, 1),
                    request.findPath("newUserStatus").booleanValue(),
                    Ebean.find(Role.class).where().ilike("roleTitle", request.findPath("selectedRole").textValue()).findUnique()
            ));
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно создать новый аккаунт!");
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result editUserJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            User u = Ebean.find(User.class, request.findPath("userId").intValue());

            u.setPass(request.findPath("editUserPass").textValue());
            u.setUserFirstName(request.findPath("editUserFirstName").textValue());
            u.setUserLastName(request.findPath("editUserLastName").textValue());
            u.setUserStatus(request.findPath("editUserStatus").booleanValue());
            u.setLogin(request.findPath("editUserLogin").textValue());
            u.setUserFaculty(Ebean.find(Faculty.class).where().ilike("fclTitle", request.findPath("editUserFaculty").textValue()).findUnique());
            u.setUserStip(Ebean.find(Category.class).where().ilike("catTitle", request.findPath("editUserStip").textValue()).findUnique());
            u.setUserRole(Ebean.find(Role.class).where().ilike("roleTitle", request.findPath("editUserRole").textValue()).findUnique());

            Ebean.save(u);
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно изменить аккаунт! " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addUserManyJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        String[] stus_names = request.findPath("newUsers").textValue().split("; *");
        String studlist = "";

        try {
            for (String stud : stus_names) {
                String[] name = stud.split(" ");

                User u = new User(
                        ".123",
                        name[1].replace("_", " "),
                        name[0].replace("_", " "),
                        request.findPath("newUserPass").textValue(),
                        Ebean.find(Faculty.class).where().ilike("fclTitle", request.findPath("selectedFaculty").textValue()).findUnique(),
                        Ebean.find(Category.class, 1),
                        request.findPath("newUserStatus").booleanValue(),
                        Ebean.find(Role.class).where().ilike("roleTitle", request.findPath("selectedRole").textValue()).findUnique()
                );

                Ebean.save(u);
                u.setLogin("student" + u.getUserId());
                u.setPass("pass" + u.getUserId() + Testing.intRandom(1000, 10000000));
                Ebean.update(u);

                studlist += u.getLogin() + " # " + u.getPass() + " # " + u.getUserLastName() + " # " + u.getUserFirstName() + "\n";
            }

            result.put("status","OK");
            result.put("newUsersList", studlist);
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно создать новые аккаунты!");
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeUserJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        User user = User.find.byId(request.findPath("userId").toString());

        Crypto crypto = play.Play.application().injector().instanceOf(Crypto.class);

        if (!crypto.decryptAES(session("current_user")).equals(request.findPath("userId").toString()) && !request.findPath("userId").toString().equals("1")) {
            try {
                for(Achievement ach : user.getAchievements()) ach.delete();
                for(Moderation m : user.getModerations()) m.delete();

                user.delete();
                result.put("status", "OK");
                return ok(result);
            } catch (Exception e) {
                Logger.error("Невозможно удалить аккаунт!");
                result.put("status", "error");
                return badRequest(result);
            }
        } else {
            result.put("status", "error");
            return badRequest(result);
        }
    }

    public Result generateUsers(int count){
        new Testing().UserRandomize(count, count);
        ObjectNode result = Json.newObject();
        result.put("status","OK");
        return ok(result);
    }


    //Контроллеры 3 модуля дашборда

    public Result getAllFacultiesJSON(String search){
        ObjectNode result = Json.newObject();

        int count = Ebean.find(Faculty.class).findRowCount();

        List<ObjectNode> faculties = Ebean
                .find(Faculty.class)
                .where()
                .or(
                        Expr.icontains("fclLongTitle", search),
                        Expr.icontains("fclTitle", search)
                )
                .findList()
                .stream()
                .map(Faculty::getFacultyInfoJSON)
                .collect(Collectors.toList());

        result.put("countFaculties", count);
        result.put("status","OK");
        result.set("faculties", Json.toJson(faculties));
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addFacultyJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Ebean.save(new Faculty(
                    request.findPath("newFacultyLongTitle").textValue(),
                    request.findPath("newFacultyTitle").textValue(),
                    request.findPath("newFacultyAdress").textValue()
            ));
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно создать новый факультет!");
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result editFacultyJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Faculty f = Ebean.find(Faculty.class, request.findPath("editFacultyId").intValue());

            f.setFclLongTitle(request.findPath("editFacultyLongTitle").textValue());
            f.setFclTitle(request.findPath("editFacultyTitle").textValue());
            f.setFclAdress(request.findPath("editFacultyAdress").textValue());

            Ebean.save(f);
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно изменить факультет! " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeFacultyJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        Faculty fcl = Ebean.find(Faculty.class, request.findPath("fclId").intValue());

        try {
            for(Moderation m : fcl.getModerations()) m.delete();
            for(User u : fcl.getUsers()) {
                u.setUserFaculty(Ebean.find(Faculty.class, 1));
                Ebean.save(u);
            }

            fcl.delete();
            result.put("status", "OK");
            return ok(result);
            } catch (Exception e) {
                Logger.error("Невозможно удалить факультет!");
                result.put("status", "error");
                return badRequest(result);
            }
    }


    //Контроллеры 4 модуля дашборда

    public Result getAllCategoriesJSON(String search){
        ObjectNode result = Json.newObject();

        int count = Ebean.find(Category.class).findRowCount();

        List<ObjectNode> categories = Ebean
                .find(Category.class)
                .where()
                .icontains("catTitle", search)
                .findList()
                .stream()
                .map(Category::getCategoryInfoJSON)
                .collect(Collectors.toList());

        result.put("countCategories", count);
        result.put("status","OK");
        result.set("categories", Json.toJson(categories));
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addCategoryJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Ebean.save(new Category(request.findPath("newCategoryTitle").textValue()));
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно создать новую категорию!");
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result editCategoryJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Category cat = Ebean.find(Category.class, request.findPath("editCategoryId").intValue());

            cat.setCatTitle(request.findPath("editCategoryTitle").textValue());

            Ebean.save(cat);
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно изменить категорию! " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeCategoryJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        Category cat = Ebean.find(Category.class, request.findPath("catId").intValue());

        try {
            for(SubCategory s : cat.getSubCategories()) {
                for(Achievement a: s.getAchievements()) a.delete();
                s.delete();
            }
            for(User u : cat.getUsers()) {
                u.setUserStip(Ebean.find(Category.class, 1));
                Ebean.save(u);
            }
            cat.delete();
            result.put("status", "OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно удалить категорию!");
            result.put("status", "error");
            return badRequest(result);
        }
    }


    //Контроллеры 5 модуля дашборда

    public Result getAllRolesJSON(String search){
        ObjectNode result = Json.newObject();

        int count = Ebean.find(Role.class).findRowCount();

        List<ObjectNode> roles = Ebean
                .find(Role.class)
                .where()
                .icontains("roleTitle", search)
                .findList()
                .stream()
                .map(Role::getRoleInfoJSON)
                .collect(Collectors.toList());

        result.put("countRoles", count);
        result.put("status","OK");
        result.set("roles", Json.toJson(roles));
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addRoleJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Ebean.save(new Role(request.findPath("newRoleTitle").textValue()));
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно создать новую роль!");
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result editRoleJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Role role = Ebean.find(Role.class, request.findPath("editRoleId").intValue());

            role.setRoleTitle(request.findPath("editRoleTitle").textValue());

            Ebean.save(role);
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно изменить роль! " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeRoleJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        if (1==request.findPath("roleId").intValue()) return badRequest();
        Role role = Ebean.find(Role.class, request.findPath("roleId").intValue());
        try {
            for(User u : role.getUsers()) {
                u.setUserRole(Ebean.find(Role.class, 1));
                Ebean.save(u);
            }
            role.delete();
            result.put("status", "OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно удалить роль!");
            result.put("status", "error");
            return badRequest(result);
        }
    }


    //Контроллеры 6 модуля дашборда

    public Result getAllSubCatsJSON(String search){
        ObjectNode result = Json.newObject();

        int count = Ebean.find(SubCategory.class).findRowCount();
        List<String> categories = Ebean.find(Category.class).findList().stream().map(Category::getCatTitle).collect(Collectors.toList());

        List<ObjectNode> subCats = Ebean
                .find(SubCategory.class)
                .where()
                .or(
                        Expr.icontains("subCatDefinition", search),
                        Expr.icontains("subCatAlias", search)
                )
                .findList()
                .stream()
                .map(SubCategory::getSubCatInfoJSON)
                .collect(Collectors.toList());

        result.put("countSubCats", count);
        result.put("status","OK");
        result.set("subCats", Json.toJson(subCats));
        result.set("cats", Json.toJson(categories));
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addSubCatJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            Ebean.save(new SubCategory(
                    request.findPath("newSubCatAlias").textValue(),
                    Ebean.find(Category.class).where().like("catTitle", request.findPath("newParentCat").textValue()).findUnique(),
                    request.findPath("newSubCatDefinition").textValue()
            ));
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно создать новую подгатегорию!");
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result editSubCatJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();

        try {
            SubCategory sc = Ebean.find(SubCategory.class, request.findPath("editSubCatId").intValue());

            sc.setSubCatAlias(request.findPath("editSubCatAlias").textValue());
            sc.setSubCatDefinition(request.findPath("editSubCatDefinition").textValue());
            sc.setParentCat(Ebean.find(Category.class).where().like("catTitle", request.findPath("editParentCat").textValue()).findUnique());

            Ebean.save(sc);
            result.put("status","OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно изменить подгатегорию! " + e.getMessage());
            result.put("status", "error");
            return badRequest(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeSubCatJSON() {
        JsonNode request = request().body().asJson();
        ObjectNode result = Json.newObject();
        SubCategory sc = Ebean.find(SubCategory.class, request.findPath("subCatId").intValue());

        try {
            for(Achievement a: sc.getAchievements()) {
                a.setAchCat(Ebean.find(SubCategory.class, 1));
                a.save();
            }

            sc.delete();
            result.put("status", "OK");
            return ok(result);
        } catch (Exception e) {
            Logger.error("Невозможно удалить подгатегорию!");
            result.put("status", "error");
            return badRequest(result);
        }
    }


    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.Admin_API.getGeneralInfoJSON(),
                        controllers.routes.javascript.Admin_API.getAllUsersJSON(),
                        controllers.routes.javascript.Admin_API.getUtilTitlesJSON(),
                        controllers.routes.javascript.Admin_API.addUserJSON(),
                        controllers.routes.javascript.Admin_API.editUserJSON(),
                        controllers.routes.javascript.Admin_API.addUserManyJSON(),
                        controllers.routes.javascript.Admin_API.removeUserJSON(),
                        controllers.routes.javascript.Admin_API.generateUsers(),
                        controllers.routes.javascript.Admin_API.getAllFacultiesJSON(),
                        controllers.routes.javascript.Admin_API.addFacultyJSON(),
                        controllers.routes.javascript.Admin_API.editFacultyJSON(),
                        controllers.routes.javascript.Admin_API.removeFacultyJSON(),
                        controllers.routes.javascript.Admin_API.getAllCategoriesJSON(),
                        controllers.routes.javascript.Admin_API.addCategoryJSON(),
                        controllers.routes.javascript.Admin_API.editCategoryJSON(),
                        controllers.routes.javascript.Admin_API.removeCategoryJSON(),
                        controllers.routes.javascript.Admin_API.getAllRolesJSON(),
                        controllers.routes.javascript.Admin_API.addRoleJSON(),
                        controllers.routes.javascript.Admin_API.editRoleJSON(),
                        controllers.routes.javascript.Admin_API.removeRoleJSON(),
                        controllers.routes.javascript.Admin_API.getAllSubCatsJSON(),
                        controllers.routes.javascript.Admin_API.addSubCatJSON(),
                        controllers.routes.javascript.Admin_API.editSubCatJSON(),
                        controllers.routes.javascript.Admin_API.removeSubCatJSON()
                )
        );
    }
}
