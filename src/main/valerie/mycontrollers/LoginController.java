package valerie.mycontrollers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
@Controller
public class LoginController {

//    public String goLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session){
//        model.addAttribute("movie","Moon Walker");
//        return "jjLogin";
//    }

    @RequestMapping("/jj/goIndex")
//
    public String goIndex(Model model){
        model.addAttribute("movie","Moon Walker");
        return "index";
    }
//
    @RequestMapping("jj/loging")
    public String login(){
        return "loginPage";
    }
@RequestMapping("/jj/goLogin")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model, HttpSession session
    ){
        if(StringUtils.hasText(username) && "111111".equals(password)){
            session.setAttribute("loginUser", username);
            System.out.println("right");
            return "index";
        }

//            System.out.println("right");
            model.addAttribute("msg","login failed");
            return "login";

    }
}


