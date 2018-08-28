require.config({
    baseUrl : '../../js/',
    shim:{
        'bootstrap' :{
            deps:['jquery']
        },
        'ajaxfileupload':{
            deps:['jquery','bootstrap']
        },
        'theme':{
            deps :['jquery']
        },
        'signup':{
            deps:['jquery','bootstrap','theme'],
            exports:'signup'
        },
        "datetimepicker":{
            deps:["jquery","bootstrap"]
        },
        'detetimepicker_zh':{
        	deps:["jquery","bootstrap","datetimepicker"]
        },
        "moment":{
            deps:["jquery","bootstrap"]
        },
        "defaults":{
            deps:["jquery","bootstrap"]
        }
    },
    paths:{
        'jquery' : 'common/jquery-1.9.1.min',
        'bootstrap':'bootstrap/js/bootstrap.min',
        'ajaxfileupload':'common/ajaxfileupload',
        'theme':'bootstrap/js/theme',
        'My97DatePicker':'common/My97DatePicker/WdatePicker',
        'datetimepicker':'common/bootstrap-datetimepicker/bootstrap-datetimepicker.min',
        'detetimepicker_zh':'common/bootstrap-datetimepicker/bootstrap-datetimepicker.zh-CN',
        'moment':'common/bootstrap-datetimepicker/moment-with-locales',
        'signup':'myjs/signup'
    }
});

require(['jquery','bootstrap','signup','ajaxfileupload','theme',"moment",'My97DatePicker','datetimepicker','detetimepicker_zh'],
    function ($, bootstrap, signup,ajaxfileupload,theme,moment,My97DatePicker,datetimepicker,detetimepicker_zh) {
	
	$('.datetimepicker').datetimepicker({
		language:  'zh-CN',  //让星期和月份中文显示
	    step: 10,
	    weekStart: 1,
	    format: 'yyyy-mm-dd hh:ii:ss',
	    autoclose :true,
	    minView:0,
	    todayBtn:true,
	    todayHighlight:true
	});
	
        signup.messsage();

        signup.getLoginUser();

});

define(['signup'],function(signup){
    var signup = {
        messsage:function () {
            console.log("加载了!");
        },

        getLoginUser:function(){
            $.ajax({
                url: '/admin/getLoginUser',
                dataType: 'json',
                data: '',
                contentType: 'application/x-www-form-urlencoded',
                success: function (data) {
                    var userInfo = data.userInfo;
                    $(".msg_span").text(userInfo.realName);
                    var menuList = data.menuListByRoleId;
                    html = "";
                    $.each(menuList, function (index, item) {

                        if(item.childrenMenus.length >0 ){
                            var chilHtml = "";
                            html += '<li class="nav nav-list nav-list-expandable nav-list-expanded">' +
                                '<a><i class="fa fa-user"></i>'+item.menuName+' <span class="caret"></span></a>' +
                                '<ul class="nav navbar-nav" style="width: 100%;">';
                            $.each(item.childrenMenus,function (index1,child) {
                                chilHtml +=
                                    '   <li><a href="../'+child.menuUrl+'"><i class="'+child.classStyle+'"></i> '+child.menuName+'</a></li>' ;
                            })
                            html+= chilHtml +'</ul></li>';
                        }else{
                            if(item.menuId == 'signup'){
                                html += '<li class="active"><a href="../'+item.menuUrl+'"><i class="'+item.classStyle+'"></i> '+item.menuName+'</a></li>';
                            }else{
                                html += '<li class=""><a href="../'+item.menuUrl+'"><i class="'+item.classStyle+'"></i> '+item.menuName+'</a></li>';
                            }
                        }

                    });
                    $("#menu_ul").append(html);
                }
            });
        },

        //下载方法
        fileDownload :function (url) {
            $.ajaxFileUpload({
                url:url,
                data:{timestamp:new Date().getTime()},
                dataType:'json',
                success:function (data) {
                    
                }
            });
        }
    };
    return signup;
});