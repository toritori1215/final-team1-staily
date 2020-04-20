package com.itwill.staily.login.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.itwill.staily.admin.service.NaverLogin;
import com.itwill.staily.login.exception.NoExistedIdException;
import com.itwill.staily.login.exception.NoSearchMemberException;
import com.itwill.staily.login.exception.PasswordMissmatchException;
import com.itwill.staily.login.service.LoginService;
import com.itwill.staily.login.service.NaverLogin2;
import com.itwill.staily.util.Company;
import com.itwill.staily.util.Member;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	
	public LoginController() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(value = "/login")
	public String login() {
		return "login/login";
	}
	
	
	@RequestMapping(value = "/login_action", method = RequestMethod.GET)
	public String login_action_get() {
		return "login/login";
	}
	
	@RequestMapping(value = "/login_action", method = RequestMethod.POST)
	public String login_action_post(@RequestParam String userId, String userPw, HttpSession session, Model model) {
		String forwardPath = "";
		Member member = new Member();
		Member successMember;
		member.setmId(userId);
		member.setmPw(userPw);
		
		try {
			successMember = loginService.login(member);
			System.out.println(successMember);
			session.setAttribute("userId", successMember.getmId());
			session.setAttribute("userNo", successMember.getmNo());
			session.setAttribute("userType", successMember.getmType());
			
			forwardPath = "redirect:/main/index";
		} catch (NoExistedIdException e) {
				e.printStackTrace();
				model.addAttribute("msg", e.getMessage());
				forwardPath = "login/login";
		} catch (PasswordMissmatchException e) {
			e.printStackTrace();
			model.addAttribute("msg", e.getMessage());
			forwardPath = "login/login";
		} catch (Exception e) {
			e.printStackTrace();
			forwardPath = "redirect:/404.jsp";
		}
		return forwardPath;
	}
	
	@RequestMapping(value = "/logout_action")
	public String logout_action(HttpSession session) {
		session.invalidate();
		return "redirect:/main/index";
	}
	
	@RequestMapping(value = "/id_read")
	public String id_read() {
		return "login/id_read";
	}
	
	@RequestMapping(value = "/id_read_action", method = RequestMethod.GET)
	public String id_read_action_get() {
		return "login/id_read";
	}
	
	@RequestMapping(value = "/id_read_action", method = RequestMethod.POST)
	public String find_id_action_post(@RequestParam String name, String phone, Model model) {
		String forwardPath = "";
		String findId = "";
		try {
			findId = loginService.findId(phone, name);
			model.addAttribute("findId", findId);
			
			forwardPath = "login/login";
		} catch(NoSearchMemberException e) {
			e.printStackTrace();
			model.addAttribute("msg", e.getMessage());
			forwardPath = "login/id_read";
		} catch (Exception e) {
			e.printStackTrace();
			forwardPath = "redirect:/404.jsp";
		}
		return forwardPath;
	}
	
	@RequestMapping(value = "/pw_count_read")
	public String pw_read() {
		return "login/pw_count_read";
	}
	
	@RequestMapping(value = "/pw_count_read_action", method = RequestMethod.GET)
	public String pw_count_read_action_get() {
		return "login/pw_count_read";
	}
	
	@RequestMapping(value = "/pw_count_read_action", method = RequestMethod.POST)
	public String pw_count_read_action_post(@RequestParam String id, String phone, Model model) {
		String forwardPath = "";
		
		try {
			loginService.isIdExistForPw(id, phone);
			model.addAttribute("id", id);
			forwardPath = "login/pw_update";
		}catch(NoSearchMemberException e) {
			e.printStackTrace();
			model.addAttribute("msg", e.getMessage());
			forwardPath = "login/pw_count_read";
		}catch(Exception e) {
			e.printStackTrace();
			forwardPath = "redirect:/404.jsp";
		}
		return forwardPath;
	}
	
	@RequestMapping(value = "/pw_update", method = RequestMethod.GET)
	public String update_pw_get() {
		return "login/pw_count_read";
	}
	
	@RequestMapping(value = "/pw_update", method = RequestMethod.POST)
	public String update_pw_post(@RequestParam String id, String pw) {
		String forwardPath = "";
		Member updateMember = new Member();
		updateMember.setmId(id);
		updateMember.setmPw(pw);
		
		try {
			loginService.updatePw(updateMember);
			forwardPath = "login/login";
		} catch(Exception e) {
			e.printStackTrace();
			forwardPath = "redirect:/404.jsp";
		}
		return forwardPath;
	}
	
	
	@RequestMapping(value = "/member_create")
	public String member_create() {
		return "login/member_create";
	}
	
	@RequestMapping(value = "/member_create_action", method = RequestMethod.GET)
	public String member_create_action_get() {
		return "login/member_create";
	}
	
	@RequestMapping(value = "/member_create_action", method = RequestMethod.POST)
	public String member_create_action_post(@ModelAttribute Member signupMember, String coNo, Model model) {
		String forwardPath = "";
		
		try {
			if(signupMember.getmType().equals("M")) {
				loginService.signUpMember(signupMember);
			}else if(signupMember.getmType().equals("C")) {
				Company com = new Company();
				int intCoNo = Integer.parseInt(coNo);
				com.setCoNo(intCoNo);
				
				signupMember.setmCompany(com);
				loginService.signUpCompany(signupMember);
			}
			forwardPath = "login/login";
		}catch(Exception e) {
			e.printStackTrace();
			forwardPath = "redirect:/404.jsp";
		}
		return forwardPath; 
	}
	
	/***************************************
	 * Naver
	 *****************************************/
	/* NaverLogin */
	private NaverLogin2 naverLogin2;
	private String apiResult = null;

	@Autowired
	private void setNaverLogin(NaverLogin2 naverLogin2) {
		this.naverLogin2 = naverLogin2;
	}

//로그인 첫 화면 요청 메소드
	@RequestMapping(value = "/naver", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(Model model, HttpSession session) {
		/* 네이버아이디로 인증 URL을 생성하기 위하여 naverLogin클래스의 getAuthorizationUrl메소드 호출 */
		String naverAuthUrl = naverLogin2.getAuthorizationUrl(session);
//https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=sE***************&
//redirect_uri=http%3A%2F%2F211.63.89.90%3A8090%2Flogin_project%2Fcallback&state=e68c269c-5ba9-4c31-85da-54c16c658125
		System.out.println("네이버:" + naverAuthUrl);
//네이버
		model.addAttribute("url", naverAuthUrl);
		return "login/Naver_login2";
	}

//네이버 로그인 성공시 callback호출 메소드
	@RequestMapping(value = "/callback", method = { RequestMethod.GET, RequestMethod.POST })
	public String callback(Model model, @RequestParam String code, @RequestParam String state, HttpSession session)
			throws IOException, ParseException {
		System.out.println("여기는 callback");
		OAuth2AccessToken oauthToken;
		oauthToken = naverLogin2.getAccessToken(session, code, state);
//1. 로그인 사용자 정보를 읽어온다.
		apiResult = naverLogin2.getUserProfile(oauthToken); // String형식의 json데이터
		/**
		 * apiResult json 구조 {"resultcode":"00", "message":"success",
		 * "response":{"id":"33666449","nickname":"shinn****","age":"20-29","gender":"M","email":"sh@naver.com","name":"\uc2e0\ubc94\ud638"}}
		 **/
//2. String형식인 apiResult를 json형태로 바꿈
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(apiResult);
		JSONObject jsonObj = (JSONObject) obj;
//3. 데이터 파싱
//Top레벨 단계 _response 파싱
		JSONObject response_obj = (JSONObject) jsonObj.get("response");
//response의 nickname값 파싱
		String nickname = (String) response_obj.get("nickname");
		System.out.println(nickname);
//4.파싱 닉네임 세션으로 저장
		session.setAttribute("sessionId", nickname); // 세션 생성
		model.addAttribute("result", apiResult);
		return "login";
	}

//로그아웃
	@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
	public String logout(HttpSession session) throws IOException {
		System.out.println("여기는 logout");
		session.invalidate();
		return "redirect:index.jsp";
	}
	
	
	
	
	
}  
