function Achiev(achId, achTitle, achDate, achCat, achLongCat, achDop, achComment, achPrem, classPrem, achStip, classStip, canDel) {
    var self = this;
    self.achId = achId;
    self.achTitle = ko.observable(achTitle);
    self.achDate = ko.observable(achDate);
    self.achCat = ko.observable(achCat);
    self.achLongCat = ko.observable(achLongCat);
    self.achDop = ko.observable(achDop);
    self.achComment = ko.observable(achComment);
    self.achPrem = ko.observable(achPrem);
    self.classPrem = ko.observable(classPrem);
    self.achStip = ko.observable(achStip);
    self.classStip = ko.observable(classStip);
    self.canDel = ko.observable(canDel);
}

ViewModelAhieves = function() {
    var self = this;
    self.achieves = ko.observableArray([]);

    self.achId = ko.observable(null);
    self.achTitle = ko.observable("");
    self.achDate = ko.observable("");
    self.achDop = ko.observable("");
    self.achPrem = ko.observable("");
    self.achComment = ko.observable("");
    self.classPrem = ko.observable("");
    self.achStip = ko.observable("");
    self.classStip = ko.observable("");
    self.canDel = ko.observable(true);
    self.currStip = ko.observable("?");

    self.meName = ko.observable("");
    self.meStatus = ko.observable("");
    self.meLogin = ko.observable("");
    self.meFaculty = ko.observable("");
    self.meReg = ko.observable("");
    self.meStip = ko.observable("");
    self.userRole = ko.observable("");

    self.sorter = ko.observable("");

    self.pageSize = ko.observable(20);
    self.currentPage = ko.observable(1);
    self.countAches = ko.observable("");
    self.totalPages = ko.observable("");
    self.pages = ko.observableArray([]);
    self.search = ko.observable("");

    self.ct = ko.observable("");

    self.openModal = function(){
        $('#new-ach').modal('show');
    };

    self.getUserInfo = function(){
        jsRoutes.controllers.API.getUserInfoJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            success : function(data) {
                self.meName(data.user.userFirstName + " " + data.user.userLastName);
                self.meLogin(data.user.userLogin);
                self.meFaculty(data.user.userFaculty);
                self.meReg(data.user.userReg);
                self.meStip(data.user.userStip);
                self.userRole(data.user.userRole);
                self.meStatus(data.user.userStatus);
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.checkBender = function() {
        jsRoutes.controllers.API.checkBenderJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            success : function() {
                self.reloadAchievs();
            },
            error : function() {
                alert("Эй, я не могу так быстро работать!");
            }
        });
    };

    self.dataReset = function () {
        self.achId = ko.observable(null);
        self.achTitle = ko.observable("");
        self.achDate = ko.observable("");
        self.achDop = ko.observable("");
        self.achPrem = ko.observable("");
        self.achComment = ko.observable("");
        self.classPrem = ko.observable("");
        self.achStip = ko.observable("");
        self.classStip = ko.observable("");
        self.canDel = ko.observable(true);
        self.currStip = ko.observable("?");
    };

    self.newAchieve = function(){
        var achCat = $("li.AchCat_new.active").attr("id");
        var achSubCat = $("a.AchCat_new.active").attr("id");
        jsRoutes.controllers.API.newAchievJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({achTitle: self.achTitle(), achDate: self.achDate(), achSubCat: achSubCat, achDop: self.achDop()}),
            success : function(data){
                var dStr = self.achDate().split("-");
                self.achieves.push(new Achiev(
                    parseInt(data.id), self.achTitle(), dStr[2]+"."+dStr[1]+"."+dStr[0], achCat, achSubCat, self.achDop(), "", "На рассмотрении", "text-warning", "На рассмотрении", "text-warning", true));
                self.dataReset();
        },
            error : function(){
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.seeAchieve = function(ach){
        self.achId(ach.achId.toString());
        self.achTitle(ach.achTitle());
        var dStr = ach.achDate().split(".");
        self.achDate(dStr[2]+"-"+dStr[1]+"-"+dStr[0]);
        //$('#'+ach.achCat()).addClass("active");
        self.achDop(ach.achDop());
        self.achComment(ach.achComment());
        self.achPrem(ach.achPrem());
        self.classPrem(ach.classPrem());
        self.achStip(ach.achStip());
        self.classStip(ach.classStip());
        self.canDel(ach.canDel());
        $('#see-ach').modal('show');
        if(ach.achCat() == "Успехи в учебе"){
            $('a[href="#cat_learn-see"]').tab('show');
        }else if (ach.achCat() == "Научная деятельность"){
            $('a[href="#cat_science-see"]').tab('show');
        }else if (ach.achCat() == "Спортивная деятельность"){
            $('a[href="#cat_sport-see"]').tab('show');
        }else if (ach.achCat() == "Творческая деятельность"){
            $('a[href="#cat_tvor-see"]').tab('show');
        }else if (ach.achCat() == "Общественная деятельность"){
            $('a[href="#cat_obsh-see"]').tab('show');
        }
        $( "a[id='"+ach.achSubCat()+"']" ).addClass("active");  //todo
    };

    self.editAchieve = function(){
        var achCat = $("li.AchCat_see.active").attr("id");
        var achSubCat = $("a.AchCat_see.active").attr("id");
        jsRoutes.controllers.API.editAchievJSON().ajax({
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({achId: self.achId(), achTitle: self.achTitle(), achDate: self.achDate(), achSubCat: achSubCat, achDop: self.achDop()}),
            success: function () {
                var dStr = self.achDate().split("-");
                for (var i = 0; i < self.achieves().length; i++) {
                    if (self.achieves()[i].achId == self.achId()){
                        self.achieves()[i].achTitle(self.achTitle());
                        self.achieves()[i].achDate(dStr[2]+"."+dStr[1]+"."+dStr[0]);
                        self.achieves()[i].achCat(achCat);
                        self.achieves()[i].achLongCat(achSubCat);
                        self.achieves()[i].achDop(self.achDop());
                    }
                }
                self.dataReset();
            },
            error : function(){
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.setStip = function(stip){
        jsRoutes.controllers.API.setStipJSON().ajax({
            dataType : 'json',
            contentType : 'application/json; charset=utf-8',
            data : JSON.stringify({stip: stip}),
            success : function() {
                self.getStip();
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.getStip = function(){
        jsRoutes.controllers.API.getStipJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            success : function(data) {
                    self.currStip(data.stip);
                    $('#popStip').popover('show');
                    setTimeout(function() { $('#popStip').popover('hide')}, 4500);
            },
            error : function(data) {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.removeAchiev = function(ach) {
        if (confirm("Удалить достижение?")) {
            var dataJSON;
            if (ach == "bt") {
                dataJSON = JSON.stringify({achId: self.achId.toString()});
            } else {
                dataJSON = JSON.stringify({achId: ach.achId});
            }

            jsRoutes.controllers.API.deleteAchievJSON().ajax({
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: dataJSON,
                success: function () {
                    self.achieves.remove(ach);
                },
                error: function () {
                    console.log('Не могу отправить json запрос');
                }
            });
        }
    };

    self.loadAchievs = function(){
        jsRoutes.controllers.API.getAllAchievsJSON().ajax({
            contentType : 'charset=utf-8',
            data : {pageSize: parseInt(self.pageSize()), currentPage: parseInt(self.currentPage()), sortby: self.sorter, search: self.search},
            success : function(data) {
                self.countAches(data.countAches);
                self.totalPages(data.pages);

                var c = parseInt(self.currentPage());

                self.pages.removeAll();
                for (var j = 1; j <= data.pages; j++) self.pages.push(j);

                self.currentPage(c);

                var o = data.aches;

                for (var i = 0; i< o.length; i++){
                    var prem, cPrem, stip, cStip, canDel = true;
                    switch (o[i].achPremStatus) {
                        case 0:
                            prem = "На рассмотрении";
                            cPrem = "text-warning";
                            break;
                        case 1:
                            prem = "Принято";
                            cPrem = "text-success";
                            canDel = false;
                            break;
                        case -1:
                            prem = "Отклонено";
                            cPrem = "text-danger";
                            break;
                    }
                    switch (o[i].achStipStatus) {
                        case 0:
                            stip = "На рассмотрении";
                            cStip = "text-warning";
                            break;
                        case 1:
                            stip = "Принято";
                            cStip = "text-success";
                            canDel = false;
                            break;
                        case -1:
                            stip = "Отклонено";
                            cStip = "text-danger";
                            break;
                    }
                    self.achieves.push(new Achiev(
                        o[i].achId, o[i].achTitle, o[i].achDate, o[i].achCat, o[i].achLongCat, o[i].achDop, o[i].achComment, prem, cPrem, stip, cStip, canDel));
                    console.log("Успешно обработан json запрос. Записи загружены");
                }
            },
            error : function(data) {
                alert("error! "+ data.error);
                console.log('Не могу отправить json запрос');
                console.log(data);
            }
        });
    };

    self.reloadAchievs = function() {
        self.achieves.removeAll();
        self.loadAchievs();
    };

    self.loadAchievs();

    self.generator = function(){
        jsRoutes.controllers.API.generateAches().ajax({
            contentType : 'charset=utf-8',
            data : {count: self.ct},
            success : function() {
                self.reloadAchievs();
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.delAches = function(){
        jsRoutes.controllers.API.delAches().ajax({
            success : function() {
                self.reloadAchievs();
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    ko.computed(function() {
        self.reloadAchievs();
        return self.pageSize() + self.sorter();
    });
};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelAhieves());
});
$( document ).ready(function() {
        $("a.AchCat_new").click( function() {
                $("a.AchCat_new").removeClass("active");
                $(this).addClass("active");
            }
        );
        $("a.AchCat_see").click( function() {
                $("a.AchCat_see").removeClass("active");
                $(this).addClass("active");
            }
        );
    });
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});