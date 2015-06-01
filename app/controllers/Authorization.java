package controllers;

import com.avaje.ebean.Ebean;
import models.Administrator;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Authorization extends Controller {

    public Result index() {
        if(session("current_user") == null){
            return ok(auth.render(""));
        } else {
            return redirect(controllers.routes.Application.student());
        }
    }

    public Result logIn() {
        DynamicForm requestData = Form.form().bindFromRequest();

        Crypto crypto = play.Play.application().injector().instanceOf(Crypto.class);

        User user = Ebean.find(User.class)
                .where()
                .ilike("login", requestData.get("userLogin"))
                .ilike("pass", requestData.get("userPass"))
                .findUnique();

        Administrator admin = Ebean.find(Administrator.class)
                .where()
                .ilike("login", requestData.get("userLogin"))
                .ilike("pass", requestData.get("userPass"))
                .findUnique();

        if (user != null) {
            session( "current_user", crypto.encryptAES( String.valueOf(user.getUserId()) ) );
            return redirect(controllers.routes.Application.student());
        } else if (admin != null) {
            session( "current_user", crypto.encryptAES( String.valueOf(admin.getLogin()) ) );
            return redirect(controllers.routes.Application.admin());
        } else {
            return badRequest(auth.render("Неверный логин или пароль!"));
        }
    }

    public Result logOut() {
        session().clear();
        return ok(auth.render(""));
    }
}