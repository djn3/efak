var topicTable = $("#efak_topic_manage_tbl").DataTable({
    "bSort": false,
    "bLengthChange": false,
    "bProcessing": true,
    "bServerSide": true,
    "fnServerData": retrieveData,
    "sAjaxSource": "/system/audit/table/ajax",
    "aoColumns": [{
        "mData": 'host'
    }, {
        "mData": 'uri'
    }, {
        "mData": 'params'
    }, {
        "mData": 'method'
    }, {
        "mData": 'spent'
    }, {
        "mData": 'code'
    }, {
        "mData": 'modify'
    }, {
        "mData": 'operate'
    }],
    language: {
        "sProcessing": "处理中...",
        "sLengthMenu": "显示 _MENU_ 项结果",
        "sZeroRecords": "没有匹配结果",
        "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
        "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
        "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
        "sInfoPostFix": "",
        "sSearch": "搜索:",
        "sUrl": "",
        "sEmptyTable": "表中数据为空",
        "sLoadingRecords": "载入中...",
        "sInfoThousands": ",",
        "oPaginate": {
            "sFirst": "首页",
            "sPrevious": "上页",
            "sNext": "下页",
            "sLast": "末页"
        },
        "oAria": {
            "sSortAscending": ": 以升序排列此列",
            "sSortDescending": ": 以降序排列此列"
        }
    }
});

function retrieveData(sSource, aoData, fnCallback) {
    $.ajax({
        "type": "get",
        "contentType": "application/json",
        "url": sSource,
        "dataType": "json",
        "data": {
            aoData: JSON.stringify(aoData)
        },
        "success": function (data) {
            fnCallback(data)
        }
    });
}

setInterval(function () {
    topicTable.ajax.reload();
}, 60000); // 1 min


// view topic
$(document).on('click', 'a[name=efak_topic_manage_add_partition]', function (event) {
    event.preventDefault();

    $('#efak_topic_partition_add_modal').modal('show');
});
