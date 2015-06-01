function Category (catId, catTitle, catSubCategoriesCount, catUsersCount, catAchesCount) {
    var self = this;
    self.catId                      = catId;
    self.catTitle                   = ko.observable(catTitle);
    self.catSubCategoriesCount      = ko.observable(catSubCategoriesCount);
    self.catUsersCount              = ko.observable(catUsersCount);
    self.catAchesCount              = ko.observable(catAchesCount);
}

ViewModelUsers = function() {

    var self = this;

    self.categories          = ko.observableArray([]);
    self.countCategories     = ko.observable("");

    self.newCategoryTitle         = ko.observable("");

    self.editCategoryId          = ko.observable("");
    self.editCategoryTitle       = ko.observable("");

    self.catId                      = ko.observable("");
    self.catTitle                   = ko.observable("");
    self.catSubCategoriesCount      = ko.observable("");
    self.catUsersCount              = ko.observable("");
    self.catAchesCount              = ko.observable("");

    self.search                     = ko.observable("");

    self.loadCategories = function() {
        jsRoutes.controllers.Admin_API.getAllCategoriesJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            data : {search: self.search},
            success : function(data) {
                self.countCategories(data.countCategories);

                self.categories.removeAll();
                var o = data.categories;
                for (var i = 0; i < o.length; i++) {
                    self.categories.push(
                        new Category(
                            o[i].catId,
                            o[i].catTitle,
                            o[i].catSubCategoriesCount,
                            o[i].catUsersCount,
                            o[i].catAchesCount
                        )
                    );
                }
            },
            error : function() {
                console.log('Не могу отправить json запрос');
            }
        });
    };

    self.addCategory = function(){
        jsRoutes.controllers.Admin_API.addCategoryJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({newCategoryTitle:  self.newCategoryTitle()}),
            success : function(result){
                console.log(result);
                self.reloadCategories();
            },
            error : function(result){
                console.log("Error: " + result);
            }
        });
    };

    self.loadOneCategory = function(cat) {
        self.editCategoryId(cat.catId);
        self.editCategoryTitle(cat.catTitle());;
        $('#editCategory').modal('show');
    };

    self.editCategory = function(){
        jsRoutes.controllers.Admin_API.editCategoryJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({editCategoryId:        self.editCategoryId(),
                                          editCategoryTitle:  self.editCategoryTitle()
            }),
            success : function(){
                    self.reloadCategories();
            },
            error : function(){
                alert("Не удалось изменить категорию");
            }
        });
    };

    self.removeCategory = function(cat){
        if (confirm("Вы действительно хотите удалить категорию?"))
        jsRoutes.controllers.Admin_API.removeCategoryJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : JSON.stringify({catId : cat.catId}),
            success : function(){
                self.categories.remove(cat);
            },
            error : function(){
                alert("Не удалось удалить категорию");
            }
        });
    };

    self.reloadCategories = function() {
        self.newCategoryTitle("");
        self.categories.removeAll();
        self.loadCategories();
    };

    self.loadCategories();

    
    ko.computed(function() {
        self.reloadCategories();
        return self.search();
    });
};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelUsers());
});
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});
$('#editCategory').on('shown.bs.modal', function () {
    $('#myInput').focus()
});