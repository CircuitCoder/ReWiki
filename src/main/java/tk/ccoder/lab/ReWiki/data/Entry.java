package tk.ccoder.lab.ReWiki.data;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by CircuitCoder on 4/29/16.
 */
public class Entry {
  @Id
  private ObjectId id;

  @Field
  private ObjectId current;

  @Field
  private ObjectId group;

  @Field
  @TextIndexed(weight = 2.0f)
  private String title;

  @Field
  @TextIndexed
  private String content;

  @Field
  private EntryLang lang;

  public Entry(EntryLang lang, String title) {
    this.lang = lang;
    this.title = title;
  }

  public EntryLang getLang() {
    return lang;
  }

  public void setRevision(Revision rev) {
    this.current = rev.getRef();
    this.content = rev.getContent();
  }

  public void setGroup(Entry base) {
    this.group = base.group;
  }

  public ObjectId getGroup() {
    return group;
  }

  public String getContent() {
    return content;
  }

  public String getTitle() {
    return title;
  }

  public ObjectId getCurrent() {
    return current;
  }
}
