/* Stas Karpov
   ReplacingInputStream uploading bytes from file, comparing it with search[] and change it with replacment[] if the are equal
 */


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;



class ReplacingInputStream {

    LinkedList<Integer> inQueue = new LinkedList<Integer>();
    LinkedList<Integer> outQueue = new LinkedList<Integer>();
    final byte[] buf,search, replacement;
    int NUM_OF_MATCHES;
    int pos;
    int count;

    protected ReplacingInputStream(File in, byte[] search,
                                            byte[] replacement) throws IOException {

        buf = Files.readAllBytes(Paths.get(in.getPath()));
        this.search = search;
        this.replacement = replacement;
        pos = 0;
        count = buf.length;
        NUM_OF_MATCHES = 0;

    }

    private boolean isMatchFound() {
        Iterator<Integer> inIter = inQueue.iterator();
        for (int i = 0; i < search.length; i++)
            if (!inIter.hasNext() || search[i] != inIter.next())
                return false;
        ++NUM_OF_MATCHES;
        System.out.print("Match is Found ! \n");
        return true;
    }

    private boolean isEndOfBuf(){
        return (count == pos);
    }

    public boolean isEnd(){
        return (isEndOfBuf() && outQueue.isEmpty() && inQueue.isEmpty() );
    }



    private void readAhead() throws IOException  {
        while (inQueue.size() < search.length) {
            if (isEndOfBuf())
                break;

            int next = buf[pos++] ;//overrided super.read function
                                                                                     //without sign-bite deleting
            inQueue.offer(next);
        }
    }


    public int read() {

        // Next byte already determined.
        if (outQueue.isEmpty()) {
            try{
                readAhead();
            }catch(IOException e){
                e.printStackTrace();
            }

            if (isMatchFound()) {
                for (int i = 0; i < search.length; i++)
                    inQueue.remove();

                for (byte b : replacement)
                    outQueue.offer((int) b);
            } else
                outQueue.add(inQueue.remove());
        }

        return outQueue.remove();
    }

    int readNumOfMatches(){
        int n = NUM_OF_MATCHES;
        NUM_OF_MATCHES = 0;
        return n;
    }


}
