package tk.ccoder.lab.ReWiki.controller;

import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tk.ccoder.lab.ReWiki.data.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by CircuitCoder on 5/1/16.
 */

@Controller
public class EntryController {

  public static class Commit {
    public String content;
    public String parent;
    public String comment;

    public Commit(String content, String parent, String comment) {
      this.content = content;
      this.parent = parent;
      this.comment = comment;
    }
  }

  @Autowired
  private MongoTemplate mongo;

  @RequestMapping(value = "/{lang}/{title}", method = RequestMethod.GET, produces = "text/html")
  public String display(
          @PathVariable EntryLang lang,
          @PathVariable String title,
          @RequestParam(value = "rev", required = false) Revision rev,
          Model model) {

    //TODO: rev

    if(title.matches("[ \t]+")) {
      String replaced = title.replaceAll("[ \t]+", "_");
      return "redirect:/" + lang.toString() + "/" + replaced;
    }

    String replaced = title.replace('_', ' ');

    model.addAttribute("title", replaced);
    model.addAttribute("lang", lang);

    PegDownProcessor processor = new PegDownProcessor();

    Entry e = mongo.findOne(new Query(Criteria.where("lang").is(lang).and("title").is(replaced)), Entry.class);

    List<Entry> group;
    if(e.getGroup() != null) group = mongo.find(new Query(Criteria.where("group").is(e.getGroup())), Entry.class);
    else group = Arrays.asList(e);

    if(e == null) return "entry/404";

    String cont = e.getContent();
    String html = processor.markdownToHtml(cont);
    String safeHtml = Jsoup.clean(html, Whitelist.relaxed());

    model.addAttribute("content", safeHtml);
    model.addAttribute("group", group);

    return "entry/display";
  }

  @RequestMapping(value = "/{lang}/{title}", method = RequestMethod.POST)
  public String commit(
          @PathVariable EntryLang lang,
          @PathVariable String title,
          Commit commit,
          @AuthenticationPrincipal Account author) {
    String replaced = title.replace('_', ' ');

    Entry e = mongo.findOne(new Query(Criteria.where("lang").is(lang).and("title").is(replaced)), Entry.class);
    Revision parent = mongo.findById(new ObjectId(commit.parent), Revision.class);
    if(e == null || e != parent.getEntry()) return "entry/failed";

    Revision current = new Revision(parent, e, author, commit.content, commit.comment);
    mongo.insert(current);

    if(e.getCurrent() == parent.getRef()) {
      e.setRevision(current);
      mongo.save(e);
      return String.format("redirect:/%s/%s?msg=success", e.getLang().toString(), e.getTitle());
    } else {
      return String.format("redirect:/%s/%s?rev=%s&msg=conflict", e.getLang().toString(), e.getTitle(), current.getRef());
    }
  }

  @RequestMapping(value = "/{lang}/{title}", method = RequestMethod.PUT)
  public String create(
          @PathVariable("lang") EntryLang lang,
          @PathVariable("title") String title,
          @RequestParam(name = "root", required = false) Entry root,
          @AuthenticationPrincipal Account author) {
    String replaced = title.replace('_', ' ');

    Entry same = mongo.findOne(new Query(Criteria.where("lang").is(lang).and("title").is(replaced)), Entry.class);
    if(same != null) return "redirect:/" + same.getLang().toString() + "/" + same.getTitle();

    if(root != null) {
      Entry corr = mongo.findOne(new Query(Criteria.where("group").is(root.getGroup()).and("lang").is(lang)), Entry.class);
      if(corr != null)
        return "redirect:/" + corr.getLang().toString() + "/" + corr.getTitle(); // TODO: notification
      else {
        Entry curr = new Entry(lang,replaced);
        curr.setGroup(root);
        Revision init = new Revision(null, curr, author, root.getContent(), "Creation");
        mongo.insert(init);

        curr.setRevision(init);
        mongo.insert(curr);
        mongo.save(init); // To update dbref
      }
    } else {
      Entry curr = new Entry(lang, replaced);
      Revision init = new Revision(null, curr, author, "", "Creation");
      mongo.insert(init);

      curr.setRevision(init);
      mongo.insert(curr);
      mongo.save(init);
    }

    return "redirect:/" + lang.toString() + "/" + title.replace(' ','_') + "/edit";
  }

  @RequestMapping(value = "/{lang}/{title}/edit", method = RequestMethod.GET)
  public String edit(
          @PathVariable EntryLang lang,
          @PathVariable String title,
          @RequestParam(required = false) Revision parent,
          Model model) {
    String replaced = title.replace('_', ' ');
    Entry e = mongo.findOne(new Query(Criteria.where("lang").is(lang).and("title").is(replaced)), Entry.class);
    if(e == null) return "redirect:/" + lang.toString() + "/" + title + "/create";
    else if(parent != null && parent.getEntry() != e) return "entry/failed";
    else {
      if(parent != null) model.addAttribute("commit", new Commit(parent.getContent(),parent.getRef().toString(),""));
      else {
        Revision r = mongo.findById(e.getCurrent(), Revision.class);
        model.addAttribute("commit", new Commit(r.getContent(), r.getRef().toString(), ""));
      }

      return "entry/edit";
    }
  }

  @RequestMapping(value = "/{lang}/{title}/create", method = RequestMethod.GET)
  public String create(
          @PathVariable EntryLang lang,
          @PathVariable String title,
          Model model) {
    return "entry/create";
  }
}
