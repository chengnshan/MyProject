/**
 * 
 */
$.fn.serializeObject = function() {
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [ o[this.name] ];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
};


/**
 * 为了向后兼容XMLHttpRequest ，一个 jqXHR 对象将公开下列属性和方法：
	readyState
	responseXML 和/或 responseText 当底层的请求分别作出 XML 和/或 文本响应
	status
	statusText
	abort( [ statusText ] )
	getAllResponseHeaders() 一个字符串
	getResponseHeader( name )
	overrideMimeType( mimeType )
	setRequestHeader( name, value ) 用新值替换旧值，而不是将新值与旧值连接起来，这偏离了标准
	statusCode( callbacksByStatusCode )
 */
jQuery.extend({
	'comleteAjax':function(){
		//设置全局ajax选项参数
		$.ajaxSetup({
			//设置请求超时时间（毫秒）。值为0表示没有超时。
			timeout : 0,
			cache : false,  //默认: true, dataType为"script"和"jsonp"时默认为false
			beforeSend : function(jqXHR,settings){
				console.log('ajax请求开始.');
//				console.log(settings);
			},
			//这个回调函数得到2个参数： jqXHR (在 jQuery 1.4.x中是 XMLHTTPRequest) 对象
			//和一个描述请求状态的字符串("success", "notmodified", "nocontent"，"error", "timeout", "abort", 或者 
			//"parsererror") 。从jQuery 1.5开始， complete设置可以接受一个函数的数组。每个函数将被依次调用。
			complete:function(xhr,TS){
				console.log('ajax请求结束.');
				console.log(TS);
//				console.log(xhr.responseText);
//				console.log(xhr.status);
				if(xhr.responseText && xhr.responseText.indexOf("<h1>用户登录</h1>") >=0){
					window.location.href="/html/login1.html";
					return ;
				}
			}
		});
		
		/**
		 *  Prefilters是一个预过滤器，在每个请求之前被发送和 $.ajax() 处理它们前处理。
		    options 是请求的选项
			originalOptions 值作为提供给Ajax方法未经修改的选项，因此，没有ajaxSettings设置中的默认值
			jqXHR 是请求的jqXHR对象
			以上内容的核心思想是维护一个队列，发送请求时，将请求加入队列，请求响应后，从队列中清除，这就保证了在任一时刻只能有一个同样的请求发送.
			局限性：仅仅是前台防止jquery的ajax请求。对于非jquery的ajax请求，不起作用。因为使用的是jquery的ajaxPreFilter函数，仅仅对jquery的ajax请求有作用。
			调用abort后jquery会执行error的方法，抛出abort的异常信息。可以使用以下方式区分出该类型的异常。
		 */
		var pendingRequests = {};
	    $.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
	        var key = options.url;
	        console.log(key);
	        if (!pendingRequests[key]) {
	            pendingRequests[key] = jqXHR;
	        }else{
	            //jqXHR.abort();    //放弃后触发的提交
	            pendingRequests[key].abort();   // 放弃先触发的提交
	        }

	        var complete = options.complete;
	        options.complete = function(jqXHR, textStatus) {
	            pendingRequests[key] = null;
	            if ($.isFunction(complete)) {
	            	complete.apply(this, arguments);
	            }
	        };
	    });
	}
});

function generatePendingRequestKey(opts) {
    var url = opts.url;
    var type = opts.type;
    var data = opts.data;
    var str = url + type;
    if (data) {
        str += data;
    }
    return str;
}

