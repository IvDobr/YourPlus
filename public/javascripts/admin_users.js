function User (userId, userLogin, userFirstName, userLastName, userFaculty, userReg, userStip, userStatus, userRole, achCount) {
    var self = this;
    self.userId             = userId;
    self.userLogin          = ko.observable(userLogin);
    self.userFirstName      = ko.observable(userFirstName);
    self.userLastName       = ko.observable(userLastName);
    //self.userPass           = ko.observable(userPass);
    self.userFaculty        = ko.observable(userFaculty);
    self.userReg            = ko.observable(userReg);
    self.userStip           = ko.observable(userStip);
    self.userStatus         = ko.observable(userStatus);
    self.userRole           = ko.observable(userRole);
    self.achCount           = ko.observable(achCount);
}

ViewModelUsers = function() {

    var self = this;

    self.usersList          = ko.observableArray([]);

    self.newUserLogin       = ko.observable("");
    self.newUserPass        = ko.observable("");
    self.newUserLastName    = ko.observable("");
    self.newUserFirstName   = ko.observable("");
    self.faculties          = ko.observableArray([]);
    self.roles              = ko.observableArray([]);
    self.stips              = ko.observableArray([]);
    self.selectedFaculty    = ko.observable("");
    self.selectedRole       = ko.observable("");
    self.newUserStatus      = ko.observable(true);
    self.newUserOtherGroup  = ko.observable("");

    self.editUserId             = ko.observable("");
    self.editUserPass           = ko.observable("");
    self.editUserFirstName      = ko.observable("");
    self.editUserLastName       = ko.observable("");
    self.editUserStatus         = ko.observable("");
    self.editUserLogin          = ko.observable("");
    self.editUserFaculty        = ko.observable("");
    self.editUserStip           = ko.observable("");
    self.editUserRole           = ko.observable("");

    self.userId             = ko.observable("");
    self.userPass           = ko.observable("");
    self.userName           = ko.observable("");
    self.userFirstName      = ko.observable("");
    self.userLastName       = ko.observable("");
    self.userStatus         = ko.observable("");
    self.userLogin          = ko.observable("");
    self.userFaculty        = ko.observable("");
    self.userReg            = ko.observable("");
    self.userStip           = ko.observable("");
    self.userRole           = ko.observable("");
    self.achCount           = ko.observable("");

    self.pageSize           = ko.observable(20);
    self.currentPage        = ko.observable(1);
    self.countUsers         = ko.observable("");
    self.totalPages         = ko.observable("");
    self.pages              = ko.observableArray([]);
    self.search             = ko.observable("");
    self.sorter             = ko.observable("");

    self.newUsers           = ko.observable("");
    self.newUsersList       = ko.observable("");
    self.newUsersListBOOL   = ko.observable(false);

    self.ct = ko.observable("");

    self.loadUsers = function() {
        jsRoutes.controllers.Admin_API.getAllUsersJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            data : {pageSize: parseInt(self.pageSize()), currentPage: parseInt(self.currentPage()), sortby: self.sorter, search: self.search},
            success : function(data) {
                self.countUsers(data.countUsers);
                self.totalPages(data.pages);

                var c = parseInt(self.currentPage());

                self.pages.removeAll();
                for (var j = 1; j <= data.pages; j++) self.pages.push(j);

                self.currentPage(c);

                self.usersList.removeAll();
                var o = data.users;
                for (var i = 0; i < o.length; i++) {
                    self.usersList.push(
                        new User(
                            o[i].userId,
                            o[i].userLogin,
                            o[i].userFirstName,
                            o[i].userLastName,
                            //o[i].userPass,
                            o[i].userFaculty,
                            o[i].userReg,
                            o[i].userStip,
                            o[i].userStatus,
                            o[i].userRole,
                            o[i].achCount
                        )
                    );
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.addUser = function(){
        jsRoutes.controllers.Admin_API.addUserJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({newUserLogin: self.newUserLogin(),
                                          newUserFirstName: self.newUserFirstName(),
                                          newUserLastName: self.newUserLastName(),
                                          newUserPass: self.newUserPass(),
                                          selectedFaculty: self.selectedFaculty(),
                                          selectedRole: self.selectedRole(),
                                          newUserStatus: self.newUserStatus()
                                        }),
            success : function(result){
                console.log(result);
                self.reloadUsers();
            },
            error : function(result){
                console.log("Error: " + result);
            }
        });
    };

    self.addUserMany = function(){
        jsRoutes.controllers.Admin_API.addUserManyJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({newUsers: self.newUsers(),
                selectedFaculty: self.selectedFaculty(),
                selectedRole: self.selectedRole(),
                newUserStatus: self.newUserStatus()
            }),
            success : function(result){
                self.reloadUsers();
                self.newUsersList(result.newUsersList);
                self.newUsersListBOOL(true);
            },
            error : function(result){
                console.log("Error: " + result);
            }
        });
    };

    self.loadOneUser = function(user) {
        self.editUserId(user.userId);
        //self.editUserPass(user.userPass());
        self.editUserFirstName(user.userFirstName());
        self.editUserLastName(user.userLastName());
        self.editUserStatus(user.userStatus()=="Активен");
        self.editUserLogin(user.userLogin());
        self.editUserFaculty(user.userFaculty());
        self.editUserStip(user.userStip());
        self.editUserRole(user.userRole());
        $('#editUser').modal('show');
    };

    self.editUser = function(){
        jsRoutes.controllers.Admin_API.editUserJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({userId:               self.editUserId(),
                                          editUserPass:         self.editUserPass(),
                                          editUserFirstName:    self.editUserFirstName(),
                                          editUserLastName:     self.editUserLastName(),
                                          editUserStatus:       self.editUserStatus(),
                                          editUserLogin:        self.editUserLogin(),
                                          editUserFaculty:      self.editUserFaculty(),
                                          editUserStip:         self.editUserStip(),
                                          editUserRole:         self.editUserRole()
                                        }),
                success : function(){
                self.reloadUsers();
            },
            error : function(){
                alert("Не удалось изменить пользователя");
            }
        });
    };

    self.removeUser = function(user){
        //if (confirm("Вы действительно хотите удалить пользователя?"))
        jsRoutes.controllers.Admin_API.removeUserJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({userId : user.userId}),
            success : function(){
                self.usersList.remove(user);
            },
            error : function(){
                alert("Не удалось удалить пользователя");
            }
        });
    };

    self.reloadUsers = function() {
        self.newUserLogin("");
        self.newUserPass("");
        self.newUserLastName("");
        self.newUserFirstName("");
        self.newUsersList("");
        self.usersList.removeAll();
        self.loadUsers();
    };

    self.getUtil = function(){
        self.faculties.removeAll();
        self.roles.removeAll();
        jsRoutes.controllers.Admin_API.getUtilTitlesJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            success : function(data) {
                var o = data.faculties;
                var p = data.roles;
                var r = data.stips;
                for (var i = 0; i < o.length; i++) {
                    self.faculties.push(o[i])
                }
                for (i = 0; i < p.length; i++) {
                    self.roles.push(p[i])
                }
                for (i = 0; i < r.length; i++) {
                    self.stips.push(r[i])
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.getUtil();
    self.loadUsers();

    self.generator = function(){
        jsRoutes.controllers.Admin_API.generateUsers().ajax({
            contentType : 'charset=utf-8',
            data : {count: self.ct},
            success : function() {
                self.reloadUsers();
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };
    
    ko.computed(function() {
        self.reloadUsers();
        return self.pageSize() + self.sorter();
    });
};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelUsers());
});
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});
$('#editUser').on('shown.bs.modal', function () {
    $('#myInput').focus()
});