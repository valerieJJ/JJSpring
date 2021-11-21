package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import valerie.myservices.ESService;

import java.io.IOException;

@Controller
public class ESController {
    @Autowired
    private ESService esService;

    public ESController() {}

    public void searchES() throws IOException {
        String queryCollection = "movietags";
        this.esService.search(queryCollection);
    }
}
