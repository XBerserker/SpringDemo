package knights;

import java.io.PrintStream;

/**
 * Created by faker on 2017/10/20.
 */
public class Minstrel {
    private PrintStream stream;
    public Minstrel(PrintStream stream){
        this.stream=stream;
    }

    public void singBeforeQuest(){
        stream.println("Fa la fa la, the knight is so brave!"); //打怪前加BUFF
    }
    public void singAfterQuest(){
        stream.println("Tee tee tee, the brave knight did embark on a quest!"); //打完怪回血
    }
}
