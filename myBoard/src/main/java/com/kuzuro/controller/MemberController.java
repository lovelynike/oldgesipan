package com.kuzuro.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kuzuro.domain.MemberVO;
import com.kuzuro.service.MemberService;

@Controller
@RequestMapping("/member/*")
public class MemberController {

private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Inject
	MemberService service;
	
	@Autowired
	BCryptPasswordEncoder passEncoder;
	
	// 회원 가입 get
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void getRegister() throws Exception {
		logger.info("get register");
	}

	// 회원 가입 post
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String postRegister(MemberVO vo) throws Exception {
		logger.info("post resister");
		
		String inputPass = vo.getUserPass();
		String pass = passEncoder.encode(inputPass);
		vo.setUserPass(pass);
		
		service.register(vo);
		
		return "redirect:/";
	}
	
	// 로그인
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(MemberVO vo, HttpServletRequest req, RedirectAttributes rttr) throws Exception {
		logger.info("post login");

		HttpSession session = req.getSession();
		MemberVO login = service.login(vo);

		boolean passMatch = passEncoder.matches(vo.getUserPass(), login.getUserPass());

		if (login != null && passMatch) {

			session.setAttribute("member", login);

		} else {
			session.setAttribute("member", null);
			rttr.addFlashAttribute("msg", false);
		}

		/*
		 * if(login == null) { session.setAttribute("member", null);
		 * rttr.addFlashAttribute("msg", false); } else { session.setAttribute("member",
		 * login); }
		 */

		return "redirect:/";
	}
		
	// 로그아웃
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) throws Exception {
		logger.info("get logout");
		
		session.invalidate();
				
		return "redirect:/";
	}
	
	// 회원정보 수정 get
	@RequestMapping(value = "/modify", method = RequestMethod.GET)
	public void getModify() throws Exception {
		logger.info("get modify");
	}

// 회원정보 수정 post
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String postModify(HttpSession session, MemberVO vo) throws Exception {
		logger.info("post modify");

		service.modify(vo);

		session.invalidate();

		return "redirect:/";
	}

// 회원 탈퇴 get
	@RequestMapping(value = "/withdrawal", method = RequestMethod.GET)
	public void getWithdrawal() throws Exception {
		logger.info("get withdrawal");

	}

// 회원 탈퇴 post
	@RequestMapping(value = "/withdrawal", method = RequestMethod.POST)
	public String postWithdrawal(HttpSession session, MemberVO vo, RedirectAttributes rttr) throws Exception {
		logger.info("post withdrawal");

		MemberVO member = (MemberVO) session.getAttribute("member");

		String oldPass = member.getUserPass();
		String newPass = vo.getUserPass();

		/*
		 * 더블 이퀄(==)로 비교할 땐 변수가 사용하는 주소를 비교하는것이며, 위의 코드처럼 .equals() 을 사용하면 변수가 가진 값 자체를
		 * 비교하게 됩니다.
		 */
		if (!(oldPass.equals(newPass))) {
			rttr.addFlashAttribute("msg", false);
			return "redirect:/member/withdrawal";
		}

		service.withdrawal(vo);

		session.invalidate();

		return "redirect:/";
	}
	
	// 회원 확인
	//스프링에서 비동기 처리를 하는 경우 @RequestBody 와 @ReponseBody 를 사용
	@ResponseBody
	@RequestMapping(value = "/idCheck", method = RequestMethod.POST)
	public int postIdCheck(HttpServletRequest req) throws Exception{
		logger.info("post idCheck");
		
		String userId = req.getParameter("userId");
		MemberVO idCheck = service.idCheck(userId);
		
		int result = 0;
		
		if(idCheck != null) {
			result = 1;
		}
		
		return result;
	}
		
}