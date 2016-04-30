package tk.ccoder.lab.ReWiki.data;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CircuitCoder on 4/29/16.
 */

@Document
public class Revision {

  public static class Blame implements Cloneable {
    @Field
    public ObjectId ref;

    @Field
    public int start;

    @Field
    public int length;

    @DBRef
    public Account author;

    @Field
    public String comment;

    public Blame(ObjectId ref, int start, int length, Account author, String comment) {
      this.ref = ref;
      this.start = start;
      this.length = length;
      this.author = author;
      this.comment = comment;
    }

    @Override
    public Blame clone() {
      return new Blame(ref, start, length, author, comment);
    }
  }

  @Id
  private ObjectId id;

  @DBRef
  private Account author;

  @Field
  private String content;

  @Field
  private String comment;

  @Field
  private List<Blame> blame = new LinkedList<>();

  // Parent revision
  @DBRef(lazy = true)
  private Revision parent;

  // Merged from
  @DBRef(lazy = true)
  private Revision merge;

  /**
   * Create a normal revision
   * @param parent The parent revision. If this is a new entry, then send NULL
   * @param author The author
   * @param content The NEW content of the entry
   * @param comment The comment of this commit
   */
  public Revision(Revision parent, Account author, String content, String comment) {
    this.author = author;
    this.content = content;
    this.comment = comment;
    this.parent = parent;
    this.id = new ObjectId();

    // Produce blame
    if(parent != null) {
      List<String> original = Arrays.asList(parent.content.split("\\r?\\n"));
      List<String> revised = Arrays.asList(content.split("\\r?\\n"));

      Patch p = DiffUtils.diff(original, revised);

      // Clone
      blame = new LinkedList<>();
      blame.addAll(parent.blame.stream().map(Blame::clone).collect(Collectors.toList()));

      ListIterator<Delta> deltaIterator = p.getDeltas().listIterator();
      ListIterator<Blame> blameIterator = blame.listIterator();

      // Modify length
      while (deltaIterator.hasNext()) {
        Delta current = deltaIterator.next();

        int deltaBegin = current.getOriginal().getPosition();
        int deltaEnd = deltaBegin + current.getOriginal().size();

        if(!blameIterator.hasNext()) {
          blameIterator.add(new Blame(
                  this.id,
                  0, // Start will be modified later
                  current.getRevised().size(),
                  this.author,
                  this.comment
          ));
        }

        while (blameIterator.hasNext()) {
          Blame b = blameIterator.next();
          if (b.start + b.length <= deltaBegin) { } // Do nothing
          else if (b.start < deltaBegin) {
            if(b.start + b.length > deltaEnd) { // This blame covers current delta
              blameIterator.add(new Blame(
                      this.id,
                      0, // Start will be modified later
                      current.getRevised().size(),
                      this.author,
                      this.comment
              ));
              blameIterator.add(new Blame(b.ref, deltaEnd, b.start + b.length-deltaEnd, b.author, b.comment));

              b.length = deltaBegin - b.start;

              // The part of blame after current delta is still not processed
              blameIterator.previous();

              break;
            } else if(b.start + b.length == deltaEnd) {
              // This blame has the same tail position with current delta
              // And next blame has no relation with current delta
              // So it is responsible to push the new blame into blame array
              blameIterator.add(new Blame(
                      this.id,
                      0, // Start will be modified later
                      current.getRevised().size(),
                      this.author,
                      this.comment
              ));

              b.length = deltaBegin - b.start;
            } else { // This blame doesn't cover the entire delta
              b.length = deltaBegin - b.start;
            }
          } else if (b.start + b.length <= deltaEnd) {
            blameIterator.remove();
          } else { // Partially affected
            b.length = b.start + b.length - deltaEnd;

            // Insert blame
            blameIterator.previous();
            blameIterator.add(new Blame(
                    this.id,
                    0, // Start will be modified later
                    current.getRevised().size(),
                    this.author,
                    this.comment
            ));

            // Skip blame from current iteration
            blameIterator.next();

            break; // blameIterator
          }
        }
      }

      // Generate start
      blameIterator = blame.listIterator();

      int currentLine = 0;
      while(blameIterator.hasNext()) {
        Blame b = blameIterator.next();
        b.start = currentLine;
        currentLine += b.length;
      }

    } else { // parent == null
      blame = new LinkedList<>();
      blame.add(new Blame(
              this.id,
              0,
              content.split("\\r?\\n").length,
              this.author,
              this.comment
      ));
    }
  }

  /**
   * Merge two revisions into one
   * @param parent The parent revision
   * @param merge The revision to be merged into parent
   */
  public Revision(Revision parent, Revision merge) {
    throw new UnsupportedOperationException("Not Implemented");
  }

  public Account getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  public String getComment() {
    return comment;
  }

  public List<Blame> getBlame() {
    return blame;
  }

  public ObjectId getRef() {
    return id;
  }
}
