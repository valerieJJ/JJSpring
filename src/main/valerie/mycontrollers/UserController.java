package valerie.mycontrollers;

import java.net.UnknownHostException;
import com.mongodb.DBObject;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
//import valerie.mycontrollers.MongodbService;
import valerie.myModel.User;
import valerie.myModel.requests.LoginUserRequest;
import valerie.myModel.requests.RegisterUserRequest;
import valerie.myModel.requests.UserRequest;
import valerie.myservices.UserService;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private UserService userservice;

    public UserController() throws UnknownHostException {
    }

    @RequestMapping("/show")
    public String getData(Model model,HttpServletRequest request) throws UnknownHostException {
        DBObject data = userservice.getCollectionData();
        model.addAttribute("coll",data.toString());

        HttpSession session1 = request.getSession();
        User usr = (User)session1.getAttribute("user");
        if(usr == null){
            usr = userservice.getDefaultUser();
        }
        model.addAttribute("user", usr);

        System.out.println("show - port:"+request.getServerPort()+",session:"+session1.getId());
        System.out.println("show - get user from request.session" + usr.getUsername());
        return "show";
    }

    @RequestMapping("/homePage")
    public String goHomePage(Model model, HttpServletRequest request) throws UnknownHostException {
        User user = (User) model.getAttribute("user");
        System.out.println("HomePage" + user.getUsername());
        HttpSession session1 = request.getSession();
        System.out.println("homepage - port:"+request.getServerPort()+",session:"+session1.getId());
        return "homePage";
    }

    @RequestMapping("/whoisit")
    public String getNickname(Model model, HttpServletRequest request) throws UnknownHostException {

        User user = userservice.getDefaultUser();
        model.addAttribute("user", user);

        HttpSession session1 = request.getSession();
        System.out.println("whoisit - port:"+request.getServerPort()+",session:"+session1.getId());
        return "whoisit";
    }

    /****************************  Login  **************************/

    @RequestMapping("/login")
    public String login(Model model) throws UnknownHostException {
//        User user = userservice.getDefaultUser();
//        model.addAttribute("user", user);
        return "login";
    }

    @RequestMapping("/dologin") //@RequestMapping("/user/dologin")
    public String login(@ModelAttribute("user") User user, Model model,HttpServletRequest request) throws UnknownHostException {
        User newUsr = userservice.loginUser(new LoginUserRequest(user.getUsername(),user.getPassword()));
        if(newUsr==null){
            System.out.println("Account does not exist");
            return "login";
        }else {
            System.out.println("\nGet username="+newUsr.getUsername());
            System.out.println("Get password="+newUsr.getPassword());
            model.addAttribute("user", newUsr);

            HttpSession session = request.getSession();
            session.setAttribute("user", newUsr);
            System.out.println("login - port:"+request.getServerPort()+",session:"+session.getId());

            return "mainIndex";
        }
    }

    /****************************  Log out  **************************/
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        // 将用户信息从session中删除
        session.removeAttribute("user");
        Object info = session.getAttribute("user");
        if(info==null){
            System.out.println("logout success");
        }else{
            System.out.println("failed to logout");
        }
        return "index";
    }

    /****************************  Register  **************************/
    @RequestMapping("/register")
    public String register(){
        return "register";
    }
    @RequestMapping("/doregister")
    public String register(Model model, HttpServletRequest request){
        String name = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("username is " + name);
        System.out.println("password is " + password);
//        User user = userservice.getUser(new RegisterUserRequest(name, password));
        if(userservice.checkUserExist(name)){
            model.addAttribute("success",false);
            model.addAttribute("message"," 用户名已经被注册！");
            return "register";
        }
        model.addAttribute("success", true);

        User user = userservice.registerUser(new RegisterUserRequest(name,password));
        model.addAttribute("user",user);
        return "mainIndex";
    }

    @RequestMapping("/account")
    public ModelAndView accountPage(Model model, HttpServletRequest request){//, @ModelAttribute("user") User user

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println("account action: sessionID" + session.getId());
        System.out.println("account action: user " + session.getAttribute("user"));

        ModelAndView mv = new ModelAndView();
        if(user==null){
            mv.setViewName("index");
//            mv.setViewName("dologin");
            System.out.println("please log in first");
        }else{
            mv.addObject("user", user);
            mv.setViewName("homePage");
        }

        return mv;
    }

    @RequestMapping("/goIndex")
    public ModelAndView goIndex(HttpServletRequest request){
        HttpSession session = request.getSession();
        ModelAndView mv = new ModelAndView();
        if(session.getAttribute("user")==null){// || session.getAttribute("user")==null
            mv.setViewName("index");
        }else{
            mv.setViewName("mainIndex");
        }
        return mv;
    }

}
