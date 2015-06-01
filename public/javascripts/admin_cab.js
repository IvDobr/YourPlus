ViewModelUsers = function() {
    var self = this;
    self.user_count      = ko.observable("");
    self.user_1          = ko.observable("");
    self.user_2          = ko.observable("");
    self.user_3          = ko.observable("");
    self.user_4          = ko.observable("");
    self.user_5          = ko.observable("");

    self.ach_count       = ko.observable("");
    self.ach_1           = ko.observable("");
    self.ach_2           = ko.observable("");
    self.ach_3           = ko.observable("");
    self.ach_4           = ko.observable("");
    self.ach_5           = ko.observable("");
    self.ach_6           = ko.observable("");
    self.ach_7           = ko.observable("");

    self.fcl_count       = ko.observable("");

    self.long_count      = ko.observable("");

    self.stip_count      = ko.observable("");

    self.meName          = ko.observable("");
    self.meStatus        = ko.observable("");
    self.meLogin         = ko.observable("");
    self.meFaculty       = ko.observable("");
    self.meReg           = ko.observable("");
    self.meStip          = ko.observable("");
    self.meGroup         = ko.observable("");

    self.load = function() {
        jsRoutes.controllers.Admin_API.getGeneralInfoJSON().ajax({
            dataType: 'json',
            contentType: 'charset=utf-8',
            success: function (data) {
                var o = data.info;

                self.user_count(o.user_count);
                self.user_1(o.user_1);
                self.user_2(o.user_2);
                self.user_3(o.user_3);
                self.user_4(o.user_4);
                self.user_5(o.user_5);

                self.ach_count(o.ach_count);
                self.ach_1(o.ach_1);
                self.ach_2(o.ach_2);
                self.ach_3(o.ach_3);
                self.ach_4(o.ach_4);
                self.ach_5(o.ach_5);
                self.ach_6(o.ach_6);
                self.ach_7(o.ach_7);

                self.fcl_count(o.fcl_count);

                self.long_count(o.long_count);

                self.stip_count(o.stip_count);
            },
            error: function (data) {
                alert("error! " + data.error);
                console.log('Не могу отправить json запрос');
                console.log(data);
            }
        });
    };

    self.load();
};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelUsers());
});