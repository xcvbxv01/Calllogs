<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>通话记录</title>
    <link rel="stylesheet" type="text/css" href="../css/my.css">
    <script type="text/javascript" src="../js/jquery-3.2.0.min.js"></script>
    <script type="text/javascript" >

        //定义函数
        function refreshTable(){
            $("#t1 tbody").empty();
            $.getJSON("/calllog/json/findAll", function (data) {
                $.each(data, function (i, obj) {
                    var str = "<tr><td>" + obj.caller + "</td>";
                    str = str + "<td> " + obj.callerName + "</td>";
                    str = str + "<td> " + obj.callee + "</td>";
                    str = str + "<td> " + obj.calleeName + "</td>";
                    str = str + "<td></td>";
                    str = str + "<td> " + obj.callTime + "</td>";
                    str = str + "<td> " + obj.callDuration + "</td>";
                    str = str + "</tr>";
                    $("#t1 tbody").append(str);
                });
            });
        }

        $(function(){
            setInterval(refreshTable, 2000);
        })

    </script>
</head>
<body>
<input id="btnCleanTable" type="button" value="清除表格"><br>
<table id="t1" border="1px" class="t-1" style="width: 800px">
    <thead>
        <tr>
            <td>本机号码</td>
            <td>本机名字</td>
            <td>对方号码</td>
            <td>对方名字</td>
            <td>是否主叫</td>
            <td>通话时间</td>
            <td>通话时长</td>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${calllogs}" var="u">
            <tr>
                <td><c:out value="${u.caller}"/></td>
                <td><c:out value="${u.callerName}"/></td>
                <td><c:out value="${u.callee}"/></td>
                <td><c:out value="${u.calleeName}"/></td>
                <td>
                    <c:if test="${u.caller != param.caller}">
                        被叫
                    </c:if>
                    <c:if test="${u.caller == param.caller}">
                        主叫
                    </c:if>
                </td>
                <td><c:out value="${u.callTime}"/></td>
                <td><c:out value="${u.callDuration}"/></td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="7" style="text-align: right">
            </td>
        </tr>
    </tbody>
</table>
</body>
</html>