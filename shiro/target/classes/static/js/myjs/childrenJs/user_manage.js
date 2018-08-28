require.config({
    baseUrl : '../../../js/',
    shim:{
        'bootstrap' :{
            deps:['jquery']
        },
        'ajaxfileupload':{
            deps:['jquery','bootstrap']
        },
        'bootstrap_fileinput':{
        	deps:['jquery','bootstrap']
        },
        'fileinput_locale_zh':{
        	deps:['jquery','bootstrap','bootstrap_fileinput']
        },
        "page" : {
            deps :["jquery"]
        },
        'theme':{
            deps :['jquery']
        },
        'confirmTool':{
        	deps:['jquery','bootstrap']
        },
        'selectpicker':{
            deps:['jquery','bootstrap']
        },
        'i18n':{
            deps:['jquery','bootstrap']
        },
        'user_manage':{
            deps:['jquery','bootstrap','theme','bootstrap_fileinput'],
            exports:'user_manage'
        },
        "datetimepicker":{
            deps:["jquery","bootstrap"]
        },
        "moment":{
            deps:["jquery","bootstrap"]
        },
        'CustCommonJs':{
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
        'bootstrap_fileinput':'bootstrap/bootstrap-fileinput/js/fileinput.min',
        'fileinput_locale_zh':'bootstrap/bootstrap-fileinput/js/fileinput_locale_zh',
        'theme':'bootstrap/js/theme',
        'confirmTool':'common/confirm/confirmTool',
        'My97DatePicker':'common/My97DatePicker/WdatePicker',
        'datetimepicker':'common/bootstrap-datetimepicker/bootstrap-datetimepicker.min',
        'moment':'common/bootstrap-datetimepicker/moment-with-locales',
        'selectpicker':'common/bootstrap-select-1.12.4/js/bootstrap-select.min',
        'i18n':'common/bootstrap-select-1.12.4/js/i18n/defaults-zh_CN',
        'page' : 'common/pagegenerator',
        'CustCommonJs':'common/CustCommonJs',
        'user_manage':'myjs/childrenJs/user_manage'
    }
});

require(['jquery','bootstrap','user_manage','ajaxfileupload','bootstrap_fileinput','fileinput_locale_zh','theme','confirmTool',"moment","page",
	'selectpicker','i18n','My97DatePicker','datetimepicker','CustCommonJs'],
    function ($, bootstrap, user_manage,ajaxfileupload,bootstrap_fileinput,fileinput_locale_zh,theme,confirmTool,moment,page,selectpicker
    		,i18n,My97DatePicker,datetimepicker,CustCommonJs) {

		$.comleteAjax();
	
        user_manage.messsage();

        user_manage.getLoginUser();

        user_manage.getRoleList();

        user_manage.getUserList(page,1);

        //查询按钮
        $(document).on('click','#queryOnlieUser',function () {
            user_manage.getUserList(page,1);
        });
        //添加
        $(document).on('click','#saveUserInfo',function () {
        	user_manage.addUserInfo(page);
        });
        //删除
        $(document).on('click','#delUserInfo',function () {      	
        	 //取表格的选中行数据
 //       	var arrselections = $("#tb_departments").bootstrapTable('getSelections');
        	var delUserName = $(this).attr('value');
        	if (delUserName.length <= 0) {
        		 toastr.warning('请选择有效数据');
        		 return;
        	}
        	
        	var realName = $(this).parent().parent().children("td:eq(2)").html();
        	Ewin.confirm({ message: "确认要删除<span style='color:red;font-size:16px;'>"+realName+"</span>这个用户吗?" }).on(function (e) {
	        	if (!e) {
	        		return;
	        	}
	        	
	        	user_manage.deleteUser(delUserName,page);
        	});
        });
        //修改
        $(document).on('click','#updateUserInfo',function () { 
        	
        	user_manage.getUserInfoByUserName($(this).val());
        });
        
        $(document).on('click','#updateSuccBtn',function () { 
        	Ewin.confirm({ message: "确认要修改选择的数据吗?" }).on(function (e) {
	        	if (!e) {
	        		return;
	        	}
	        	
	        	user_manage.updateSaveUserInfo(page);
        	});
        });
        

        $('.selectpicker').selectpicker({'noneSelectedText':'请选择'});
        $('.selectpicker').selectpicker('refresh');

        $("#addUser").click(function(){
        	user_manage.clearAddText('add');
            $("#addUserModal").modal("toggle");
        });
        
        /**
         * 清空文本框
         */
        $(document).on('click','#clearQueryText',function(){
        	user_manage.clearAddText();
        });
        
        /**
         * 下载用户列表
         */
        $(document).on('click','#exportUser',function(){
        	user_manage.fileDownload('/admin/exportUserInfo');
        });
        
        /**
         * 上传用户列表
         */
        $(document).on('click','#importUser',function(){
        	$("#uploadUser").modal("toggle");
        });
        
      //初始化fileinput控件（第一次初始化）
        user_manage.initFileInput('file-Portrait', "/admin/importUserInfo",page);
});

define(['user_manage'],function(user_manage){
    var user_manage = {
        messsage:function () {
            console.log("加载了!");
        },

        getLoginUser:function(){
            $.ajax({
                url: '/admin/getLoginUser',
                dataType: 'json',
                data: {timestamp:new Date()},
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
                                '<ul class="nav navbar-nav" style="width: 100%;display: block;">';

                            $.each(item.childrenMenus,function (index1,child) {
                                console.log(child.menuName);
                                if(child.menuId == 'user_manage'){
                                    chilHtml +=
                                        '   <li class="active"><a href="../../'+child.menuUrl+'"><i class="'+child.classStyle+'"></i> '+child.menuName+'</a></li>' ;
                                }else{
                                    chilHtml +=
                                        '   <li><a href="../../'+child.menuUrl+'"><i class="'+child.classStyle+'"></i> '+child.menuName+'</a></li>' ;
                                }

                            })

                            html+= chilHtml +'</ul></li>';
                        }else{
                                html += '<li class=""><a href="../../'+item.menuUrl+'"><i class="'+item.classStyle+'"></i> '+item.menuName+'</a></li>';
                        }
                    });
                    $("#menu_ul").append(html);
                }
            });
        },
        getRoleList:function(){
            $.ajax({
                url:'/role/findRoleList',
                dataType:'json',
                data:{timestamp:new Date()},
                type:'post',
                contentType:'application/x-www-form-urlencoded',
                success:function (data) {
                    var html = '';
                    if(data){
                        $.each(data,function (index, item) {
                            html += '<option value="'+item.roleId+'">'+item.roleName+'</option>';
                        });
                    }
                    
                    $('#roleSelect').append("<option value=''>请选择</option>"+html);
                    $('#addRoleId').append(html);
                    $('.selectpicker').selectpicker({
                        size : 10
                    });
                    $('.selectpicker').selectpicker('refresh');
                }
            });
        },
        getUserList:function(page,pageNo){
            var num=0;
            if(pageNo){
                $("#currentPage").val(pageNo);
                num=parseInt(pageNo-1)*10;
            }
            var params = $("#userForm").serialize()+'&'+$.param({timestamp:new Date().getTime()});
            $.ajax({
                url:'/admin/queryUserListByCondition',
                dataType:'json',
                type:'POST',
                contentType:'application/x-www-form-urlencoded',
                data:params,
                success:function (data) {
                    var result = data.resultData;
                    var html = "";
                    if(data.resultCode > 0 && result){
                        $.each(result,function (index, item) {

                            var roleNames = "";
                            html+='<tr><td>'+parseInt(num+1+index)+'</td><td>'+item.userName+'</td><td>'+item.realName+'</td>';
                            var roleList = item.roleList;
                            if(roleList != undefined && roleList){
                                $.each(roleList,function (index1, itemRole) {
                                    roleNames += itemRole.roleName+',';
                                });
                            }
                            if(roleNames != null && roleNames.length > 0){
                                roleNames = roleNames.substring(0,roleNames.length-1);
                            }
                            html+='<td>'+roleNames+'</td><td>'+item.create_time+'</td>' +'<td>'+item.update_time+'</td>'+
                            	'<td><button class="btn btn-link" id="updateUserInfo" value="'+item.userName+'">修改</button>'+
                                '<button class="btn btn-link" id="delUserInfo" value="'+item.userName+'">删除</button></td>' + '</tr>';
                        });
                        var totalRows=data.resultCode;console.log(totalRows);
                        totalRows=totalRows ==0 ?1 :totalRows ;
                        var pageHtml=page.pageWithUrl('/queryUserListByCondition',pageNo,totalRows);

                        $("#page").html(pageHtml);
                        $("#page nav").removeAttr("style");
                        user_manage.pageClick(page);
                    }else{
                        html+="<tr name='trDataBody'><td colspan='7' align='center'><span style='color:#ff0000;' >"
                            +"没有查询合适条件的数据</span></td></tr>";
                        $("#page").hide();
                    }
                    if(html ==""){
                        html+="<tr name='trDataBody'><td colspan='7' align='center'><span style='color:#ff0000;' >"
                            +"没有查询合适条件的数据</span></td></tr>";
                        $("#page").hide();
                    }

                    $('#userInfoBody').html(html);
                },
                error:function (data) {
                    html+="<tr name='trDataBody'><td colspan='7' align='center'><span style='color:#ff0000;' >"
                        +"网络异常</span></td></tr>";
                    $("#userInfoBody").html(html);
                }
            });
        },
        //添加用户
        addUserInfo : function (page) {
            var addUserName = $('#addUserName').val();
            var addRealName = $('#addRealName').val();
            var addRoleId = $('#addRoleId').val();
            var addUserPassword = $('#addUserPassword').val();
            if( addUserName == null || addUserName == ''){
            	$(".warning_p.addUser").text("用户名必须填写");
            	$('.alert-warning.addUser').slideDown(500).delay(2000).slideUp(1000);
                return false;
            }
            if( addRealName == null || addRealName == ''){
            	$(".warning_p.addUser").text("真实姓名必须填写");
            	$('.alert-warning.addUser').slideDown(500).delay(2000).slideUp(1000);
                return false;
            }
            if( addRoleId == null || addRoleId == ''){
            	$(".warning_p.addUser").text("角色必须选择");
        		$('.alert-warning.addUser').slideDown(500).delay(2000).slideUp(1000);
                return false;
            }
            if( addUserPassword == null || addUserPassword == ''){
            	$(".warning_p.addUser").text("密码必须填写");
        		$('.alert-warning.addUser').slideDown(500);
                return false;
            }
            var params = $('#addUserForm').serialize()+'&'+$.param({'timestamp':new Date().getTime()});
            $.ajax({
                url:'/admin/addUserInfoAndRole',
                dataType:'json',
                type:'POST',
                contentType:'application/x-www-form-urlencoded',
                data:params,
                success:function (data) {
                	var result = data.resultData;
                	if(data.resultCode > 0 && result){
                		console.log(result);
                		$("#addUserModal").modal("toggle");
                		user_manage.getUserList(page,1);
                	}else{
                		$(".warning_p").text(data.resultMessage);
                		$('.alert-warning').slideDown(500).delay(2000).slideUp(1000);
                	} 
                },
                error:function(){
                	
                }
            });
        },

        //删除用户
        deleteUser:function (userName,page) {
           var params = $.param({userName:userName});
            $.ajax({
                    url:'/admin/deleteUserInfoAndRole',
                    dataType:'json',
                    type:'POST',
                    contentType:'application/x-www-form-urlencoded',
                    data:params,
                    success:function (data) {
                    	var result = data.resultData;
                    	if(data.resultCode > 0 && result){
                    		console.log(result);
                    		user_manage.getUserList(page,1);
                    	}
                    }
             });
        },
        
        updateUserInfo:function(userInfo){
        	$("body").find("#updateModal").remove();
        	var roleHtml = "";
        	var selectVal = new Array();
        	var roleIds = "";
        	if(userInfo.userInfo.roleList){
        		$.each(userInfo.userInfo.roleList,function(index,item){
        			roleIds+= item.roleId+",";
        		});
        //		console.log(roleIds);
        	}
        	if(userInfo.roleList){
        		$.each(userInfo.roleList,function(index,item){
        			if(roleIds.indexOf(item.roleId) >= 0){
        				roleHtml+='<option value='+item.roleId+'>'+item.roleName+'</option>';
        				selectVal[index] = item.roleId;
        			}else{
        				roleHtml+='<option value='+item.roleId+'>'+item.roleName+'</option>';
        			}
            	});
        	}
        	
 //       	var mdlId = new Date().valueOf();
        	var modelHtml= 
        	'<div class="modal fade" id="'+'updateModal'+'" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">'+
        	' <div class="modal-dialog" style="width: 60%;">'+
        	'	<div class="modal-content">'+
        	'		<div class="modal-header">'+
        	'			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'+
        	'			<h4 class="modal-title" id="myModalLabel" style="font-family: \'Arial Black\';font-weight: bold;font-size: 20px;">修改用户</h4>'+
        	'		</div>'+
        	'		<div class="modal-body" style="width: 100%;">'+
        	'			<div class="alert alert-success update" role = "alert" style="display: none">'+
        	'				<button class="close"  data-dismiss="alert" type="button" >&times;</button>'+
        	'				<p class="success_p update"></p>'+
        	'			</div>'+
        	'			<div class="alert alert-warning update" role = "alert" style="display: none">'+
        	'				<button class="close"  data-dismiss="alert" type="button" >&times;</button>'+
        	'				<p class="warning_p update"></p>'+
        	'			</div>'+
        	'			<form class="form-horizontal" id="updateUserForm">'+
        	'				<input type="hidden" name="updateId" value ="'+userInfo.userInfo.id+'" />'+
        	'				<div class="row" style="border:0px red solid;text-center:center;">'+
        	'					<div class="col-xs-6 form-group">'+
        	'						<label class="col-xs-3 control-label">用户名:</label>'+
        	'						<div class="col-xs-9">'+
        	'							<div class="col-xs-10">'+
        	'								<input type="text" class="form-control" id="updateUserName" name="updateUserName" placeholder="请输入用户名" value='+userInfo.userInfo.userName+'>'+
        	'							</div>'+
        	'						</div>'+
        	'					</div>'+
        	'					<div class="col-xs-6 form-group">'+
        	'						<label class="col-xs-3 control-label">真实姓名:</label>'+
        	'						<div class="col-xs-9">'+
        	'							<div class="col-xs-10">'+
        	'								<input type="text" class="form-control" id="updateRealName" name="updateRealName" placeholder="请输入真实姓名" value='+userInfo.userInfo.realName+'>'+
        	'							</div>'+
        	'						</div>'+
        	'					</div>'+
        	'				</div>'+
        	'				<div class="row" style="border:0px red solid;text-center:center;">'+
        	'					<div class="col-xs-6 form-group">'+
        	'						<label class="col-xs-3 control-label">角 &nbsp;&nbsp;色:</label>'+
        	'						<div class="col-xs-9">'+
        	'							<div class="col-xs-10">'+
        	'								<select class="selectpicker form-control updateSelectpicker" multiple data-live-search="true" data-style="btn-default" id="updateRoleId" name="updateRoleId">'+
        								roleHtml+
        	'								</select>'+
        	'							</div>'+
        	'						</div>'+
        	'					</div>'+
        	'					<div class="col-xs-6 form-group">'+
        	'						<label class="col-xs-3 control-label">用户密码:</label>'+
        	'						<div class="col-xs-9">'+
        	'							<div class="col-xs-10">'+
        	'								<input type="text" class="form-control" id="updateUserPassword" name="updateUserPassword" readonly value='+userInfo.userInfo.password+'>'+
        	'							</div>'+
        	'						</div>'+
        	'					</div>'+
        	'				</div>'+
        	'			</form>'+
        	'		</div>'+
        	'		<div class="modal-footer">'+
        	'			<div style="margin-right:50px;">'+
        	'		 		<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>'+
        	'				<span class="" style="width: 20px;margin-left: 15px;"></span>'+
        	'		 		<button type="button" class="btn btn-primary" id="updateSuccBtn">修改</button>'+
        	'			</div>'+
        	'		</div>'+
        	'	</div>'+
        	' </div>'+
        	'</div>';
        	
        	$("body").append(modelHtml);
        	if(selectVal && selectVal.length>0){
        	//	console.log(selectVal);
        		$('.updateSelectpicker').selectpicker('val',selectVal);
        	}
        	
            $('.updateSelectpicker').selectpicker('refresh');
        	$("#updateModal").modal();
        },
        
        getUserInfoByUserName:function(userName){
        	$.ajax({
                url:'/admin/getUserInfoByUserName',
                dataType:'json',
                type:'POST',
                contentType:'application/x-www-form-urlencoded',
                data:{userName:userName},
                success:function (data) {
                	var result = data.resultData;
                	if(data.resultCode > 0 && result){
                		user_manage.updateUserInfo(data.resultData);
                	}
                }
        	});
        },
        
        updateSaveUserInfo:function(page){
 //       	var updateFormData = $("#updateUserForm").serialize();
        	var updateId = $('[name=updateId]').val();
        	var updateUserName = $('#updateUserName').val();
        	var updateRealName = $('#updateRealName').val();
        	var updateRoleId = $('#updateRoleId').val();
        	if( updateUserName == null || updateUserName == ''){
            	$(".warning_p.update").text("用户名必须填写");
            	$('.alert-warning.update').slideDown(500).delay(2000).slideUp(1000);
                return false;
            }
            if( updateRealName == null || updateRealName == ''){
            	$(".warning_p.update").text("真实姓名必须填写");
            	$('.alert-warning.update').slideDown(500).delay(2000).slideUp(1000);
                return false;
            }
       /*     if( updateRoleId == null || updateRoleId == ''){
            	$(".warning_p.update").text("角色必须选择");
        		$('.alert-warning.update').slideDown(500).delay(2000).slideUp(1000);
                return false;
            }*/
            
        	var params = {id:updateId,userName:updateUserName,realName:updateRealName,roleIds:updateRoleId};
        	var data = JSON.stringify(params);
        	console.log(data);
        	$.ajax({
        		url:'/admin/updateUserInfoAndRole?timestamp='+new Date().getTime(),
        		dataType:'json',
        		type:'POST',
        		contentType:'application/json',
        		data:data,
        		success:function(data){
        			var result = data.resultData;
        			if(data.resultCode > 0 && result){
        				$("#updateModal").modal("toggle");
        				user_manage.getUserList(page,1);
        			}else{
        				$(".warning_p.update").text(data.resultMessage);
                		$('.alert-warning.update').slideDown(500).delay(2000).slideUp(1000);
        			}
        		},
        		error:function(data){
        			
        		}
        	});
        },

        clearAddText : function(addText){
        	if(addText == 'add'){
        		 $('#addUserName').val('');
            	 $('#addRealName').val('');
                 $('#addRoleId').val('');
                 $('#addUserPassword').val('');
        	}else{
        		$('#userName').val('');
           	 	$('#realName').val('');
                $('#roleSelect').selectpicker('val','');
        	}
        },
        
        //下载方法
        fileDownload :function (url) {
        	var userName = $('#userName').val();
            var realName = $('#realName').val();
            var roleId = $('#roleSelect').val();
            
            var params = $.param({userName:userName,realName:realName,'roleIds':roleId,timestamp:new Date().getTime()});
            var jsonStr = JSON.stringify({userName:userName,realName:realName,'roleIds':roleId,timestamp:new Date().getTime()});

            $.ajaxFileUpload({
                url:url,
                data:{userName:userName,realName:realName,'roleIds':roleId,timestamp:new Date().getTime()},
                dataType:'text',
                success:function (responseText) {
                	var data = $.parseJSON(responseText);
        			if(data.resultCode > 0 ){
        				$(".success.queryUser").text(data.resultMessage);
                		$('.alert-success.queryUser').slideDown(500).delay(2000).slideUp(1000);
        			}else{
        				$(".span.warning.queryUser").text(data.resultMessage);
                		$('.alert-warning.queryUser').slideDown(500).delay(2000).slideUp(1000);
        			}
                },
                error:function(e,xhr,opt){
                	$(".warning.queryUser").text(e.responseText);
            		$('.alert-warning.queryUser').slideDown(500).delay(2000).slideUp(1000);
                }
            });
        },
        
        uploadFile:function(url){
        	
        },
        
       //初始化fileinput控件（第一次初始化）
        initFileInput:function(ctrlName, uploadUrl,page) {    
            var control = $('#' + ctrlName); 

            //初始化上传控件的样式
            control.fileinput({
                language: 'zh', //设置语言
                uploadUrl: uploadUrl, //上传的地址
                uploadExtraData:{id : 1,userName:"李青"},
                allowedFileExtensions: ['xls'],//接收的文件后缀
                showUpload: true, //是否显示上传按钮
                showCancel:true,	//是否显示取消按钮
                showCaption: false,//是否显示标题
                browseClass: "btn btn-primary", //按钮样式     
                //dropZoneEnabled: false,//是否显示拖拽区域
                //minImageWidth: 50, //图片的最小宽度
                //minImageHeight: 50,//图片的最小高度
                //maxImageWidth: 1000,//图片的最大宽度
                //maxImageHeight: 1000,//图片的最大高度
                //maxFileSize: 0,//单位为kb，如果为0表示不限制文件大小
                //minFileCount: 0,
                maxFileCount: 1, //表示允许同时上传的最大文件个数
                maxFileSize : 100000,//单位为kb，如果为0表示不限制文件大小
                maxFilesNum : 10,//
                layoutTemplates :{
                    // actionDelete:'', //去除上传预览的缩略图中的删除图标
                    actionUpload:'',//去除上传预览缩略图中的上传图片；
                    actionZoom:''   //去除上传预览缩略图中的查看详情预览的缩略图标。
                },
                enctype: 'multipart/form-data',
                validateInitialCount:true,
                previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
                msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
            }).on("fileuploaded", function (event, data, previewId, index) {
            	//导入文件上传完成之后的事件
            	var dataText = data.response;
                var result= dataText.resultData;
            	 if(dataText.resultCode >=0 && result) {
               //      fileName.push(data.response.fileName);
                     $("#uploadUser").modal("toggle");
                     user_manage.getUserList(page,1);
                 }else{
                     $(".uploadUser_p").text(dataText.resultMessage);
             		 $('.uploadUser_div').slideDown(500).delay(2000).slideUp(1000);
                 }
            	 control.fileinput("clear");
            	 control.fileinput("reset");
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
                        user_manage.getUserList(page,parseInt(pageNo));
                        return false;
                    });
                }
            });
        }
    };
    return user_manage;
});