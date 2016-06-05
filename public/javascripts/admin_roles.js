function Role (roleId, roleTitle, roleUsersCount) {
    var self = this;
    self.roleId                      = roleId;
    self.roleTitle                   = ko.observable(roleTitle);
    self.roleUsersCount              = ko.observable(roleUsersCount);
}

ViewModelRoles = function() {

    var self = this;

    self.roles          = ko.observableArray([]);
    self.countRoles     = ko.observable("");

    self.newRoleTitle         = ko.observable("");

    self.editRoleId          = ko.observable("");
    self.editRoleTitle       = ko.observable("");

    self.roleId                      = ko.observable("");
    self.roleTitle                   = ko.observable("");
    self.roleUsersCount              = ko.observable("");

    self.search                     = ko.observable("");

    self.loadRoles = function() {
        jsRoutes.controllers.Admin_API.getAllRolesJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            data : {search: self.search},
            success : function(data) {
                self.countRoles(data.countRoles);

                self.roles.removeAll();
                var o = data.roles;
                for (var i = 0; i < o.length; i++) {
                    self.roles.push(
                        new Role(
                            o[i].roleId,
                            o[i].roleTitle,
                            o[i].usersCount
                        )
                    );
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.addRole = function(){
        if ("" == self.newRoleTitle()) ;
        jsRoutes.controllers.Admin_API.addRoleJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({newRoleTitle:  self.newRoleTitle()}),
            success : function(result){
                console.log(result);
                self.reloadRoles();
            },
            error : function(result){
                console.log("Error: " + result);
            }
        });
    };

    self.loadOneRole = function(role) {
        self.editRoleId(role.roleId);
        self.editRoleTitle(role.roleTitle());
        $('#editRole').modal('show');
    };

    self.editRole = function(){
        jsRoutes.controllers.Admin_API.editRoleJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({editRoleId:        self.editRoleId(),
                                          editRoleTitle:  self.editRoleTitle()
            }),
            success : function(){
                    self.reloadRoles();
            },
            error : function(){
                alert("Не удалось изменить роль");
            }
        });
    };

    self.removeRole = function(role){
        if (confirm("Вы действительно хотите удалить роль?"))
        jsRoutes.controllers.Admin_API.removeRoleJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({roleId : role.roleId}),
            success : function(){
                self.roles.remove(role);
            },
            error : function(){
                alert("Не удалось удалить роль");
            }
        });
    };

    self.reloadRoles = function() {
        self.newRoleTitle("");
        self.roles.removeAll();
        self.loadRoles();
    };

    self.loadRoles();

    
    ko.computed(function() {
        self.reloadRoles();
        return self.search();
    });
};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelRoles());
});
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});
$('#editRole').on('shown.bs.modal', function () {
    $('#myInput').focus()
});