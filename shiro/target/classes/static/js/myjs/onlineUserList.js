require.config({
    baseUrl : '../../js/',
    shim:{
        'bootstrap' :{
            deps:['jquery']
        },
        'ajaxfileupload':{
            deps:['jquery','bootstrap']
        },
        "page" : {
            deps :["jquery"]
        },
        'theme':{
            deps :['jquery']
        },
        'onlineUserList':{
            deps:['jquery','bootstrap'],
            exports:'onlineUserList'
        },
        "datetimepicker":{
            deps:["jquery","bootstrap"]
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
        'moment':'common/bootstrap-datetimepicker/moment-with-locales',
        'page' : 'common/pagegenerator',
        'onlineUserList':'myjs/onlineUserList'
    }
});

require(['jquery','bootstrap','onlineUserList','ajaxfileupload','theme',"moment","page",'My97DatePicker','datetimepicker'],
    function ($, bootstrap, onlineUserList,ajaxfileupload,theme,moment,page,My97DatePicker,datetimepicker) {

        onlineUserList.messsage();
        
        onlineUserList.getLoginUser();

        onlineUserList.getOnlineUserList(page);
       
       $("#queryOnlieUser").click(function(){
    	   onlineUserList.getOnlineUserList(page);
       });
       
       $(document).on("click",'#btn_link',function(){
    	   onlineUserList.kickout($(this),page);
       });
       
       onlineUserList.getLocalSessionId();
});

define(['onlineUserList'],function(onlineUserList){
    var onlineUserList = {
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
                                console.log(child.menuName);
                                chilHtml +=
                                    '   <li><a href="../'+child.menuUrl+'"><i class="'+child.classStyle+'"></i> '+child.menuName+'</a></li>' ;
                            })
                            html+= chilHtml +'</ul></li>';
                        }else{
                            if(item.menuId == 'onlineUserList'){
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
        getLocalSessionId: function(){
        	$.ajax({
        		url:'/session/getLocalSessionId',
        		data:'',
        		dataType:'json',
        		type:'post',
        		contentType:'application/x-www-form-urlencoded',
        		success:function(data){
        		//	console.log(data);
        			$("#hid_sessionId").val(data.sessionId);
        		}
        	});
        },
        getOnlineUserList : function(page,pageNo){
            var num=0;
            if(pageNo){
                $("#currentPage").val(pageNo);
                num=parseInt(pageNo-1)*10;
            }
        	$.ajax({
        		url : '/session/findOnlineUserList' ,
        		dataType:'json',
        		data : $("#formDate").serialize(),
        		type:'post',
        		contentType:'application/x-www-form-urlencoded',
        		success:function(data){
        		    var result = data.resultData;
        			var html = "";
        			if(data.resultCode > 0 && result ){
                        var onlineList = result.onlineList;
                        var sessionSize = result.sessionSize;
                        $(".span").text(sessionSize);
                        $.each(onlineList,function(index,item){
                            var userInfo =item.userInfo;
                            html += '<tr class="text-center">'
                                + '<td>'+parseInt(num+1+index)+'</td>'
                                + '<td>'+item.sessionId+'</td>'
                                + '<td>'+item.host+'</td>'
                                + '<td>'+userInfo.userName+'</td>'
                                + '<td>'+userInfo.realName+'</td>'
                                + '<td>' +item.startTimestamp+'</td>'
                                + '<td>'+item.lastAccessTime +'</td>'
                                + '<td >';
                            if($("#hid_sessionId").val() == item.sessionId ){
                                html += '<button type="button" id="btn_link" class="btn btn-link btn-xs" attse="'+item.sessionId+'" disabled="disabled" >删除</button></td></tr>';
                            }else{
                                html += '<button type="button" id="btn_link" class="btn btn-link btn-xs" attse="'+item.sessionId+'">删除</button></td></tr>';
                            }
                        });
                        var totalRows=data.resultCode;console.log(totalRows);
                        totalRows=totalRows ==0 ?1 :totalRows ;
                        var pageHtml=page.pageWithUrl('/session/findOnlineUserList',pageNo,totalRows);

                        $("#page").html(pageHtml);
                        $("#page nav").removeAttr("style");
                        onlineUserList.pageClick(page);
                    }else{
                        html+="<tr name='trDataBody'><td colspan='7' align='center'><span style='color:#ff0000;' >"
                            +"没有查询合适条件的数据</span></td></tr>";
                        $("#page").hide();
                    }

        			$("#onlineBody").html(html);
        		},
        		error:function(){
                    html+="<tr name='trDataBody'><td colspan='7' align='center'><span style='color:#ff0000;' >"
                        +"网络异常</span></td></tr>";
                    $("#onlineBody").html(html);
        		}
        	});
        },
        kickout:function(obj,page){
        	var sessionId =  obj.attr('attse');
        	$.ajax({
        		url : '/session/kickoutSession' ,
        		dataType:'json',
        		type:'post',
        		data:{sessionId:sessionId},
        		contentType:'application/x-www-form-urlencoded',
        		success:function(data){
        			if(data.status == 200){
        				 onlineUserList.getOnlineUserList(page,1);
        			}
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
        },
        pageClick : function(page){
            var element = $("#page nav li:not(.disabled) a");
            $(element).each(function(){
                var url=$(this).attr("href");
                if(url != "#"){
                    var pageNo= url.substr(url.indexOf("pageNo=") + 7 ,url.length);
                    //绑定click事件
                    $(this).click(function () {
                        onlineUserList.getOnlineUserList(page,parseInt(pageNo));
                        return false;
                    });
                }
            });
        }
    };
    return onlineUserList;
});