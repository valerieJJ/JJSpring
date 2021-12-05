package valerie.mycontrollers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MusicController {
    @RequestMapping("/music/mscdetails")
    public String getMscDetails(HttpServletRequest request, HttpServletResponse response, Model model){
//        String openid = request.getParameter("openid");
//        String openkey = request.getParameter("openkey");
//        System.out.println("msc-details: "+ openid+": "+openkey);
//
//        model.addAttribute("msg",openid);
        return "mscDetails";
    }

    @RequestMapping("/music/getmsc")
    public String getMsc(HttpServletRequest request, HttpServletResponse response, Model model){
        String mscID = request.getParameter("mscID");
//        String openkey = request.getParameter("openkey");
//        System.out.println(openid+": "+openkey);
//        model.addAttribute("msg",mscID);
        model.addAttribute("mscID", mscID);
//        model.addAttribute("openkey",openkey);
        return "forward:/music/mscplayer";
//        return "mscPlayer";
    }
    @RequestMapping("/music/mscplayer")
    public String gotoQQMsc(HttpServletRequest request, HttpServletResponse response, Model model){
        String mscID = request.getParameter("mscID");
//        String openkey = request.getParameter("openkey");
//        System.out.println(openid+": "+openkey);
//        model.addAttribute("msg",mscID);
        System.out.println("goto player:"+mscID);
        model.addAttribute("mscID", mscID);
        return "mscPlayer";
    }


}
