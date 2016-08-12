package co.samsao.reporter;

/**
 * Created by Vincent on 2016-08-10.
 */
public class Repository {
    String name;
    String lastUpdate;
    String language;
    String defaultBranch;
    int forksCount;

    public Repository(String name, String lastUpdate, String language, String defaultBranch, int forksCount){
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.language = language;
        this.defaultBranch = defaultBranch;
        this.forksCount = forksCount;
    }
}
