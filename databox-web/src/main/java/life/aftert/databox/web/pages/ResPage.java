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

//    @GetMapping({"/","/index"})
//    public ModelAndView index() {
//
//        ModelAndView mv = new ModelAndView();
//        mv.addObject("title","数据尚云-首页");
//        mv.setViewName("/pages/index.html");
//        return mv;
//    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("title","数据尚云-登录");
        mv.setViewName("/pages/login.html");
        return mv;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("title","数据尚云-后台");
        mv.setViewName("/pages/dashboard.html");
        return mv;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("title","数据尚云-注册");
        mv.setViewName("/pages/register.html");
        return mv;
    }

}
