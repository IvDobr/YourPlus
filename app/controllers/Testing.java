package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Testing {

    private List<Faculty> faculties;
    private List<Category> categories;
    private List<SubCategory> subCategories;
    private List<Role> roles;

    public Testing() {
        this.faculties = Ebean.find(Faculty.class).findList();
        this.categories = Ebean.find(Category.class).findList();
        this.subCategories = Ebean.find(SubCategory.class).findList();
        this.roles = Ebean.find(Role.class).findList();
    }

    public Testing(List<Faculty> faculties, List<Category> categories, List<SubCategory> subCategories, List<Role> roles) {
        this.faculties = faculties;
        this.categories = categories;
        this.subCategories = subCategories;
        this.roles = roles;
    }

    public void testDataBase(Boolean flag) {

        Ebean.save(new User("stud", "Иван", "Иванов", "123", faculties.get(1), categories.get(0), true, roles.get(2)));
        Logger.info("Добавлен тестовый студент");

        if (flag) {
            UserRandomize(5000, 6000);
            Logger.info("Пользователи добавлены");

            AchievesRandomize(50000, 59000);
            Logger.info("Достижения добавлены");

            if (checkBender(0)) Logger.info("Пользователи рандомизированы");
        }
    }

    public void UserRandomize(int min, int max) {
        //Рандомизация базы пользователей
        List<String> man_names = lineReader("public/docs/man_names.txt");//609 имен

        List<String> woman_names = lineReader("public/docs/woman_names.txt");//496 имен

        List<String> lastnames = lineReader("public/docs/lastnames.txt");//15353 фамилии

        int userCount = intRandom(min, max);

        List<User> users = new ArrayList<>();

        Logger.warn("ДОБАВЛЕНИЕ В БАЗУ " + userCount + " ПОЛЬЗОВАТЕЛЕЙ");
        String Fname, Lname;

        for (int i = 1; i <= userCount; i++) {
            if (1 == intRandom(0,1)) {
                Fname = man_names.get( intRandom( 0, man_names.size() - 1) );
                Lname = lastnames.get( intRandom( 0, lastnames.size() - 1) );
            } else {
                Fname = woman_names.get( intRandom( 0, woman_names.size() - 1) );
                String str = lastnames.get(intRandom( 0, lastnames.size() - 1));
                if (str.endsWith("в") || str.endsWith("н")) {
                    Lname = str + "a";
                } else if (str.endsWith("ий")) {
                    Lname = str.replace("ий", "ая");
                } else if (str.endsWith("ой")) {
                    Lname = str.replace("ой", "ая");
                } else {
                    Lname = str;
                }
            }

            users.add(new User(
                    "user_" + i,
                    Fname,
                    Lname,
                    "userpass",
                    faculties.get(intRandom( 1, faculties.size() - 1) ),
                    categories.get(intRandom( 0, categories.size() - 1) ),
                    intRandom(1, 100) <= 93,
                    getRoleRandom(5, 20, 1000))
            );
        }

        try {
            Ebean.save(users);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public void AchievesRandomize(int min, int max) {
        Integer achievCount = intRandom(min, max);
        Logger.warn("ДОБАВЛЕНИЕ В БАЗУ " + achievCount + " ДОСТИЖЕНИЙ");

        List<User> users = Ebean.find(User.class).findList();
        List<Achievement> achieves = new ArrayList<>();

        for (int i = 1; i <= achievCount; i++) {
            achieves.add(new Achievement(
                            users.get( intRandom( 0, users.size() - 1) ),
                            "Тестовое достижение номер 000000" + i,
                            dateRandom(1230829200000L),
                            subCategories.get( intRandom( 0, subCategories.size() - 1) ),
                            "(Комментарий: " + i + ") Тестовое достижение номер 000000" + i,
                            "Бла бла бла",
                            intRandom(-1, 1),
                            intRandom(-1, 1))
            );
        }

        try {
            Ebean.save(achieves);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public static void achievesToUserRND(int achievCount, User user) {
        Logger.warn("ДОБАВЛЕНИЕ В БАЗУ " + achievCount + " ДОСТИЖЕНИЙ");

        List<Achievement> achieves = new ArrayList<>();
        List<SubCategory> subCategories = Ebean.find(SubCategory.class).findList();

        for (int i = 1; i <= achievCount; i++) {
            achieves.add(new Achievement(
                            user,
                            "Тестовое достижение номер 000000" + i,
                            dateRandom(1230829200000L),
                            subCategories.get( intRandom( 0, subCategories.size() - 1) ),
                            "(Комментарий: " + i + ") Тестовое достижение номер 000000" + i,
                            user.getLogin() + ": " + user.getUserFirstName() + " " + user.getUserLastName(),
                            intRandom(-1, 1),
                            intRandom(-1, 1))
            );
        }

        try {
            Ebean.save(achieves);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    private Role getRoleRandom(int watchers, int moders, int all) {
        if (roles.size()<4) throw new NullPointerException("Недостаточно ролей");
        if (watchers > moders || moders > all) throw new IllegalArgumentException("Неверные вероятности");
        int prob = intRandom(1, all);
        if (prob <= watchers) return roles.get(4);
        else if (prob <= moders) return roles.get(2);
        else return roles.get(3);
    }

    public static Date dateRandom(long lower_range) {
        java.util.Random r = new java.util.Random();
        return new java.util.Date(lower_range + (long)(r.nextDouble()*(System.currentTimeMillis() - lower_range)));
    }

    public static Boolean checkBender(Integer userId) {
        List<Achievement> achievsList;
        if (0 == userId) {
            achievsList = Ebean.find(Achievement.class).findList();
        } else {
            User user = Ebean.find(User.class, userId);
            achievsList =  Ebean.find(Achievement.class).where().eq("achOwner", user).findList();
        }
        List<String> lines = lineReader("public/docs/aphorism.txt");
        for (Achievement o : achievsList) {
            o.setAchPremStatus(intRandom(-1, 1));
            o.setAchStipStatus(intRandom(-1, 1));
            o.setAchComment( lines.get( intRandom( 0, lines.size() - 1) ) );
            try{
                o.update();
            } catch(Exception e) {
                Logger.error("Бендер поленился");
                Logger.error(e.getMessage());
                return false;
            }
        }
        Logger.info("Сработал Бендер");
        return true;
    }

    public static List<String> lineReader(String filepath) {
        List<String> lines = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(new File(filepath));
            Logger.info("Читаю файл: " + filepath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
        return lines;
    }

    public static Integer intRandom (int min, int max) {
        if (max == min) return min;
        if(min>max) {
            int temp = max;
            max = min;
            min = temp;
        }
        java.util.Random random = new java.util.Random();
        return min + random.nextInt(max - min + 1);
    }
}
