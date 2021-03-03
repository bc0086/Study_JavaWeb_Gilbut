<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	request.setCharacterEncoding("UTF-8");
%>
<c:set var="contextPath" value="${pageContext.request.contextPath }"></c:set>
<c:set var="article"  value="${articleMap.article}"  />
<c:set var="imageFileList"  value="${articleMap.imageFileList}"  />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글보기</title>
<style>
     #tr_btn_modify{
       display:none;
     }
</style>
<script src = "${contextPath }/resources/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript">
	//제이쿼리를 이용해 이미지 파일 텀부 시 미리보기 기능을 구현
	function readURL(input) {
		if(input.files && input.files[0]) {
			var reader = new FileReader();
			reader.onload = function(e) {
				$('#preview').attr("src", e.target.result);
			}
			reader.readAsDataURL(input.files[0]);
		}
	}
	
    function backToList(obj){
		obj.action="${contextPath}/board/listArticles.do";
	    obj.submit();
	  }
      
    function fn_enable(obj) {
  		document.getElementById("i_title").disabled = false;
  	  	document.getElementById("i_content").disabled = false;
  	  	document.getElementById("i_imageFileName").disabled = false;
  	  	document.getElementById("tr_btn_modify").style.display = "block";
  	  	document.getElementById("tr_btn").style.display = "none";
  	  	
  	  	document.getElementById("tr_file_upload").style.visibility = "visible";
  	  	document.getElementById("tr_file_upload2").style.visibility = "visible";
  	  	document.getElementById("tr_file_upload3").style.visibility = "visible";
    }
      
	function fn_modify_article(obj) {
   		obj.action = "${contextPath}/board/modArticle.do";
    	obj.submit();
    }
	
	// 삭제하기 클릭 시 fn_remove_article() 자바스크립트 함수를 호출하면서 articleNO를 전달
	function fn_remove_article(url, articleNO) {
		// 1. 자바스크립트를 이용해 동적으로 form태그를 생성
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", url);
		
		// 2. 자바스크립트를 이용해 동적으로 input태그를 생성한 후 name과 value를
		//		articleNO와 컨트롤러로 글 번호로 설정함
		var articleNOInput = document.createElement("input");
		articleNOInput.setAttribute("type", "hidden");
		articleNOInput.setAttribute("name", "articleNO");
		articleNOInput.setAttribute("value", articleNO);
		
		// 3. 동적으로 생성된 input태그를 동적으로 생성한 form태그에 append함
		form.appendChild(articleNOInput);
		
		// 4. form태그를 body태그에 추가(append)한 후 서버에 요청함
		document.body.appendChild(form);
		form.submit();
	}
	
	// 답글쓰기 클릭 시 해당함수를 호출하면서 요청명과 글 번호를 전달 받음
	function fn_reply_form(url, parentNO) {
		var form = document.createElement("form");
		form.setAttribute("method","post");
		form.setAttribute("action",url); 
			// 전달된 요청명을 form태그의 action 속성값에 설정
		
		var parentNOInput = document.createElement("input");
		parentNOInput.setAttribute("type", "hidden");
		parentNOInput.setAttribute("name", "parentNO");
		parentNOInput.setAttribute("value", parentNO);
		
		form.appendChild(parentNOInput);
		document.body.appendChild(form);
		form.submit();
	}
</script>
</head>
<body>
<form name="frmArticle" method="post" action="${contextPath}" enctype="multipart/form-data">
	<table border="0" align="center">
		<tr>
			<td width="150" align="center" bgcolor="#FF9933"> 글번호</td>
			<td>
				<input type="text" value="${article.articleNO }" disabled />
				<input type="hidden" name="articleNO" value="${article.articleNO }" />
					<!-- 글 수정 시 글번호를 컨트롤러로 전송하기 위해 미리 hidden태그를 
						이용해 글 번호를 저장 -->
			</td>
		</tr>
		
		<tr>
			<td width="150" align="center" bgcolor="#FF9933"> 작성자 아이디</td>
			<td>
				<input type="text" value="${article.id }" name="writer" disabled />
			</td>
		</tr>
		
		<tr>
			<td width="150" align="center" bgcolor="#FF9933"> 제목</td>
			<td>
				<input type="text" value="${article.title }" name="title" id="i_title" />
			</td>
		</tr>
		
		<tr>
  			<td width="150" align="center" bgcolor="#FF9933"> 내용</td>
   			<td>
    			<textarea rows="20" cols="60" name="content" id="i_content" disabled >
    				${article.content }
    			</textarea>
		   	</td>  
		</tr>
		
		
 <c:if test="${not empty imageFileList && imageFileList!='null' }">
	  <c:forEach var="item" items="${imageFileList}" varStatus="status" >
		    <tr>
			    <td width="150" align="center" bgcolor="#FF9933"  rowspan="2">
			      이미지${status.count }
			   </td>
			   <td>
			     <input  type= "hidden"   name="originalFileName" value="${item.imageFileName }" />
			    <img src="${contextPath}/download.do?articleNO=${article.articleNO}&imageFileName=${item.imageFileName}" id="preview"  /><br>
			   </td>   
			  </tr>  
			  <tr>
			    <td>
			       <input  type="file"  name="imageFileName " id="i_imageFileName"   disabled   onchange="readURL(this);"   />
			    </td>
			 </tr>
		</c:forEach>
 </c:if>
 	
		
		<%-- <c:choose>
			<c:when test="${not empty article.imageFileName && article.imageFileName!='null' }">
			<tr>
				<td width="20%" align="center" bgcolor="#FF9933"  rowspan="2"> 이미지</td>
				<td>
					<!-- 이미지 수정에 대비해 hidden태그에 원래 이미지 파일이름을 저장 -->
					<input type ="hidden" name="originalFileName" value="${article.imageFileName }">
						
					<!-- FileDownloadController서블릿에 이미지 파일이름과 글 번호를 전송해
						이미지를 img태그에 표시함 -->
					<img src="${contextPath }/download.do?articleNO=${article.articleNO}
						&imageFileName=${article.imageFileName}" id="preview" /> <br />
				</td>
			</tr>
			<tr>
				<td>
					<!-- 수정된 이미지 파일 이름을 전송 -->
	       			<input type="file" name="imageFileName" id="i_imageFileName" disabled   
	       				onchange="readURL(this);"   />
				</td>
			</tr>
			</c:when>
			
			<c:otherwise>
				<tr id="tr_file_upload" style="visibility: hidden" >
				    <td width="150" align="center" bgcolor="#FF9933"  rowspan="2">이미지</td>
				    <td>
				    	<input type= "hidden" name="originalFileName" 
				    		value="${article.imageFileName }" />
				    </td>
			    </tr>
			    <tr id="tr_file_upload2" style="visibility: hidden">
			    	<td><img id="preview" /></td>
			    	<td></td>
			    </tr>
			    <tr id="tr_file_upload3" style="visibility: hidden">
				    <td ></td>
				    <td><input type="file" name="imageFileName" id="i_imageFileName"   
				       	disabled onchange="readURL(this);" />
				    </td>
			  	</tr>
			</c:otherwise>
		</c:choose> --%>
		
		<tr>
			<td width="20%" align="center" bgcolor="#FF9933"> 등록일자 </td>
	   		<td>
	    		<input type=text value="<fmt:formatDate value="${article.writeDate}" />" disabled />
	   		</td>  
		</tr>
		
		<tr id="tr_btn_modify"  >
	   		<td colspan="2"   align="center" >
	       		<input type=button value="수정반영하기" onClick="fn_modify_article(frmArticle)"  >
           		<input type=button value="취소" onClick="backToList(frmArticle)">
	   		</td>   
  		</tr>
    	
  		<tr id="tr_btn" >
   			<td colspan=2 align="center">
   			<c:if test="${member.id == article.id }">
	  			<input type=button value="수정하기" onClick="fn_enable(this.form)">
	  			<input type=button value="삭제하기" 
	  				onClick="fn_remove_article('${contextPath}/board/removeArticle.do', 
	  					${article.articleNO})">
	  		</c:if>
	 			<input type=button value="리스트로 돌아가기"  onClick="backToList(this.form)">
	  			<input type=button value="답글쓰기" onClick="fn_reply_form(
	  				'${contextPath}/board/replyForm.do', ${article.articleNO})">
 			</td>
  		</tr>
  		
	</table>
</form>
</body>
</html>