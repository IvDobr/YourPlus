package controllers;

import com.avaje.ebean.Ebean;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Stat_struct {

    /**
     СТРУКТУРА

     Пользователи
         - Всего ()
         - Активных ()
         - Не активных ()
         - Роли пользователя ()
             - ... ()
             - ... ()
             - ... ()

     Достижения
         - Всего ()
         - На стипендию
             - Принято ()
             - Рассматривается ()
             - Отклонено ()
         - На премию
             - Принято ()
             - Рассматривается ()
             - Отклонено ()
         - Достижений на пользователя ()

     Факультеты
         - Всего ()
             - ...
                 - кол-во студентов ()
                 - достижения ()
                 - модераторов ()

     Стипендии
         - Всего ()
            - ... (Подкатегории )
     */

    public final Integer all_users;
    public final Integer active_users;
    public final Integer nonactive_users;
    public final Integer users_roles;
    public final TreeMap<String, Integer> users_roles_list;

    public final Integer all_achievements;
    public final Double achievements_per_user;
    public final ArrayList<Integer> ach_on_stip;
    public final ArrayList<Integer> ach_on_prem;

    public final Integer all_fcl;
    public final TreeMap<String, ArrayList<Integer>> fcls;

    public final Integer all_cats;
    public final TreeMap<String, Integer> cats;

    public Stat_struct() {
        all_users = Ebean.find(User.class).findRowCount();
        active_users = Ebean.find(User.class).where().eq("userStatus", true).findRowCount();
        nonactive_users = Ebean.find(User.class).where().eq("userStatus", false).findRowCount();
        List<Role> roles= Ebean.find(Role.class).findList();
        users_roles = roles.size();
        users_roles_list = new TreeMap<>();
        for (Role r: roles) {
            users_roles_list.put(r.getRoleTitle(), r.getUsers().size());
        }

        all_achievements = Ebean.find(Achievement.class).findRowCount();
        achievements_per_user = Math.rint(100.0 * ((double)all_achievements / (double)all_users) / 100.0);
        ach_on_stip = new ArrayList<>();
        ach_on_stip.add(Ebean.find(Achievement.class).where().ilike("achStipStatus", "1").findRowCount());
        ach_on_stip.add(Ebean.find(Achievement.class).where().ilike("achStipStatus", "0").findRowCount());
        ach_on_stip.add(Ebean.find(Achievement.class).where().ilike("achStipStatus", "-1").findRowCount());
        ach_on_prem = new ArrayList<>();
        ach_on_prem.add(Ebean.find(Achievement.class).where().ilike("achPremStatus", "1").findRowCount());
        ach_on_prem.add(Ebean.find(Achievement.class).where().ilike("achPremStatus", "0").findRowCount());
        ach_on_prem.add(Ebean.find(Achievement.class).where().ilike("achPremStatus", "-1").findRowCount());

        List<Faculty> fcl_list= Ebean.find(Faculty.class).findList();
        fcl_list.remove(0);//Убираем нулевой факультет
        all_fcl = fcl_list.size();
        fcls = new TreeMap<>();
//        for (Faculty f: fcl_list) {
//            ArrayList<Integer> temp_map = new ArrayList<>();
//            temp_map.add(f.getUsers().size());
//            Integer achievements_per_fcl = 0;
//            for (User u: f.getUsers()) {
//                achievements_per_fcl += u.getAchievements().size();
//            }
//            temp_map.add(achievements_per_fcl);
//            temp_map.add(f.getModerations().size());
//            fcls.put(f.getFclTitle(), temp_map);
//        }

        List<Category> cat_list = Ebean.find(Category.class).findList();
        all_cats = cat_list.size();
        cats = new TreeMap<>();
        for (Category c: cat_list) {
            cats.put(c.getCatTitle(), c.getSubCategories().size());
        }
    }
}
