package controllers;

import com.avaje.ebean.Ebean;
import models.Administrator;
import models.User;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

@Security.Authenticated(Authenticator.class)
public class Application extends Controller {

    private Boolean isUser() {
        Crypto crypto = play.Play.application().injector().instanceOf(Crypto.class);
        return null == Ebean.find(Administrator.class).where().ilike("login", crypto.decryptAES(session("current_user"))).findUnique();
    }

    private String getName() {
        Crypto crypto = play.Play.application().injector().instanceOf(Crypto.class);
        if (isUser()) {
            User user = Ebean.find( User.class, Integer.parseInt( crypto.decryptAES( session("current_user") ) ) );
            return user.getUserFirstName() + " " + user.getUserLastName();
        } else {
            Administrator admin = Ebean.find(Administrator.class).where().ilike("login", crypto.decryptAES(session("current_user"))).findUnique();
            return admin.getLogin();
        }
    }

    public Result student() {
        if (isUser()) {
            return ok(student.render( getName() ));
        } else {
            return redirect(controllers.routes.Application.admin());
        }
    }

    public Result admin() {
        if (!isUser()) return ok(admin_cab.render(getName()));
        else return redirect(controllers.routes.Application.student());
    }

    public Result dashboard(String page) {
        if (!isUser()) {
                 if (page.equals("users"))      return ok(admin_users.render( getName() ));
            else if (page.equals("faculty"))    return ok(admin_faculties.render( getName() ));
            else if (page.equals("cats"))       return ok(admin_categories.render( getName() ));
            else if (page.equals("subcats"))    return ok(admin_subcats.render( getName() ));
            else if (page.equals("roles"))      return ok(admin_roles.render( getName() ));
            else if (page.equals("notes"))      return ok(notes.render(""));
            else                                return notFound(notFound.render(""));
        } else {
            return redirect(controllers.routes.Application.student());
        }
    }
}
