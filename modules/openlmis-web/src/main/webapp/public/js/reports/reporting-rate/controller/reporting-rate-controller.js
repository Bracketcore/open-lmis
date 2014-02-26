/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ReportingRateController($scope, leafletData) {

  $scope.indicator_types = [{code: 'ever_over_total',name:'Ever Reported / Total Facilities'}
                            ,{code: 'ever_over_expected',name:'Ever Reported / Expected Facilities'}
                            ,{code: 'period_over_expected',name:'Reported during period / Expected Facilities'}];

  $scope.indicator_type = 'period_over_expected';

  $scope.geojson = {};

  function interpolate(value, count) {
    var val = parseFloat(value) / parseFloat(count);
    var interpolator = chroma.interpolate.bezier(['red', 'yellow', 'green']);
    return interpolator(val).hex();
  }

  $scope.style = function (feature) {
    //var val = parseFloat(feature.period) / parseFloat(feature.total);
    var color = ($scope.indicator_type == 'ever_over_total')?interpolate(feature.ever, feature.total):($scope.indicator_type == 'ever_over_expected')? interpolate(feature.ever, feature.expected):interpolate(feature.period, feature.expected);
    return {
      fillColor: color,
      weight: 1,
      opacity: 1,
      color: 'white',
      dashArray: '1',
      fillOpacity: 0.7
    };
  };

  $scope.centerJSON = function () {
    leafletData.getMap().then(function (map) {
      var latlngs = [];
      for (var c = 0; c < $scope.features.length; c++) {
        if ($scope.features[c].geometry === null || angular.isUndefined($scope.features[c].geometry))
          continue;
        if ($scope.features[c].geometry.coordinates === null || angular.isUndefined($scope.features[c].geometry.coordinates))
          continue;
        for (var i = 0; i < $scope.features[c].geometry.coordinates.length; i++) {
          var coord = $scope.features[c].geometry.coordinates[i];
          for (var j in coord) {
            var points = coord[j];
            latlngs.push(L.GeoJSON.coordsToLatLng(points));
          }
        }
      }
      map.fitBounds(latlngs);
    });
  };

  $scope.drawMap = function (json) {

    angular.extend($scope, {
      geojson: {
        data: json,
        style: $scope.style,
        onEachFeature: onEachFeature,
        resetStyleOnMouseout: true
      },
//      layers: {
//        baselayers: {
//          googleTerrain: {
//            name: 'Google Terrain',
//            layerType: 'TERRAIN',
//            type: 'google'
//          },
//          googleHybrid: {
//            name: 'Google Hybrid',
//            layerType: 'HYBRID',
//            type: 'google'
//          },
//          googleRoadmap: {
//            name: 'Google Streets',
//            layerType: 'ROADMAP',
//            type: 'google'
//          }
//        }
//      },
      defaults: {
        scrollWheelZoom: false
      }
    });
    $scope.$apply();
  };

  function onEachFeature(feature, layer) {

    layer.on({
      click: zoomToFeature
    });
    layer.bindPopup(popupFormat(feature));
  }

  function popupFormat(feature){
    return '<b>' + feature.properties.name + '</b><br />' +
        '<div>Expected Facilities: ' + feature.expected + '</div>' +
        '<div>Reported This Period: ' + feature.period + '</div>' +
        '<div>Ever Reported: ' + feature.ever + '</div>' +
        '<div>Total Facilities: ' + feature.total + '</div> ';
  }

  function zoomToFeature(e) {

  }

  $scope.OnFilterChanged = function(){
    $.getJSON('/gis/reporting-rate.json',{program: $scope.program, period: $scope.period }, function (data) {
      $scope.features = data.map;

      angular.forEach($scope.features, function (feature) {
        feature.geometry_text = feature.geometry;
        feature.geometry = JSON.parse(feature.geometry);
        feature.type = "Feature";
        feature.properties = {};
        feature.properties.name = feature.name;
        feature.properties.id = feature.id;
      });

      $scope.drawMap({
        "type": "FeatureCollection",
        "features": $scope.features
      });
      $scope.centerJSON();
    });
  };





}