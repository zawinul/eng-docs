package it.eng.ms.camundademo.oidc.config;


//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AltroController {
    //private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/altro")
    @ResponseBody
    public final String home() {
    	String username = "";
        //username = SecurityContextHolder.getContext().getAuthentication().getName();
        return "Welcome to altro controller" + username;
    } 

}
