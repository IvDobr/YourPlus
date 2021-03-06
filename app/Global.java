import com.avaje.ebean.Ebean;
import controllers.Testing;
import models.*;
import play.Application;
import play.GlobalSettings;
import play.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        Logger.info("Приложение запущено");

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8")); //Задаем иркутский часовой пояс

        if (Ebean.find(Administrator.class).findList().isEmpty()) {

            Ebean.save(new Administrator("admin", "123123"));
            Logger.info("Добавлен администратор");


            List<Category> categories = Arrays.asList(
                    new Category("Не выбрано"),
                    new Category("Научная деятельность"),
                    new Category("Спортивная деятельность"),
                    new Category("Творческая деятельность"),
                    new Category("Общественная деятельность"),
                    new Category("Успехи в учебе")
            );
            
            List<SubCategory> subCategories = Arrays.asList(
                    new SubCategory("SubCat_1_1", categories.get(1), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии награды (приза) за результаты научно-исследовательской работы, проводимой учреждением высшего профессионального образования или иной организацией."),
                    new SubCategory("SubCat_1_2", categories.get(1), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии документа, удостоверяющего исключительное право студента на достигнутый им научный (научно-методический, научно-технический, научно-творческий) результат интеллектуальной деятельности (патент, свидетельство)."),
                    new SubCategory("SubCat_1_3", categories.get(1), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии гранта на выполнение научно-исследовательской работы."),
                    new SubCategory("SubCat_1_4", categories.get(1), "Наличие у студента публикации в научном (учебно-научном, учебно-методическом) международном издании, индексируемом в базе данных 'Сеть науки' (Web of Science), в течение года, предшествующего назначению повышенной стипендии."),
                    new SubCategory("SubCat_1_5", categories.get(1), "Наличие у студента публикации в научном (учебно-научном, учебно-методическом) международном издании учреждения высшего профессионального образования или иной организации в течение года, предшествующего назначению повышенной стипендии."),
                    new SubCategory("SubCat_1_6", categories.get(1), "Наличие у студента публикации в научном (учебно-научном, учебно-методическом) всероссийском издании учреждения высшего профессионального образования или иной организации в течение года, предшествующего назначению повышенной стипендии."),
                    new SubCategory("SubCat_1_7", categories.get(1), "Наличие у студента публикации в научном (учебно-научном, учебно-методическом) ведомственном или региональном издании, в издании учреждения высшего профессионального образования или иной организации в течение года, предшествующего назначению повышенной стипендии."),
                    new SubCategory("SubCat_1_8", categories.get(1), "Публичное представление студентом в течение года, предшествующего назначению повышенной стипендии, результатов научно-исследовательской работы, в том числе путем выступления с докладом (сообщением) на конференции, семинаре и ином международном мероприятии, проводимом учреждением высшего профессионального образования, общественной или иной организацией."),
                    new SubCategory("SubCat_1_9", categories.get(1), "Публичное представление студентом в течение года, предшествующего назначению повышенной стипендии, результатов научно-исследовательской работы, в том числе путем выступления с докладом (сообщением) на конференции, семинаре и ином всероссийском мероприятии, проводимом учреждением высшего профессионального образования, общественной или иной организацией."),
                    new SubCategory("SubCat_1_10",categories.get(1), "Публичное представление студентом в течение года, предшествующего назначению повышенной стипендии, результатов научно-исследовательской работы, в том числе путем выступления с докладом (сообщением) на конференции, семинаре и ином ведомственном или региональном мероприятии, проводимом учреждением высшего профессионального образования, общественной или иной организацией."),
                    new SubCategory("SubCat_2_1", categories.get(2), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии, награды (приза) за результаты спортивной деятельности, осуществленной им в рамках спортивных международных, мероприятий, проводимых учреждением высшего профессионального образования или иной организацией."),
                    new SubCategory("SubCat_2_2", categories.get(2), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии, награды (приза) за результаты спортивной деятельности, осуществленной им в рамках спортивных всероссийских мероприятий, проводимых учреждением высшего профессионального образования или иной организацией."),
                    new SubCategory("SubCat_2_3", categories.get(2), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии, награды (приза) за результаты спортивной деятельности, осуществленной им в рамках спортивных ведомственных или региональных мероприятий, проводимых учреждением высшего профессионального образования или иной организацией."),
                    new SubCategory("SubCat_2_4", categories.get(2), "Участие студента в спортивных мероприятиях воспитательного, пропагандистского характера и (или) иных общественно значимых спортивных мероприятиях; баллы выставляются за каждое мероприятие."),
                    new SubCategory("SubCat_3_1", categories.get(3), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии, награды (приза) за результаты культурно-творческой деятельности, осуществленной им в рамках деятельности, проводимой учреждением высшего профессионального образования или иной организацией, в том числе в рамках конкурса, смотра и иного аналогичного международного мероприятия."),
                    new SubCategory("SubCat_3_2", categories.get(3), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии, награды (приза) за результаты культурно-творческой деятельности, осуществленной им в рамках деятельности, проводимой учреждением высшего профессионального образования или иной организацией, в том числе в рамках конкурса, смотра и иного аналогичного всероссийского мероприятия."),
                    new SubCategory("SubCat_3_3", categories.get(3), "Получение студентом в течение 2 лет, предшествующих назначению повышенной стипендии, награды (приза) за результаты культурно-творческой деятельности, осуществленной им в рамках деятельности, проводимой учреждением высшего профессионального образования или иной организацией, в том числе в рамках конкурса, смотра и иного аналогичного ведомственного и регионального мероприятия."),
                    new SubCategory("SubCat_3_4", categories.get(3), "Публичное представление студентом в течение года, предшествующего назначению повышенной стипендии, созданного им произведения литературы или искусства (литературного произведения, драматического, музыкально-драматического произведения, сценарного произведения, хореографического произведения, пантомимы, музыкального произведения с текстом или без текста, аудиовизуального произведения, произведения живописи, скульптуры, графики, дизайна, графического рассказа, комикса, другого произведения изобразительного искусства, произведения декоративно-прикладного, сценографического искусства, произведения архитектуры, градостроительства, садово-паркового искусства, в том числе в виде проекта, чертежа, изображения, макета, фотографического произведения, произведения, полученного способом, аналогичным фотографии, географической, геологической, другой карты, плана, эскиза, пластического произведения, относящегося к географии, топографии и другим наукам, а также другого произведения)."),
                    new SubCategory("SubCat_3_5", categories.get(3), "Участие студента в проведении (обеспечении проведения) публичной культурно-творческой деятельности воспитательного, пропагандистского характера и иной общественно значимой публичной культурно-творческой деятельности; баллы выставляются за каждое мероприятие."),
                    new SubCategory("SubCat_4_1", categories.get(4), "Участие в деятельности общественного объединения."),
                    new SubCategory("SubCat_4_2", categories.get(4), "Участие студента в проведении (обеспечении проведения) социально ориентированной, культурной (культурно-просветительской, культурно-воспитательной) деятельности в форме шефской помощи, благотворительных акций и иных подобных формах; баллы выставляются за каждое мероприятие."),
                    new SubCategory("SubCat_4_3", categories.get(4), "Участие студента в проведении (обеспечении проведения) общественной деятельности, направленной на пропаганду общечеловеческих ценностей, уважения к правам и свободам человека, а также на защиту природы; баллы выставляются за каждое мероприятие."),
                    new SubCategory("SubCat_4_4", categories.get(4), "Участие студента в проведении (обеспечении проведения) общественно значимых культурно-массовых мероприятий; баллы выставляются за каждое мероприятие."),
                    new SubCategory("SubCat_4_5", categories.get(4), "Участие студента в деятельности по информационному обеспечению общественно значимых мероприятий, общественной жизни учреждения высшего профессионального образования (в разработке сайта учреждения высшего профессионального образования, организации и обеспечении деятельности средств массовой информации, в том числе в издании газеты, журнала, создании и реализации теле- и радиопрограмм учреждения высшего профессионального образования); баллы выставляются за каждый информационный материал."),
                    new SubCategory("SubCat_4_6", categories.get(4), "Участие студента в обеспечении защиты прав студентов; баллы выставляются за каждое мероприятие."),
                    new SubCategory("SubCat_4_7", categories.get(4), "Безвозмездное выполнение студентом общественно полезной деятельности, в том числе организационной, направленной на поддержание общественной безопасности, благоустройство окружающей среды, природоохранной деятельности или иной аналогичной деятельности; баллы выставляются за каждое мероприятие."),
                    new SubCategory("SubCat_5_1", categories.get(5), "Получение студентом по итогам промежуточной аттестации в течение не менее 2 следующих друг за другом семестров, предшествующих назначению стипендии, оценок «отлично» и «хорошо» при наличии не менее 50 процентов оценок «отлично»."),
                    new SubCategory("SubCat_5_2", categories.get(5), "Признание студента победителем или призером проводимых учреждением высшего профессионального образования, общественной и иной организацией международной олимпиады, конкурса, соревнования, состязания и иного мероприятия, направленных на выявление учебных достижений студентов, проведенных в течение 2 лет, предшествующих назначению стипендии."),
                    new SubCategory("SubCat_5_3", categories.get(5), "Признание студента победителем или призером проводимых учреждением высшего профессионального образования, общественной и иной организацией всероссийской олимпиады, конкурса, соревнования, состязания и иного мероприятия, направленных на выявление учебных достижений студентов, проведенных в течение 2 лет, предшествующих назначению стипендии."),
                    new SubCategory("SubCat_5_4", categories.get(5), "Признание студента победителем или призером проводимых учреждением высшего профессионального образования, общественной и иной организацией ведомственной или региональной олимпиады, конкурса, соревнования, состязания и иного мероприятия, направленных на выявление учебных достижений студентов, проведенных в течение 2 лет, предшествующих назначению стипендии.")
            );

            List<Faculty> faculties = Arrays.asList(
                    new Faculty("Без факультета",                                   "-",        "-"),
                    new Faculty("Институт математики, экономики и информатики",     "ИМЭИ",     "г. Иркутск, Бульвар Гагарина, 20"),
                    new Faculty("Физический факультет",                             "Физфак",   "г. Иркутск, Бульвар Гагарина, 20"),
                    new Faculty("Байкальская международная бизнес-школа ИГУ",       "БМБШ",     "г. Иркутск, Карла Маркса, 1"),
                    new Faculty("Институт социальных наук",                         "ИСН",      "г. Иркутск, Ленина, 3"),
                    new Faculty("Международный институт экономики и лингвистики",   "МИЭЛ",     "г. Иркутск, Улан-Баторская, 6"),
                    new Faculty("Педагогический институт",                          "ПИ",       "г. Иркутск, Нижняя Набережная, 6"),
                    new Faculty("Юридический институт",                             "ЮИ",       "г. Иркутск, Улан-Баторская, 10"),
                    new Faculty("Биолого-почвенный факультет",                      "Биофак",   "г. Иркутск, Сухэ-Батора, 5"),
                    new Faculty("Географический факультет",                         "Геогрфак", "г. Иркутск, Лермонтова, 126"),
                    new Faculty("Геологический факультет",                          "Геофак",   "г. Иркутск, Ленина, 3"),
                    new Faculty("Исторический факультет",                           "Истфак",   "г. Иркутск, Чкалова, 2"),
                    new Faculty("Сибирско-американский факультет",                  "САФ",      "г. Иркутск, Улан-Баторская, 6"),
                    new Faculty("Факультет психологии",                             "Психфак",  "г. Иркутск, Чкалова, 2"),
                    new Faculty("Факультет сервиса и рекламы",                      "ФСИР",     "г. Иркутск, Лермонтова, 126"),
                    new Faculty("Факультет филологии и журналистики",               "Филфак",   "г. Иркутск, Чкалова, 2"),
                    new Faculty("Химический факультет",                             "Химфак",   "г. Иркутск, Лермонтова, 126")
            );

            List<Role> roles = Arrays.asList(
                    new Role("Студент"),
                    new Role("Модератор"),
                    new Role("Наблюдатель")
            );

            Ebean.save(categories);
            Logger.info("Добавлены основные категории");

            Ebean.save(subCategories);
            Logger.info("Добавлены подкатегории");

            Ebean.save(faculties);
            Logger.info("Добавлены факультеты");

            Ebean.save(roles);
            Logger.info("Добавлены роли");


            new Testing(faculties, categories, subCategories, roles).testDataBase(false); //Тестирование


        } else {Logger.info("Администратор уже есть, первичное заполнение исполнено");}
    }

    public void onStop(Application app) {
        Logger.info("Приложение остановлено...");
    }

}