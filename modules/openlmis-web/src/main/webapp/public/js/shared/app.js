'use strict';

/* App Module */
angular.module('openlmis', ['openlmis.services', 'openlmis.localStorage', 'ui.directives'],function ($routeProvider, $locationProvider, $httpProvider) {
  var interceptor = ['$rootScope', '$q', '$window', function (scope, $q, $window) {
    function success(response) {
      angular.element('#loader').hide();
      return response;
    }

    function error(response) {
      angular.element('#loader').hide();
      switch (response.status) {
        case 403:
          $window.location = "/public/pages/access-denied.html";
          break;
        case 401:
          scope.modalShown = true;
          break;
        default:
          break;
      }
      return $q.reject(response);
    }

    return function (promise) {
      return promise.then(success, error);
    };
  }];
  $httpProvider.responseInterceptors.push(interceptor);
}).config(function ($httpProvider) {
    var spinnerFunction = function (data) {
      angular.element('#loader').show();
      return data;
    };
    $httpProvider.defaults.transformRequest.push(spinnerFunction);
    $httpProvider.defaults.headers.common["X-Requested-With"] = "XMLHttpRequest";
  })
  .directive('uiNav',function () {
    return {
      restrict:'A',

      link:function (scope, element, attrs) {
        //Identify all the menu lists
        var lists = $(".navigation ul");

        //Sort the lists based their nesting, innermost to outermost
        lists.sort(function (a, b) {
          return $(b).parents("ul").length - $(a).parents("ul").length;
        });

        setTimeout(function () {

          lists.each(function () {
            var display = false;

            //Check if all the child items are hidden
            $(this).children("li:not(.beak)").each(function () {
              if ($(this).css('display') != 'none') {
                display = true;
                return false;
              }
            });
            //Hide the list and its containing li in case all the children are hidden
            if (!display) {
              $(this).parent().hide();
              $(this).parent().parent().hide();
            }
          });

          $(".navigation li > a").on("click", function () {
            $(this).next(".submenu").show();
          });
        });
      }
    };
  }).directive('openlmisMessage', function (messageService) {
    return {
      restrict:'A',
      link:function (scope, element, attrs) {
        scope.$watch(attrs.openlmisMessage, function () {
          var displayMessage = messageService.get(scope[attrs.openlmisMessage]);
          if (displayMessage)
            element.html(displayMessage);
          else
            element.html(scope[attrs.openlmisMessage]);
        });
      }
    }
  })
  .directive('formToolbar',function () {
    return {
      restrict:'A',
      link:function (scope, element, attrs) {

        function fixToolbarWidth() {
          var toolbarWidth = $(document).width() - 26;
          angular.element("#action_buttons").css("width", toolbarWidth + "px");
        }

        fixToolbarWidth();
        $(window).on('resize', fixToolbarWidth);
      }
    };
  }).directive('placeholder',function () {
    return {
      restrict:'A',
      require:'ngModel',
      link:function (scope, element, attr, ctrl) {
        var value;

        if (!jQuery.support.placeholder) {
          var placeholder = function () {
            ctrl.$modelValue = undefined;
            ctrl.$viewValue = attr.placeholder;
            ctrl.$render();
            element.css("color", "#a2a2a2");
          };
          var unPlaceholder = function () {
            ctrl.$viewValue = undefined;
            element.css("color", "");
            ctrl.$render();
          };

          scope.$watch(attr.ngModel, function (val) {
            if (val == attr.placeholder)   val = '';
            value = val || '';
          });

          element.bind('focus', function () {
            if (value == '') unPlaceholder();
          });

          element.bind('blur', function () {
            if (element.val() == '') {
              placeholder();
            }
          });

          ctrl.$formatters.unshift(function (val) {
            if (!val || (val == attr.placeholder)) {
              placeholder();
              value = '';
              return attr.placeholder;
            }
            return val;
          });
        }
      }
    };
  }).directive('openlmisPagination',function () {
    return {
      restrict:'EA',
      scope:{
        numPages:'=',
        currentPage:'=',
        maxSize:'=',
        onSelectPage:'&',
        nextText:'@',
        previousText:'@',
        checkErrorOnPage:'&'
      },
      templateUrl:'/public/pages/template/pagination/pagination.html',
      replace:true,
      link:function (scope) {
        scope.$watch('numPages + currentPage + maxSize', function () {
          scope.pages = [];
          var maxSize = ( scope.maxSize && scope.maxSize < scope.numPages ) ? scope.maxSize : scope.numPages;
          var startPage = scope.currentPage - Math.floor(maxSize / 2);
          if (startPage < 1) {
            startPage = 1;
          }
          if ((startPage + maxSize - 1) > scope.numPages) {
            startPage = startPage - ((startPage + maxSize - 1) - scope.numPages );
          }
          for (var i = 0; i < maxSize && i < scope.numPages; i++) {
            scope.pages.push(startPage + i);
          }
          if (scope.currentPage > scope.numPages) {
            scope.selectPage(scope.numPages);
          }
        });
        scope.noPrevious = function () {
          return scope.currentPage === 1;
        };
        scope.noNext = function () {
          return scope.currentPage === scope.numPages;
        };
        scope.isActive = function (page) {
          return scope.currentPage === page;
        };

        scope.selectPage = function (page) {
          if (!scope.isActive(page)) {
            scope.currentPage = page;
            scope.onSelectPage({ page:page });
          }
        };

        scope.selectPrevious = function () {
          if (!scope.noPrevious()) {
            scope.selectPage(scope.currentPage - 1);
          }
        };
        scope.selectNext = function () {
          if (!scope.noNext()) {
            scope.selectPage(scope.currentPage + 1);
          }
        };

        scope.hasErrorOnPage = function (page) {
         return scope.checkErrorOnPage({page: page});
        };

      }
    };
  }).run(function ($rootScope) {
    $rootScope.$on('$routeChangeStart', function () {
      angular.element('#ui-datepicker-div').hide();
      angular.element('body > .modal-backdrop').hide();
    });
  });

function isUndefined(value) {
  return (value == null || value == undefined || value.toString().trim().length == 0);
}
jQuery.support.placeholder = !!function () {
  return "placeholder" in document.createElement("input");
}();
