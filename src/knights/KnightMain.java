package knights;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by faker on 2017/10/20.
 */
public class KnightMain {

    public static void main(String[] args)throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring/knight.xml");
        Knight knight = context.getBean(Knight.class);
        knight.embarkOnQuest();
        context.close();
    }

}
