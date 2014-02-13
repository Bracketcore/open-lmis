
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AdminDashboardController($scope) {

    /* Bar Chart */
    var barChartTicks = [[1, "Tab1"], [2, "Tab2"], [3, "Tab3"],[4, "Tab4"],[5, "Tab5"]];

    function GenerateSeries(added){
        var data = [];
        var start = 0 + added;
        var end = 100 + added;

        for(i=1;i<=5;i++){
            var d = Math.floor(Math.random() * (end - start + 1) + start);
            data.push([i, d]);
            start++;
            end++;
        }

        return data;
    }

    $scope.barChartData =  [{ label: "Random Tabs Data Size", data:  GenerateSeries(0), color: "#5482FF" }];

    $scope.barChartOption = {
        series: {
            bars: {show: true}
        },
        bars: {
            align: "center",
            fillColor:  "#5482FF",
            barWidth: 0.3
        },
        xaxis: {
            axisLabel: "Sample tabs",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 10,
            ticks: barChartTicks
        },
        yaxis: {
            axisLabel: "Data Size",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3,
            tickFormatter: function (v, axis) {
                return v + "kb";
            }
        },
        legend: {
            container:$("#barChartLegend"),
            noColumns: 0
        },
        grid:{
            clickable:true,
            hoverable: true,
            borderWidth: 2,
            backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
        },tooltip: true,
        tooltipOpts: {
            content: "%s of %x.0 is %y",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };


    $("#afloat1").bind("plotclick", function (event, pos, item) {
        if(item) {
            var barIndex =  item.datapoint[0];
            if(++barIndex <=4){
                var tabId ='dashboard-tab-'+ barIndex;

                createTab(tabId);
            }
        }
    });

    /* End Bar Chart */

    /* Pie Chart */

    var pieChartSeries = 3;
    var pieChartColors = ["#05BC57","#CC0505", "#FFFF05"];
    var pieChartLabels = ["Reported on time","Did not report","Reported late"];

    $scope.pieChartData = [];
    var series = 3;
    var colors = ["#05BC57","#CC0505", "#FFFF05"];
    var labels = ["Reported on time","Did not report","Reported late"];

    for (var i = 0; i < series; i++) {
        $scope.pieChartData[i] = {
            label: labels[i],
            data: Math.floor(Math.random() * 100) + 1,
            color: colors[i]
        };
    }
    $scope.pieChartOption = {
        series: {
            pie: {
                show: true,
                radius: 1,

                label: {
                    show: true,
                    radius: 2 / 3,
                    formatter: function (label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
                    },
                    threshold: 0.1
                }
            }
        },
        legend: {
            show: true
        },
        grid:{
            hoverable: true
        },
        tooltip: true,
        tooltipOpts: {
            content: "%p.0%, %s",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };

    /* End Pie Chart */

   /* Calendar  */

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    /* event source that contains custom events on the scope */
    $scope.events = [
        {title: 'All Day Event',start: new Date(y, m, 1)},
        {title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d - 3, 16, 0),allDay: false},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d + 4, 16, 0),allDay: false},
        {title: 'Birthday Party',start: new Date(y, m, d + 1, 19, 0),end: new Date(y, m, d + 1, 22, 30),allDay: false},
        {title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
    ];

    /* alert on eventClick */
    $scope.alertOnEventClick = function( event, allDay, jsEvent, view ){
        alert(event.title + 'was clicked');

    };
    /* alert on Drop */
    $scope.alertOnDrop = function(event, dayDelta, minuteDelta, allDay, revertFunc, jsEvent, ui, view){
        alert('Event Droped to make dayDelta ' + dayDelta);

    };
    /* alert on Resize */
    $scope.alertOnResize = function(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view ){
        alert('Event Resized to make dayDelta ' + minuteDelta);
    };

    /* add custom event*/
    $scope.addEvent = function() {
        $scope.events.push({
            title: 'eLMIS V2 kickoff meeting',
            start: new Date(y, m, 28),
            end: new Date(y, m, 29)
        });
    };
    /* remove event */
    $scope.remove = function(index) {
        $scope.events.splice(index,1);
    };
    /* Change View */
    $scope.changeView = function(view,calendar) {
        calendar.fullCalendar('changeView',view);
    };

    /* config object */
    $scope.uiConfig = {
        calendar:{
            height: 450,
            editable: true,
            header:{
                left: 'title',
                center: '',
                right: 'today prev,next'
            },
            eventClick: $scope.alertOnEventClick,
            eventDrop: $scope.alertOnDrop,
            eventResize: $scope.alertOnResize
        }
    };


    /* event sources array*/
    $scope.eventSources = [$scope.events];

    /* End Calendar  */


    /* Easy pie chart */
    var $scaleColor = '#D7D5D5';
    var $lineWidth = 5;
    $scope.option1  = {animate:3000, barColor:'#FEBA50', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent1 = 55;
    $scope.option2  = {animate:2500, barColor:'#E23E3E', scaleColor:$scaleColor, lineWidth: $lineWidth };
    $scope.percent2 = -46;
    $scope.option3  = {animate:2000, barColor:'#38AB49', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent3 = 85;
    $scope.option4  = { animate:1500, barColor:'#FFB848', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent4 = 55;

    /* End of Easy pie chart */

    /* Custom Bar Chart */
    var d1_1 = [[0, 95],[1, 70],[2, 94]];

    var d1_2 = [[0, 80],[1, 60],[2, 30]];

    var d1_3 = [[0, 65],[1, 40],[2, 45]];

    var multiBarsTicks = [[0, "District A"], [1, "District B"], [2, "District C"]];

    $scope.multipleBarsOption = {
        series: {
            shadowSize: 1
        },
        bars: {
            show: true,
            barWidth: 0.2
        },
        xaxis: {
            tickLength: 0, // hide gridlines
            axisLabel: 'District',
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            ticks: multiBarsTicks

        } ,
        yaxis: {
            min:0,
            max:100,
            axisLabel: 'Value',
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3,

            tickFormatter: function (v, axis) {
                return v + "%";
            }
        },
        grid: {
            hoverable: true,
            clickable: false,
            borderWidth: 1
        },
        legend: {
            container:$("#multiBarsLegend"),
            noColumns: 0,
            labelBoxBorderColor: "none"
        },
        tooltip: true,
        tooltipOpts: {
            content: getTooltip,
            shifts: {
                x: 10,
                y: 20
            },
            defaultTheme: false
        }
    };
    $scope.multiBarsData = [
        {
            label: "Reported on time",
            data: d1_1,
            bars: {
                order: 1,
                fillColor:  "#05BC57"

            },
            color: "#05BC57"
        },
        {
            label: "Report late",
            data: d1_2,
            bars: {

                order: 2,
                fillColor:  "#FFFF05"
            },
            color: "#FFFF05"
        },
        {
            label: "Did not report",
            data: d1_3,
            bars: {

                order: 3,
                fillColor:  "#CC0505"
            },
            color: "#CC0505"
        }
    ];
     function getTooltip(label, xval, yval, flotItem){
         return flotItem.series.xaxis.ticks[xval].label+' '+label+' '+xval+' '+yval+' '+flotItem.series.xaxis.ticks[xval].label;
     }
    /* End Custom Bar Chart */

    /* Bootstrap Dynamic Tab Utility  */
    function createTab(tabId){
        var tabNum = tabId.substr(tabId.length - 1);
        var contentId = tabId +'-'+ tabNum;

        if($('#'+tabId).length === 0){ //tab does not exist
            $('.nav-tabs').prepend('<li id="'+tabId+'"><a href="#' + contentId + '" data-toggle="tab"'+'><button class="close closeTab" type="button" >×</button>Tab '+tabNum +'</a></li>');
            showTab(tabId);

            registerCloseEvent();

        }else{
            showTab(tabId);
        }
    }

    function registerCloseEvent() {
        $('#dashboard-tabs').on('click', ' li a .close', function(e) {
            e.preventDefault();
            $(this).parents('li').remove('li');
            $('#dashboard-tabs a:first').tab('show');
        });
    }
    function showTab(tabId) {

        $('#dashboard-tabs #' + tabId + ' a').tab('show');
    }




}