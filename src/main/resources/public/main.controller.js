'use strict';

angular.module('app')

    .controller("MainController", function ($scope, $http, $q) {

        $scope.currentPlayerTurn = 'P1';
        $http.get('initGame').
            success(function (data) {
                $scope.kalahBoard = data;
                $scope.gameMessage = '';
            });


        $scope.move = function (player, pitIndex) {
            $http.get('move', {
                params: {player: player, pitIndex: pitIndex}
            }).success(function (data) {
                $scope.kalahBoard = data;
                $scope.gameMessage = '';
            }).error(function (data) {
                $scope.gameMessage = data.message;
            });
        }
    });