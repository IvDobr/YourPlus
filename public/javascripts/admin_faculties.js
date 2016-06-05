function Faculty (fclId, fclLongTitle, fclTitle, fclAdress, fclStudCount, fclModerCount) {
    var self = this;
    self.fclId                = fclId;
    self.fclLongTitle         = ko.observable(fclLongTitle);
    self.fclTitle             = ko.observable(fclTitle);
    self.fclAdress            = ko.observable(fclAdress);
    self.fclStudCount         = ko.observable(fclStudCount);
    self.fclModerCount        = ko.observable(fclModerCount);
}

function User (userId, userLogin, userName, userFaculty, userStatus, userRole) {
    var self = this;
    self.userId             = userId;
    self.userLogin          = ko.observable(userLogin);
    self.userName           = ko.observable(userName);
    self.userFaculty        = ko.observable(userFaculty);
    self.userStatus         = ko.observable(userStatus);
    self.userRole           = ko.observable(userRole);
}

ViewModelUsers = function() {

    var self = this;

    var currentFclId;
    var userIds = [];

    self.faculties          = ko.observableArray([]);
    self.countFaculties     = ko.observable("");

    self.newFacultyLongTitle         = ko.observable("");
    self.newFacultyTitle             = ko.observable("");
    self.newFacultyAdress            = ko.observable("");

    self.editFacultyId             = ko.observable("");
    self.editFacultyLongTitle      = ko.observable("");
    self.editFacultyTitle          = ko.observable("");
    self.editFacultyAdress         = ko.observable("");

    self.fclId              = ko.observable("");
    self.fclLongTitle       = ko.observable("");
    self.fclTitle           = ko.observable("");
    self.fclAdress          = ko.observable("");
    self.fclStudCount       = ko.observable("");
    self.fclModerCount      = ko.observable("");

    self.search             = ko.observable("");

    self.usersList          = ko.observableArray([]);

    self.userId             = ko.observable("");
    self.userFirstName      = ko.observable("");
    self.userLastName       = ko.observable("");
    self.userStatus         = ko.observable("");
    self.userLogin          = ko.observable("");
    self.userFaculty        = ko.observable("");
    self.userRole           = ko.observable("");

    self.newUsersList       = ko.observableArray([]);
    self.userSearch         = ko.observable("");

    self.newUserId          = ko.observable("");
    self.newUserFirstName   = ko.observable("");
    self.newUserLastName    = ko.observable("");
    self.newUserStatus      = ko.observable("");
    self.newUserLogin       = ko.observable("");
    self.newUserFaculty     = ko.observable("");
    self.newUserRole        = ko.observable("");

    self.loadFaculties = function() {
        jsRoutes.controllers.Admin_API.getAllFacultiesJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            data : {search: self.search},
            success : function(data) {
                self.countFaculties(data.countFaculties);

                self.faculties.removeAll();
                var o = data.faculties;
                for (var i = 0; i < o.length; i++) {
                    self.faculties.push(
                        new Faculty(
                            o[i].fclId,
                            o[i].fclLongTitle,
                            o[i].fclTitle,
                            o[i].fclAdress,
                            o[i].fclStudCount,
                            o[i].fclModerCount
                        )
                    );
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.addFaculty = function(){
        jsRoutes.controllers.Admin_API.addFacultyJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({newFacultyLongTitle:  self.newFacultyLongTitle(),
                                          newFacultyTitle:      self.newFacultyTitle(),
                                          newFacultyAdress:     self.newFacultyAdress()
            }),
            success : function(result){
                console.log(result);
                self.reloadFaculties();
            },
            error : function(result){
                console.log("Error: " + result);
            }
        });
    };

    self.loadOneFaculty = function(fcl) {
        self.editFacultyId(fcl.fclId);
        self.editFacultyLongTitle(fcl.fclLongTitle());
        self.editFacultyTitle(fcl.fclTitle());
        self.editFacultyAdress(fcl.fclAdress());
        $('#editFaculty').modal('show');
    };

    self.openModalModers = function(fcl) {
        currentFclId = fcl.fclId;
        self.loadModers();
        $('#editModers').modal('show');
    };

    self.loadModers = function() {
        jsRoutes.controllers.Admin_API.getModersJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data : JSON.stringify({fclId : currentFclId}),
            success : function(data) {
                self.usersList.removeAll();
                userIds = [];
                var o = data.users;
                for (var i = 0; i < o.length; i++) {
                    self.usersList.push(
                        new User(
                            o[i].userId,
                            o[i].userLogin,
                            o[i].userFirstName + " " + o[i].userLastName,
                            o[i].userFaculty,
                            o[i].userStatus,
                            o[i].userRole
                        )
                    );
                    userIds.push(o[i].userId);
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.loadNewUsers = function() {
        var sch = "";
        if (self.userSearch().length >= 3) sch = self.userSearch;
        jsRoutes.controllers.Admin_API.getUsersForModerationJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            data : {search: sch},
            success : function(data) {
                self.newUsersList.removeAll();
                var o = data.users;
                for (var i = 0; i < o.length; i++) {
                    if (!userIds.includes(o[i].userId))
                    self.newUsersList.push(
                        new User(
                            o[i].userId,
                            o[i].userLogin,
                            o[i].userFirstName + " " + o[i].userLastName,
                            o[i].userFaculty,
                            o[i].userStatus,
                            o[i].userRole
                        )
                    );
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.editFaculty = function(){
        jsRoutes.controllers.Admin_API.editFacultyJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({editFacultyId:        self.editFacultyId(),
                editFacultyLongTitle:  self.editFacultyLongTitle(),
                editFacultyTitle:      self.editFacultyTitle(),
                editFacultyAdress:     self.editFacultyAdress()
            }),
            success : function(){
                self.reloadFaculties();
            },
            error : function(){
                alert("Не удалось изменить факультет");
            }
        });
    };

    self.addModer = function(user){
        jsRoutes.controllers.Admin_API.addModerJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({fclId: currentFclId, userId: user.userId}),
            success : function(){
                    self.loadModers();
            },
            error : function(){
                alert("Не удалось добавить отношение модерации");
            }
        });
        self.loadModers();
    };

    self.removeModer = function(user){
        jsRoutes.controllers.Admin_API.removeModerJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({fclId: currentFclId, userId: user.userId}),
            success : function(){
                self.loadModers();
            },
            error : function(){
                alert("Не удалось удалить отношение модерации");
            }
        });
    };

    self.removeFaculty = function(fcl){
        if (confirm("Вы действительно хотите удалить факультет?"))
        jsRoutes.controllers.Admin_API.removeFacultyJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({fclId : fcl.fclId}),
            success : function(){
                self.faculties.remove(fcl);
            },
            error : function(){
                alert("Не удалось удалить факультет");
            }
        });
    };

    self.reloadFaculties = function() {
        self.newFacultyLongTitle("");
        self.newFacultyTitle("");
        self.newFacultyAdress("");
        self.faculties.removeAll();
        self.loadFaculties();
    };

    self.loadFaculties();

    
    ko.computed(function() {
        self.reloadFaculties();
        return self.search();
    });

    ko.computed(function() {
        self.loadNewUsers();
        return self.userSearch();
    });
};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelUsers());
});
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});
$('#editFaculty').on('shown.bs.modal', function () {
    $('#myInput').focus()
});