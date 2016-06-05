package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Crypto;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.auth;

public class Authorization extends Controller {

    public Result index() {
        if(session("current_user") == null){
            return ok(auth.render(""));
        } else {
            return redirect(controllers.routes.Application.user());
        }
    }

    public Result logIn() {
        DynamicForm requestData = Form.form().bindFromRequest();

        Crypto crypto = play.Play.application().injector().instanceOf(Crypto.class);

        User user = Ebean.find(User.class)
                .where()
                .ilike("login", requestData.get("userLogin"))
                .findUnique();
        if (user != null) {
            if (BCrypt.checkpw(requestData.get("userPass"), user.getPass())) {
                session("current_user", crypto.encryptAES(String.valueOf(user.getUserId())));
                return redirect(controllers.routes.Application.user());
            } else {
                return badRequest(auth.render("Неверный логин или пароль!"));
            }
        } else {
            return badRequest(auth.render("Неверный логин или пароль!"));
        }
    }

    public Result logOut() {
        session().clear();
        return ok(auth.render(""));
    }

    @BodyParser.Of(BodyParser.Json.class) //TODO!!!!!!
    public Result logInAPI() {
        JsonNode request = request().body().asJson();

        Crypto crypto = play.Play.application().injector().instanceOf(Crypto.class);

        User user = Ebean.find(User.class)
                .where()
                .ilike("login", request.findPath("userLogin").textValue())
                .findUnique();
        if (user != null) {
            if (BCrypt.checkpw(request.findPath("userPass").textValue(), user.getPass())) {
                session("current_user", crypto.encryptAES(String.valueOf(user.getUserId())));
                return ok();
            } else {
                return badRequest("Неверный логин или пароль!");
            }
        } else {
            return badRequest("Неверный логин или пароль!");
        }
    }

    public Result isLogAPI() {
        if(session("current_user") == null){
            return badRequest();
        } else {
            return ok();
        }
    }
}