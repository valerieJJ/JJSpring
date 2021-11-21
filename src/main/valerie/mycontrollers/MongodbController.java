package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import valerie.myservices.MongodbService;

import java.net.UnknownHostException;

//@Controller
public class MongodbController {
    @Autowired
    private MongodbService mongoservice;

    public MongodbController() throws UnknownHostException {
    }

//    @ResponseBody
    @RequestMapping("/showdb")
    public String getData(Model model) throws UnknownHostException {
        String data = mongoservice.getData();
        model.addAttribute("mydbdata", data);
        return "showdb";
    }
}

