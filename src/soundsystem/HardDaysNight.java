package soundsystem;

public class HardDaysNight implements CompactDisc {

    private String title = "HardDaysNight";
    private String artist = "The Beatles";

    @Override
    public void play() {
        System.out.println("Playing "+title+" by "+artist);
    }
}
