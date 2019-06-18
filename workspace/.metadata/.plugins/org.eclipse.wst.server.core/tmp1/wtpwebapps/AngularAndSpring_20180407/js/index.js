var myModule = angular.module("MyModule", []);

myModule.directive("shanny", function() {
	return {
		template : '<div>Hi Shanny</div>'
	}
});

myModule.controller("HelloShanny", [ '$scope', function HelloShanny($scope) {
	$scope.greeting = {
		text : "Hello!!!!!!"
	};
} ]);

myModule.controller("productList", function($scope) {
	$scope.product = [ {
		name : "product 1 "
	}, {
		name : "product 2 "
	} ];
});

myModule.controller('MyController', [ '$scope', function($scope) {
	$scope.username = 'World';

	$scope.sayHello = function() {
		$scope.greeting = 'Hello ' + $scope.username + '!';
	};
} ]);

myModule.controller('GreetController', [ '$scope', '$rootScope',
		function($scope, $rootScope) {
			$rootScope.name = "shanny";
			$rootScope.names = [ 'Igor', 'Misko', 'Vojta' ];
		} ])
myModule.controller('ListController', [ '$scope',
		function($scope) {
			$scope.department = 'AngularJS';
		} ]);
