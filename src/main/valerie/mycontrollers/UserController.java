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

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private UserService userservice;

    public UserController() throws UnknownHostException {
    }

    @RequestMapping("/show")
    public String getData(Model model) throws UnknownHostException {
        DBObject data = userservice.getCollectionData();
        model.addAttribute("mydata",data.toString());
        return "show";
    }

    @RequestMapping("/homePage")
    public String goHomePage(Model model) throws UnknownHostException {
        User user = (User) model.getAttribute("user");
        System.out.println("HomePage" + user.getUsername());
        return "homePage";
    }

    @RequestMapping("/whoisit")
    public String getNickname(Model model) throws UnknownHostException {
        User user = userservice.getDefaultUser();
        model.addAttribute("user", user);
        return "whoisit";
    }

    /****************************  Login  **************************/

    @RequestMapping("/user/login")
    public String login(Model model) throws UnknownHostException {
        User user = userservice.getDefaultUser();
        model.addAttribute("user", user);
        return "login";
    }

    @RequestMapping("/user/dologin")
    public String login(@ModelAttribute("user") User user, Model model) throws UnknownHostException {
        User newUsr = userservice.loginUser(new LoginUserRequest(user.getUsername(),user.getPassword()));
        if(newUsr==null){
            System.out.println("Account does not exist");
            return "login";
        }else {
            System.out.println("\nGet username="+newUsr.getUsername());
            System.out.println("Get password="+newUsr.getPassword());
            model.addAttribute("user", newUsr);
            return "index";
        }
    }

    /****************************  Register  **************************/
    @RequestMapping("/user/register")
    public String register(){
        return "register";
    }
    @RequestMapping("/user/doregister")
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
//        model.addAttribute("username", name);
//        model.addAttribute("password", password);
        model.addAttribute("user",user);
        return "index";
    }

    @RequestMapping("/user/account")
    public String accountPage(Model model){
        User user = (User) model.getAttribute("user");
        System.out.println("HomePage" + user.getUsername());
        return "homePage";
    }

    //

//    @RequestMapping("/login")
//    public String login(){
//        return "login";
//    }
//    @RequestMapping("/dologin")
//    public String login(Model model, HttpServletRequest request){
//        String name = request.getParameter("username");
//        String password = request.getParameter("password");
//        System.out.println("username is " + name);
//        System.out.println("password is " + password);
//
//        User user = userservice.loginUser(new LoginUserRequest(name,password));
//
//        if(user==null){
//            System.out.println("User does not exist!");
//            model.addAttribute("success",false);
//            model.addAttribute("message","用户不存在或密码错误");
//            return "register";
//        }else{
//            System.out.println("Successfully login!");
//            model.addAttribute("user", user);
//            model.addAttribute("success", true);
//            return "homePage";
//        }
//    }

}
