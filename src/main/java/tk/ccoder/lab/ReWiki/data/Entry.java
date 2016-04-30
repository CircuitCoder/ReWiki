package tk.ccoder.lab.ReWiki.data;

import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by CircuitCoder on 4/29/16.
 */
public class Entry {
  @DBRef(lazy = true)
  private Revision current;

  @DBRef(lazy = true)
  private EntryGroup group;

  @Field
  @TextIndexed(weight = 2.0f)
  private String title;

  @Field
  @TextIndexed
  private String content;

  @Field
  private EntryLang lang;

  public EntryLang getLang() {
    return lang;
  }

  public void setRevision(Revision rev) {
    this.current = rev;
    this.content = rev.getContent();
  }
}
