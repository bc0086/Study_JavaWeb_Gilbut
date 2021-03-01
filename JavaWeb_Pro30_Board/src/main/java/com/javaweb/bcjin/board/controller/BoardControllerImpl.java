package com.javaweb.bcjin.board.controller;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.javaweb.bcjin.board.service.BoardService;
import com.javaweb.bcjin.board.vo.ArticleVO;
import com.javaweb.bcjin.member.vo.MemberVO;

@Controller("boardController")
public class BoardControllerImpl implements BoardController {
	private static final String ARTICLE_IMAGE_REPO = "c:\\03Workspace\\file_repo";

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ArticleVO articleVO;

	// 전체 글 목록
	@Override
	@RequestMapping(value="/board/listArticles.do", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		List articlesList = boardService.listArticles();
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("articlesList", articlesList);
		return mav;
	}
	
	// 글 쓰기
	@Override
	@RequestMapping(value="/board/addNewArticle.do", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> articleMap = new HashMap<String, Object>();
		
		// 글쓰기 창에서 전송된 글 정보를 Map에 key/value로 저장함
		Enumeration enu = multipartRequest.getParameterNames();
		while(enu.hasMoreElements() ) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
		}
		
		String imageFileName = upload(multipartRequest); // 업로드한 이미지 파일이름을 가져옴
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getId(); // 세션에 저장된 회원정보로부터 회원ID를 가져옴
		
		// 회원ID, 이미지 파일 이름, 부모 글 번호를 articleMap에 저장함
		articleMap.put("parentNO", 0);
		articleMap.put("id",id);
		articleMap.put("imageFileName", imageFileName);
		
		String message;
		ResponseEntity resEnt=null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		try {
			// 글 정보가 저장된 articleMap을 Service 클래스의 addArticle()메서드로 전달함
			int articleNO = boardService.addNewArticle(articleMap);
			
			// 글 정보를 추가한 후 업로드 한 이미지 파일을 글 번호로 명명한 폴더로 이동함
			if(imageFileName != null && imageFileName.length() != 0) {
				File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
				File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}
			
			message="<script>";
			message += "alert('새글을 추가하였습니다.');";
			message += "location.href='" + multipartRequest.getContextPath()+"/board/listArticles.do';";
			message +=" </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
				// 새 글을 추가한 후 메세지를 전달함.
		} catch (Exception e) {
			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();
			
			message = " <script>";
			message +=" alert('오류가 발생했습니다. 다시 시도해 주세요');');";
			message +=" location.href='"+multipartRequest.getContextPath()+"/board/articleForm.do'; ";
			message +=" </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
				// 오류 발생 시 오류메세지를 전달함.
			e.printStackTrace();
		}
		return resEnt;
	}
	
	// 글쓰기 창을 나타냄
	@RequestMapping(value="/board/*Form.do", method=RequestMethod.GET)
	private ModelAndView form(HttpServletResponse response, HttpServletRequest request) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}

	// 업로드 한 파일이름을 얻은 후 반환함
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception{
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		while(fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			imageFileName = mFile.getOriginalFilename();
			File file = new File(ARTICLE_IMAGE_REPO + "\\" + fileName);
			if(mFile.getSize() != 0) {
				if(! file.exists()) {
					if(file.getParentFile().mkdirs()) {
						file.createNewFile();
					}
				}
				mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName));
			}
		}
		return imageFileName;
	}

	@Override
	@RequestMapping(value="/board/viewArticle.do", method=RequestMethod.GET)
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, 
									HttpServletRequest request, 
									HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		articleVO = boardService.viewArticle(articleNO);
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("article", articleVO);
		return mav;
	}

	//한 개 이미지 수정 기능
  @RequestMapping(value="/board/modArticle.do" ,method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity modArticle(MultipartHttpServletRequest multipartRequest,  
		  							HttpServletResponse response) throws Exception{
	  multipartRequest.setCharacterEncoding("utf-8");
	  Map<String,Object> articleMap = new HashMap<String, Object>();
	  Enumeration enu=multipartRequest.getParameterNames();
	  while(enu.hasMoreElements()){
		  String name=(String)enu.nextElement();
		  String value=multipartRequest.getParameter(name);
		  articleMap.put(name,value);
	  }

	  String imageFileName= upload(multipartRequest);
	  articleMap.put("imageFileName", imageFileName);

	  String articleNO=(String)articleMap.get("articleNO");
	String message;
	ResponseEntity resEnt=null;
	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.add("Content-Type", "text/html; charset=utf-8");
	try {
	   boardService.modArticle(articleMap);
	   if(imageFileName!=null && imageFileName.length()!=0) {
	     File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
	     File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
	     FileUtils.moveFileToDirectory(srcFile, destDir, true);
	     
	     String originalFileName = (String)articleMap.get("originalFileName");
	     File oldFile = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO+"\\"+originalFileName);
	     oldFile.delete();
	   }	
	   message = "<script>";
	   message += " alert('글을 수정했습니다.');";
	   message += " location.href='"+multipartRequest.getContextPath()+"/board/viewArticle.do?articleNO="+articleNO+"';";
	   message +=" </script>";
	   resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
	} catch(Exception e) {
	  File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
	  srcFile.delete();
	  message = "<script>";
	  message += " alert('오류가 발생했습니다.다시 수정해주세요');";
	  message += " location.href='"+multipartRequest.getContextPath()+"/board/viewArticle.do?articleNO="+articleNO+"';";
	  message +=" </script>";
	      resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
	    }
	    return resEnt;
	  }

  @Override
  @RequestMapping(value="/board/removeArticle.do" ,method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity  removeArticle(@RequestParam("articleNO") int articleNO,
                              HttpServletRequest request, HttpServletResponse response) throws Exception{
	response.setContentType("text/html; charset=UTF-8");
	String message;
	ResponseEntity resEnt=null;
	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.add("Content-Type", "text/html; charset=utf-8");
	try {
		boardService.removeArticle(articleNO);
		File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
		FileUtils.deleteDirectory(destDir);
		
		message = "<script>";
		message += " alert('글을 삭제했습니다.');";
		message += " location.href='"+request.getContextPath()+"/board/listArticles.do';";
		message +=" </script>";
	    resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
	       
	}catch(Exception e) {
		message = "<script>";
		message += " alert('작업중 오류가 발생했습니다.다시 시도해 주세요.');";
		message += " location.href='"+request.getContextPath()+"/board/listArticles.do';";
		message +=" </script>";
	    resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
	    e.printStackTrace();
	}
	return resEnt;
  }  
}
