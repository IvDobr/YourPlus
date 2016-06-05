package controllers;

import com.avaje.ebean.Ebean;
import models.User;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

@Security.Authenticated(Authenticator.class)
public class Application extends Controller {

    private User getUser() {
        return Ebean.find(
                User.class, Integer.parseInt(play.Play.application().injector().instanceOf(Crypto.class).decryptAES(session("current_user"))));
    }
    
    /**
     * 1 - Администратор
     * 2 - Проверяющий
     * 3 - Председатель профбюро
     * 4 - Студент
     * 5 - Наблюдатель
     */
    
    public Result user() {
        User u = getUser();
        Integer role = u.getUserRole().getRoleId();

        switch (role) {
            case 1:
                return ok(admin_cab.render(u.getLogin(), new Stat_struct()));
            case 2:
                return ok(moderation.render( u ));
            case 3:
                return ok(student.render(u));
            case 4:
                return ok(student.render( u ));
            case 5:
                return TODO;
            default:
                return badRequest(notFound.render(""));
        }
    }


    public Result moder() {
        User u = getUser();
        Integer role = u.getUserRole().getRoleId();

        switch (role) {
            case 1:
                return ok(admin_cab.render(u.getLogin(), new Stat_struct()));
            case 2:
                return ok(moderation.render( u ));
            case 3:
                return ok(moderation.render( u ));
            case 4:
                return ok(student.render( u ));
            case 5:
                return TODO;
            default:
                return badRequest(notFound.render(""));
        }
    }


    public Result dashboard(String page) {
        User u = getUser();
        if (1 == u.getUserRole().getRoleId()) {
                 if (page.equals("users"))      return ok(admin_users.render( u.getLogin() ));
            else if (page.equals("faculty"))    return ok(admin_faculties.render( u.getLogin() ));
            else if (page.equals("cats"))       return ok(admin_categories.render( u.getLogin() ));
            else if (page.equals("subcats"))    return ok(admin_subcats.render( u.getLogin() ));
            else if (page.equals("roles"))      return ok(admin_roles.render( u.getLogin() ));
            else if (page.equals("notes"))      return ok(notes.render(""));
            else                                return notFound(notFound.render(""));
        } else {
            return redirect(controllers.routes.Application.user());
        }
    }
}
