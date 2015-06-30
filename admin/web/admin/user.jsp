<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>多啦亲子互动平台</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Charisma, a fully featured, responsive, HTML5, Bootstrap admin template.">
    <meta name="author" content="Muhammad Usman">

    <!-- The styles -->
    <link  href="${ctx}/admin/css/bootstrap-cerulean.css" rel="stylesheet">
    <style type="text/css">
        body {
            padding-bottom: 40px;
        }
        .sidebar-nav {
            padding: 9px 0;
        }
    </style>
    <link href="${ctx}/admin/css/bootstrap-responsive.css" rel="stylesheet">
    <link href="${ctx}/admin/css/charisma-app.css" rel="stylesheet">
    <link href="${ctx}/admin/css/jquery-ui-1.8.21.custom.css" rel="stylesheet">
    <link href='${ctx}/admin/css/fullcalendar.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/fullcalendar.print.css' rel='stylesheet'  media='print'>
    <link href='${ctx}/admin/css/chosen.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/uniform.default.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/colorbox.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/jquery.cleditor.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/jquery.noty.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/noty_theme_default.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/elfinder.min.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/elfinder.theme.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/jquery.iphone.toggle.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/opa-icons.css' rel='stylesheet'>
    <link href='${ctx}/admin/css/uploadify.css' rel='stylesheet'>

    <!-- The HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- The fav icon -->
    <link rel="shortcut icon" href="${ctx}/admin/img/logo200.png">

</head>

<body>
<!-- topbar starts -->
<div class="navbar">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="brand" href="${ctx}/user/index.do?uid=${user.id}"> <img alt="Charisma Logo" src="${ctx}/admin/img/logo200.png" /> <span>哆啦亲子</span></a>

            <!-- user dropdown starts -->
            <div class="btn-group pull-right" >
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                    <i class="icon-user"></i><span class="hidden-phone">  ${user.username}</span>
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <!-- <li><a href="#">Profile</a></li> -->
                    <li class="divider"></li>
                    <li><a href="${ctx}/user/login.do">注销</a></li>
                </ul>
            </div>
            <!-- user dropdown ends -->
        </div>
    </div>
</div>
<!-- topbar ends -->
<div class="container-fluid">
    <div class="row-fluid">

        <div id="content" class="span10">
            <!-- content starts -->
            <div class="box span12">
                <div class="row-fluid sortable">
                    <div class="box-header well" data-original-title>
                        <h2><i class="icon-user"></i> 用户管理</h2>
                        <div class="box-icon">
                            <a href="${ctx}/user/oper.do?uid=${user.id}&id=0&pageNo=${queryPage.pageNo}" class="btn btn-add btn-round"><i class=" icon-plus"></i></a>
                        </div>
                    </div>
                    <div class="box-content">
                        <form >
                            <table class="table table-striped table-bordered bootstrap-datatable datatable">
                                <c:forEach items="${queryPage.list}" var="entity">
                                    <tr>
                                        <td>用户编号:<c:out value="${entity.id}"></c:out><br>
                                            用户名称:<c:out value="${entity.username}"></c:out><br>
                                            用户密码:<c:out value="${entity.password}"></c:out><br>
                                            注册时间:<c:out value="${fn:substring(entity.addTime,0,19)}"></c:out><br>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="center">
                                            <a class="btn btn-info" href="${ctx}/user/oper.do?uid=${user.id}&id=${entity.id}&pageNo=${queryPage.pageNo}">
                                                <i class="icon-edit icon-white"></i>
                                                修改
                                            </a>
                                            <a class="btn btn-danger" href="${ctx}/user/del.do?uid=${user.id}&id=${entity.id}&pageNo=${queryPage.pageNo}">
                                                <i class="icon-trash icon-white"></i>
                                                删除
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                    <tr>
                                        <td class="center">
                                            页码:${queryPage.pageNo}/${queryPage.totalPages}
                                            <a href="${ctx}/user/info.do?uid=${user.id}&pageNo=${queryPage.topPageNo}">
                                                首页
                                            </a>
                                            <a href="${ctx}/user/info.do?uid=${user.id}&pageNo=${queryPage.previousPageNo}">
                                                上一页
                                            </a>
                                            <a href="${ctx}/user/info.do?uid=${user.id}&pageNo=${queryPage.nextPageNo}">
                                                下一页
                                            </a>
                                            <a href="${ctx}/user/info.do?uid=${user.id}&pageNo=${queryPage.bottomPageNo}">
                                                尾页
                                            </a>
                                            总纪录:${queryPage.totalRecords}
                                        </td>
                                    </tr>
                            </table>
                        </form>
                    </div>
                </div>
            </div>
            </div>
            <!-- content ends -->
        </div><!--/#content.span10-->
    </div><!--/fluid-row-->

    <footer>
        <p class="pull-left">&copy; <a href="#" target="_blank">开发时间</a> @2015－06-18 </p>
        <p class="pull-right"><a href="#">多啦亲子互动平台</a></p>
    </footer>

</div><!--/.fluid-container-->

<!-- external javascript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->

<!-- jQuery -->
<script src="${ctx}/admin/js/jquery-1.7.2.min.js"></script>
<!-- jQuery UI -->
<script src="${ctx}/admin/js/jquery-ui-1.8.21.custom.min.js"></script>
<!-- transition / effect library -->
<script src="${ctx}/admin/js/bootstrap-transition.js"></script>
<!-- alert enhancer library -->
<script src="${ctx}/admin/js/bootstrap-alert.js"></script>
<!-- modal / dialog library -->
<script src="${ctx}/admin/js/bootstrap-modal.js"></script>
<!-- custom dropdown library -->
<script src="${ctx}/admin/js/bootstrap-dropdown.js"></script>
<!-- scrolspy library -->
<script src="${ctx}/admin/js/bootstrap-scrollspy.js"></script>
<!-- library for creating tabs -->
<script src="${ctx}/admin/js/bootstrap-tab.js"></script>
<!-- library for advanced tooltip -->
<script src="${ctx}/admin/js/bootstrap-tooltip.js"></script>
<!-- popover effect library -->
<script src="${ctx}/admin/js/bootstrap-popover.js"></script>
<!-- button enhancer library -->
<script src="${ctx}/admin/js/bootstrap-button.js"></script>
<!-- accordion library (optional, not used in demo) -->
<script src="${ctx}/admin/js/bootstrap-collapse.js"></script>
<!-- carousel slideshow library (optional, not used in demo) -->
<script src="${ctx}/admin/js/bootstrap-carousel.js"></script>
<!-- autocomplete library -->
<script src="${ctx}/admin/js/bootstrap-typeahead.js"></script>
<!-- tour library -->
<script src="${ctx}/admin/js/bootstrap-tour.js"></script>
<!-- library for cookie management -->
<script src="${ctx}/admin/js/jquery.cookie.js"></script>
<!-- calander plugin -->
<script src='${ctx}/admin/js/fullcalendar.min.js'></script>
<!-- data table plugin -->
<script src='${ctx}/admin/js/jquery.dataTables.min.js'></script>

<!-- chart libraries start -->
<script src="${ctx}/admin/js/excanvas.js"></script>
<script src="${ctx}/admin/js/jquery.flot.min.js"></script>
<script src="${ctx}/admin/js/jquery.flot.pie.min.js"></script>
<script src="${ctx}/admin/js/jquery.flot.stack.js"></script>
<script src="${ctx}/admin/js/jquery.flot.resize.min.js"></script>
<!-- chart libraries end -->

<!-- select or dropdown enhancer -->
<script src="${ctx}/admin/js/jquery.chosen.min.js"></script>
<!-- checkbox, radio, and file input styler -->
<script src="${ctx}/admin/js/jquery.uniform.min.js"></script>
<!-- plugin for gallery image view -->
<script src="${ctx}/admin/js/jquery.colorbox.min.js"></script>
<!-- rich text editor library -->
<script src="${ctx}/admin/js/jquery.cleditor.min.js"></script>
<!-- notification plugin -->
<script src="${ctx}/admin/js/jquery.noty.js"></script>
<!-- file manager library -->
<script src="${ctx}/admin/js/jquery.elfinder.min.js"></script>
<!-- star rating plugin -->
<script src="${ctx}/admin/js/jquery.raty.min.js"></script>
<!-- for iOS style toggle switch -->
<script src="${ctx}/admin/js/jquery.iphone.toggle.js"></script>
<!-- autogrowing textarea plugin -->
<script src="${ctx}/admin/js/jquery.autogrow-textarea.js"></script>
<!-- multiple file upload plugin -->
<script src="${ctx}/admin/js/jquery.uploadify-3.1.min.js"></script>
<!-- history.js for cross-browser state change on ajax -->
<script src="${ctx}/admin/js/jquery.history.js"></script>
<!-- application script for Charisma demo -->
<script src="${ctx}/admin/js/charisma.js"></script>


</body>
</html>