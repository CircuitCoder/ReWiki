package tk.ccoder.lab.ReWiki.data;

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
  private String title;

  @Field
  private EntryLang lang;

  public EntryLang getLang() {
    return lang;
  }

  public void setRevision(Revision rev) {
    this.current = rev;
  }
}
