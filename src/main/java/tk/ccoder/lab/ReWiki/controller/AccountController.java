package tk.ccoder.lab.ReWiki.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.ccoder.lab.ReWiki.data.Account;

/**
 * Created by CircuitCoder on 5/1/16.
 */

@Controller
public class AccountController {
  @RequestMapping(value = "/account/login", method = RequestMethod.GET)
  public String login(@AuthenticationPrincipal Account account) {
    System.out.println("Here");
    if(account != null) return "redirect:/account/profile";
    else return "account/login";
  }

  @RequestMapping(value = "/account/profile", method = RequestMethod.GET)
  public String prifle(@AuthenticationPrincipal Account account, Model model) {
    model.addAttribute("account", account);
    model.addAttribute("isSelf", true);
    return "account/profile";
  }
}
