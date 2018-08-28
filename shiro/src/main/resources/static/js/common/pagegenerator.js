/**
 * Created by ASUS on 2017/11/25.
 */
define("page",function(){
    //url：需要查询数据的url地址
    //currentPage：当前页
    //totalRows：总记录数
    function pageWithUrl(url,currentPage,totalRows,pageSize) {
        if(url===undefined || url===""){
            url="#";
        }
        if(currentPage === undefined){
            currentPage = 1;
        }
        if(totalRows === undefined){
            totalRows = 0;
        }
        if(pageSize === undefined){
            pageSize = 10;
        }
        //总页数
        var totalPage = 0;
        if(totalRows % pageSize ===0){
            totalPage = parseInt(totalRows / pageSize);
        }else{
            totalPage = parseInt(totalRows / pageSize) +1;
        }
        var pageHtml = "<nav style='margin-top: -30px ; text-align:right; margin-right:15px;'>";
        pageHtml += "<ul class ='pagination'>'";

        //首页和上一页处理
        if(currentPage === 1){
            pageHtml += "<li class='disabled'><a href='#'>首页</a></li> ";
            pageHtml += "<li class='disabled'><a href='#' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a></li> ";
        }else{
            pageHtml += "<li><a href='" +url + "?pageNo=1 ' pageNo=1 >首页</a></li>";
            pageHtml += "<li><a href=' " +url +"?pageNo=" +(currentPage -1)
                         + " ' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a></li> ";
        }

        //总页数小于5页
        if(totalPage <= 5){
            for(var i=1;i<=totalPage;i++){
                if(i===currentPage){
                    pageHtml += "<li class='active'><a href='#'> " + i +"</a></li>";
                }else{
                    pageHtml += "<li><a href=' " + url + "?pageNo=" + i +" ' >" +i +"</a></li>";
                }
            }
        }else{
            if(currentPage > 3){
                pageHtml += "<li><a href='#'>...</a></li>";
                for(var i = (currentPage -2);i <= (currentPage + 2) && i<= totalPage;i++){
                    if(i === currentPage){
                        pageHtml += "<li class='active'><a href='#'>" + i + "</a></li>";
                    }else{
                        pageHtml += "<li><a href=' " + url + "?pageNo=" + i +" ' >" +i +"</a></li>";
                    }
                }
            }else{
                for(var i=1;i<=3 && i<=totalPage ; i++){
                    if(i=== currentPage){
                        pageHtml += "<li class='active'><a href='#'>" + i +"</a></li>";
                    }else{
                        pageHtml += "<li><a href=' " + url + "?pageNo=" + i +" ' >" +i +"</a></li>";
                    }
                }
            }

            if(currentPage + 2 < totalPage){
                pageHtml += "<li><a href='#'>...</a></li>";
            }
        }

        //尾页和下一页处理
        if(totalPage ===0 || currentPage === totalPage){
            pageHtml += "<li class='disabled'><a href='#' aria-label='Next'><span aria-hidden='true' >&raquo;</span></a></li> ";
            pageHtml += "<li class='disabled'><a href='#'>尾页</a></li>"
        }else{
            pageHtml += "<li><a href='" +url + "?pageNo="+(currentPage+1)
                        + " ' aria-label='Next'><span aria-hidden='true'>&raquo;</span> </a></li>";
            pageHtml += "<li><a href='" + url +"?pageNo= "+ totalPage +" ' >尾页</a></li>";
        }

        pageHtml += "</ul>";
        pageHtml += "</nav>";
        return pageHtml;
    }

    function pageWithAjax(reloadDataMethod,requestData,currentPage,totalRows,pageSize) {
         if(url === undefined){
             return "";
         }
         if(currentPage === undefined){
             currentPage =1 ;
         }
        if(totalRows === undefined){
            totalRows = 0;
        }
        if(pageSize === undefined){
            pageSize = 10;
        }
        //总页数
        var totalPage = 0;
        if(totalPage % pageSize ===0){
            totalPage = parseInt(totalRows / pageSize);
        }else{
            totalPage = parseInt(totalRows / pageSize) +1;
        }
        var pageHtml = "<nav style='margin-top: -30px ; text-align:right; margin-right:15px;'>";
        pageHtml += "<ul class ='pagination'>'";

        //首页和上一页处理
        if(currentPage === 1){
            pageHtml += "<li class='disabled'><a href='#'>首页</a></li> ";
            pageHtml += "<li class='disabled'><a href='#' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a></li> ";
        }else{
            pageHtml += "<li><a href='" +url + "?pageNo=1 ' pageNo=1 >首页</a></li>";
            pageHtml += "<li><a href=' " +url +"?pageNo=" +(currentPage -1)+ " ' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a></li> ";
        }

        //总页数小于5页
        if(totalPage <= 5){
            for(var i=1;i<=totalPage;i++){
                if(i===currentPage){
                    pageHtml += "<li class='active'><a href='#'> " + i +"</a></li>";
                }else{
                    pageHtml += "<li><a href=' " + url + "?pageNo=" + i +" ' >" +i +"</a></li>";
                }
            }
        }else {
            if(currentPage > 3){
                pageHtml += "<li><a href='#'>...</a></li>";
                for(var i = (currentPage -2);i <= (currentPage + 2) && i<= totalPage;i++){
                    if(i === currentPage){
                        pageHtml += "<li class='active'><a href='#'>" + i + "</a></li>";
                    }else{
                        pageHtml += "<li><a href=' " + url + "?pageNo=" + i +" ' >" +i +"</a></li>";
                    }
                }
            }else{
                for(var i=1;i<=3 && i<=totalPage ; i++){
                    if(i=== currentPage){
                        pageHtml += "<li class='active'><a href='#'>" + i +"</a></li>";
                    }else{
                        pageHtml += "<li><a href=' " + url + "?pageNo=" + i +" ' >" +i +"</a></li>";
                    }
                }
            }

            if(currentPage + 2 < totalPage){
                pageHtml += "<li><a href='#'>...</a></li>";
            }
        }

                //尾页和下一页处理
                if (totalPage === 0 && currentPage === totalPage) {
                    pageHtml += "<li class='disabled'><a href='#' aria-label='Next'><span aria-hidden='true' >&raquo;</span></a></li> ";
                    pageHtml += "<li class='disabled'><a href='#'>尾页</a></li>"
                } else {
                    pageHtml += "<li><a href='" + url + "?pageNo=" + (currentPage + 1) +
                        "' aria-label='Next'><span aria-hidden='true'>&raquo;</span> </a></li>";
                    pageHtml += "<li><a href='" + url + "?pageNo=" + totalPage + "' >尾页</a></li>";
                }

                pageHtml += "</ul>";
                pageHtml += "</nav>";
                return pageHtml;
            }

          return {
            pageWithUrl : pageWithUrl,
            pageWithAjax : pageWithAjax
        }
});