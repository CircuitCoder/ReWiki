package tk.ccoder.lab.ReWiki.data;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by CircuitCoder on 4/29/16.
 */

@Document
public class EntryGroup {

  private static class EntrySpec {
    private EntryLang lang;

    @DBRef
    private Entry ref;

    private EntrySpec(EntryLang lang, Entry e) {
      this.lang = lang;
      this.ref = e;
    }

    @Override
    public boolean equals(Object a) {
      if(a instanceof EntrySpec) {
        EntrySpec g = (EntrySpec) a;
        return g.lang == this.lang;
      } else return false;
    }
  }

  @Field
  private int count;

  @Field
  private Set<EntrySpec> entries = new HashSet<>();

  public EntryGroup() {
    this.count = 0;
  }

  public boolean pushEntry(Entry e) {
    ++count;
    return entries.add(new EntrySpec(e.getLang(), e));
  }

  public Set<EntrySpec> getEntrieSet() {
    return entries;
  }
}
