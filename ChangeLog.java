/**
 * Created by stas on 05.05.17.
 */
public class ChangeLog {
    private int  NUM_OF_MATCHES;
    private int  NUM_OF_FILES;
    private int  NUM_OF_DIRS;
    private int  NUM_OF_FILES_EXTENSION;
    String extension;

    ChangeLog(String ext){
        NUM_OF_DIRS=0;
        NUM_OF_FILES=0;
        NUM_OF_MATCHES=0;
        NUM_OF_FILES_EXTENSION=0;
        extension = ext;
    }

    void addMatches(int i){
        NUM_OF_MATCHES += i;
    }

    void newFile(){
        NUM_OF_FILES++;
    }

    void newFileExt(){
        NUM_OF_FILES_EXTENSION++;
    }

    void newDir(){
        NUM_OF_DIRS++;
    }

    String getString(){
        return "ByteChanger found and change "+ NUM_OF_MATCHES +" byte sequences \n" +
                "In "+ NUM_OF_DIRS + " directories.\n" +
                "In "+ NUM_OF_FILES + " files \n" +
                "In "+ NUM_OF_FILES_EXTENSION + " files with " + extension + " extension\n";
    }
}
