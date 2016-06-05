function SubCat (subCatId, subCatAlias, mark, subCatDefinition, parentCat, subCatAchesCount) {
    var self = this;
    self.subCatId               = subCatId;
    self.subCatAlias            = ko.observable(subCatAlias);
    self.mark                   = ko.observable(mark);
    self.subCatDefinition       = ko.observable(subCatDefinition);
    self.parentCat              = ko.observable(parentCat);
    self.subCatAchesCount       = ko.observable(subCatAchesCount);
}

ViewModelUsers = function() {

    var self = this;

    self.subCats          = ko.observableArray([]);
    self.countSubCats     = ko.observable("");

    self.newSubCatAlias              = ko.observable("");
    self.newMark                     = ko.observable("");
    self.newSubCatDefinition         = ko.observable("");
    self.newParentCat                = ko.observable("");

    self.editSubCatId             = ko.observable("");
    self.editSubCatAlias          = ko.observable("");
    self.editMark                 = ko.observable("");
    self.editSubCatDefinition     = ko.observable("");
    self.editParentCat            = ko.observable("");

    self.subCatId             = ko.observable("");
    self.subCatAlias          = ko.observable("");
    self.mark                 = ko.observable("");
    self.subCatDefinition     = ko.observable("");
    self.parentCat            = ko.observable("");
    self.subCatAchesCount        = ko.observable("");

    self.cats          = ko.observableArray([]);
    self.search             = ko.observable("");

    self.loadSubCats = function() {
        jsRoutes.controllers.Admin_API.getAllSubCatsJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            data : {search: self.search},
            success : function(data) {
                self.countSubCats(data.countSubCats);

                var p = data.cats;
                self.cats.removeAll();
                for (var j = 0; j < p.length; j++) {
                    self.cats.push(p[j]);
                }

                self.subCats.removeAll();
                var o = data.subCats;
                for (var i = 0; i < o.length; i++) {
                    self.subCats.push(
                        new SubCat(
                            o[i].subCatId,
                            o[i].subCatAlias,
                            o[i].mark,
                            o[i].subCatDefinition,
                            o[i].parentCat,
                            o[i].subCatAchesCount
                        )
                    );
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.addSubCat = function(){
        jsRoutes.controllers.Admin_API.addSubCatJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({newSubCatAlias:  self.newSubCatAlias(),
                                          mark:  self.newMark(),
                                          newSubCatDefinition:  self.newSubCatDefinition(),
                                          newParentCat:  self.newParentCat()
            }),
            success : function(result){
                console.log(result);
                self.reloadSubCats();
            },
            error : function(result){
                console.log("Error: " + result);
            }
        });
    };

    self.loadOneSubCat = function(subCat) {
        self.editSubCatId            (subCat.subCatId);
        self.editSubCatAlias         (subCat.subCatAlias());
        self.editMark                (subCat.mark());
        self.editSubCatDefinition    (subCat.subCatDefinition());
        self.editParentCat           (subCat.parentCat());
        $('#editSubCat').modal('show');
    };

    self.editSubCat = function(){
        jsRoutes.controllers.Admin_API.editSubCatJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({editSubCatId:           self.editSubCatId(),
                                          editSubCatAlias:        self.editSubCatAlias(), 
                                          editMark:               self.editMark(),
                                          editSubCatDefinition:   self.editSubCatDefinition(),
                                          editParentCat:          self.editParentCat()
            }),
            success : function(){
                    self.reloadSubCats();
            },
            error : function(){
                alert("Не удалось изменить подкатегорию");
            }
        });
    };

    self.removeSubCat = function(subCat){
        if (confirm("Вы действительно хотите удалить подкатегорию?"))
        jsRoutes.controllers.Admin_API.removeSubCatJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({subCatId : subCat.subCatId}),
            success : function(){
                self.subCats.remove(subCat);
            },
            error : function(){
                alert("Не удалось удалить подкатегорию");
            }
        });
    };

    self.reloadSubCats = function() {
        self.newSubCatAlias("");
        self.newMark("");
        self.newSubCatDefinition("");
        self.newParentCat("");
        self.loadSubCats();
    };

    self.loadSubCats();

    
    ko.computed(function() {
        self.reloadSubCats();
        return self.search();
    });
};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelUsers());
});
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});
$('#editSubCat').on('shown.bs.modal', function () {
    $('#myInput').focus()
});