package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import valerie.myservices.ESService;

import java.io.IOException;

@Controller
public class ESController {
    @Autowired
    private ESService esService;

    public ESController() {}

    @RequestMapping("/searchES")
    public String searchES(Model model) throws IOException {
        String queryCollection = "movietags";
        String data = this.esService.search(queryCollection);
        model.addAttribute("ESdata", data);
        return "/searchES";
    }

}
