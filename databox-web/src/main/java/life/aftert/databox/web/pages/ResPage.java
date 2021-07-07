package life.aftert.databox.web.pages;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ResPage {

    @GetMapping({"/","/index"})
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/pages/index.html");
        return mv;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/pages/login.html");
        return mv;
    }

}
