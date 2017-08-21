package tk.ccoder.lab.ReWiki.data;

/**
 * Created by CircuitCoder on 4/29/16.
 */
public enum EntryLang {
  en("English Int.","en"), cn("简体中文","zh_CN"), tw("正體中文","zh_TW");

  private String name;
  private String fullname;

  EntryLang(String name, String fullname) {
    this.name = name;
    this.fullname = fullname;
  }

  public String getName() {
    return name;
  }

  public String getFullName() {
    return fullname;
  }
}
